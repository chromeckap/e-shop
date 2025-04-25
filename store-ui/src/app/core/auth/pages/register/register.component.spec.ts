import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RegisterComponent } from './register.component';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { AuthService } from '../../../../services/services/auth.service';
import { ToastService } from '../../../../shared/services/toast.service';
import { ActivatedRoute, Router } from '@angular/router';
import { By } from '@angular/platform-browser';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('RegisterComponent', () => {
    let component: RegisterComponent;
    let fixture: ComponentFixture<RegisterComponent>;
    let authServiceMock: jasmine.SpyObj<AuthService>;
    let toastServiceMock: jasmine.SpyObj<ToastService>;
    let routerMock: jasmine.SpyObj<Router>;
    let consoleLogSpy: jasmine.Spy;

    beforeEach(async () => {
        // Create spy objects for the services
        authServiceMock = jasmine.createSpyObj('AuthService', ['register']);
        toastServiceMock = jasmine.createSpyObj('ToastService',
            ['showSuccessToast', 'showErrorToast']);
        routerMock = jasmine.createSpyObj('Router', ['navigate']);

        // Spy on console.log
        consoleLogSpy = spyOn(console, 'log');

        // Default return values
        authServiceMock.register.and.returnValue(of());
        toastServiceMock.showSuccessToast.and.resolveTo();
        toastServiceMock.showErrorToast.and.resolveTo();
        routerMock.navigate.and.resolveTo(true);

        await TestBed.configureTestingModule({
            imports: [
                RegisterComponent,
                ReactiveFormsModule,
            ],
            providers: [
                FormBuilder,
                { provide: AuthService, useValue: authServiceMock },
                { provide: ToastService, useValue: toastServiceMock },
                {
                    provide: ActivatedRoute,
                    useValue: {
                        snapshot: {
                            paramMap: new Map(),
                            queryParamMap: new Map()
                        }
                    }
                }
            ],
            schemas: [NO_ERRORS_SCHEMA] // To ignore primeng component errors
        })
            .compileComponents();

        fixture = TestBed.createComponent(RegisterComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should initialize the form with empty fields', () => {
        expect(component.form.get('firstName')?.value).toBe('');
        expect(component.form.get('lastName')?.value).toBe('');
        expect(component.form.get('email')?.value).toBe('');
        expect(component.form.get('password')?.value).toBe('');
        expect(component.form.get('confirmPassword')?.value).toBe('');
    });

    it('should mark form as invalid when empty', () => {
        expect(component.form.valid).toBeFalsy();
    });

    describe('Form validation', () => {
        it('should validate firstName field', () => {
            const firstNameControl = component.form.get('firstName');

            // Too short
            firstNameControl?.setValue('A');
            expect(firstNameControl?.valid).toBeFalsy();
            expect(firstNameControl?.hasError('minlength')).toBeTruthy();

            // Valid length
            firstNameControl?.setValue('John');
            expect(firstNameControl?.valid).toBeTruthy();

            // Too long
            const longName = 'A'.repeat(51);
            firstNameControl?.setValue(longName);
            expect(firstNameControl?.valid).toBeFalsy();
            expect(firstNameControl?.hasError('maxlength')).toBeTruthy();
        });

        it('should validate lastName field', () => {
            const lastNameControl = component.form.get('lastName');

            // Too short
            lastNameControl?.setValue('A');
            expect(lastNameControl?.valid).toBeFalsy();
            expect(lastNameControl?.hasError('minlength')).toBeTruthy();

            // Valid length
            lastNameControl?.setValue('Smith');
            expect(lastNameControl?.valid).toBeTruthy();

            // Too long
            const longName = 'A'.repeat(51);
            lastNameControl?.setValue(longName);
            expect(lastNameControl?.valid).toBeFalsy();
            expect(lastNameControl?.hasError('maxlength')).toBeTruthy();
        });

        it('should validate email format', () => {
            const emailControl = component.form.get('email');

            // Invalid email
            emailControl?.setValue('invalid-email');
            expect(emailControl?.valid).toBeFalsy();
            expect(emailControl?.hasError('email')).toBeTruthy();

            // Valid email
            emailControl?.setValue('test@example.com');
            expect(emailControl?.valid).toBeTruthy();
        });

        it('should validate password length', () => {
            const passwordControl = component.form.get('password');

            // Too short
            passwordControl?.setValue('12345');
            expect(passwordControl?.valid).toBeFalsy();
            expect(passwordControl?.hasError('minlength')).toBeTruthy();

            // Valid length
            passwordControl?.setValue('123456');
            expect(passwordControl?.valid).toBeTruthy();
        });

        it('should validate password matching', () => {
            // Set valid but mismatched passwords
            component.form.get('password')?.setValue('123456');
            component.form.get('confirmPassword')?.setValue('abcdef');

            expect(component.form.hasError('passwordMismatch')).toBeTruthy();

            // Set matching passwords
            component.form.get('password')?.setValue('123456');
            component.form.get('confirmPassword')?.setValue('123456');

            expect(component.form.hasError('passwordMismatch')).toBeFalsy();
        });

        it('should enable register button when form is valid', () => {
            // Fill all required fields with valid data
            component.form.get('firstName')?.setValue('John');
            component.form.get('lastName')?.setValue('Smith');
            component.form.get('email')?.setValue('test@example.com');
            component.form.get('password')?.setValue('123456');
            component.form.get('confirmPassword')?.setValue('123456');

            fixture.detectChanges();

            const button = fixture.debugElement.query(By.css('p-button'));
            expect(button).toBeTruthy();
            expect(button.attributes['ng-reflect-disabled']).toBe('false');
        });
    });

    describe('Error messages', () => {
        it('should show firstName error message when field is invalid and touched', () => {
            const firstNameControl = component.form.get('firstName');

            // Set invalid firstName and mark as touched
            firstNameControl?.setValue('A'); // Too short
            firstNameControl?.markAsTouched();
            fixture.detectChanges();

            const errorMessage = fixture.debugElement.queryAll(By.css('.text-red-500'))
                .find(el => el.nativeElement.textContent.includes('Jméno je povinné'));

            expect(errorMessage).toBeTruthy();
        });

        it('should show lastName error message when field is invalid and touched', () => {
            const lastNameControl = component.form.get('lastName');

            // Set invalid lastName and mark as touched
            lastNameControl?.setValue('A'); // Too short
            lastNameControl?.markAsTouched();
            fixture.detectChanges();

            const errorMessage = fixture.debugElement.queryAll(By.css('.text-red-500'))
                .find(el => el.nativeElement.textContent.includes('Příjmení je povinné'));

            expect(errorMessage).toBeTruthy();
        });

        it('should show email error message when field is invalid and touched', () => {
            const emailControl = component.form.get('email');

            // Set invalid email and mark as touched
            emailControl?.setValue('invalid-email');
            emailControl?.markAsTouched();
            fixture.detectChanges();

            const errorMessage = fixture.debugElement.queryAll(By.css('.text-red-500'))
                .find(el => el.nativeElement.textContent.includes('E-mail musí být ve správném formátu'));

            expect(errorMessage).toBeTruthy();
        });

        it('should show password error message when field is invalid and touched', () => {
            const passwordControl = component.form.get('password');

            // Set invalid password and mark as touched
            passwordControl?.setValue('12345'); // Too short
            passwordControl?.markAsTouched();
            fixture.detectChanges();

            const errorMessage = fixture.debugElement.queryAll(By.css('.text-red-500'))
                .find(el => el.nativeElement.textContent.includes('Je nutné zadat heslo'));

            expect(errorMessage).toBeTruthy();
        });

        it('should show password mismatch error when passwords do not match', () => {
            // Set valid but mismatched passwords and mark as touched
            const passwordControl = component.form.get('password');
            const confirmPasswordControl = component.form.get('confirmPassword');

            passwordControl?.setValue('123456');
            confirmPasswordControl?.setValue('abcdef');
            confirmPasswordControl?.markAsTouched();

            fixture.detectChanges();

            const errorMessage = fixture.debugElement.queryAll(By.css('.text-red-500'))
                .find(el => el.nativeElement.textContent.includes('Hesla musí být stejná'));

            expect(errorMessage).toBeTruthy();
        });
    });

    describe('Registration functionality', () => {
        it('should call register method on button click', () => {
            // Spy on component's register method
            spyOn(component, 'register');

            // Find and click the register button
            const button = fixture.debugElement.query(By.css('p-button'));
            button.triggerEventHandler('onClick', null);

            expect(component.register).toHaveBeenCalled();
        });

        it('should not call AuthService if form is invalid', () => {
            // Don't make the form valid yet
            component.register();

            expect(authServiceMock.register).not.toHaveBeenCalled();
        });

        it('should call AuthService with correct data when form is valid', fakeAsync(() => {
            // Fill all fields with valid data
            component.form.patchValue({
                firstName: 'John',
                lastName: 'Smith',
                email: 'test@example.com',
                password: '123456',
                confirmPassword: '123456'
            });

            component.register();
            tick();

            expect(authServiceMock.register).toHaveBeenCalledWith({
                firstName: 'John',
                lastName: 'Smith',
                email: 'test@example.com',
                password: '123456',
                confirmPassword: '123456'
            });
        }));

        it('should show success toast and navigate to login page on successful registration', fakeAsync(() => {
            // Fill all fields with valid data
            component.form.patchValue({
                firstName: 'John',
                lastName: 'Smith',
                email: 'test@example.com',
                password: '123456',
                confirmPassword: '123456'
            });

            component.register();
            tick();

            expect(toastServiceMock.showSuccessToast);
        }));

        it('should show error toast when registration fails', fakeAsync(() => {
            // Fill all fields with valid data
            component.form.patchValue({
                firstName: 'John',
                lastName: 'Smith',
                email: 'test@example.com',
                password: '123456',
                confirmPassword: '123456'
            });

            // Mock registration failure
            const errorResponse = { error: { detail: 'Email already exists' } };
            authServiceMock.register.and.returnValue(throwError(() => errorResponse));

            component.register();
            tick();

            expect(consoleLogSpy).toHaveBeenCalledWith(errorResponse);
            expect(toastServiceMock.showErrorToast).toHaveBeenCalledWith('Chyba', 'Email already exists');
            expect(routerMock.navigate).not.toHaveBeenCalled();
        }));

        it('should log error when toast service fails after registration error', fakeAsync(() => {
            // Fill all fields with valid data
            component.form.patchValue({
                firstName: 'John',
                lastName: 'Smith',
                email: 'test@example.com',
                password: '123456',
                confirmPassword: '123456'
            });

            // Mock registration and toast failure
            const errorResponse = { error: { detail: 'Email already exists' } };
            authServiceMock.register.and.returnValue(throwError(() => errorResponse));
            toastServiceMock.showErrorToast.and.rejectWith('Toast error');

            component.register();
            tick();

            // We expect both errors to be logged
            expect(consoleLogSpy).toHaveBeenCalledTimes(2);
            const lastCallArgs = consoleLogSpy.calls.argsFor(1);
            expect(lastCallArgs[0]).toContain('Chyba při zobrazení chybového toastu:');
        }));
    });

    describe('UI elements', () => {
        it('should have the correct heading', () => {
            const heading = fixture.debugElement.query(By.css('h2'));
            expect(heading).toBeTruthy();
            expect(heading.nativeElement.textContent).toContain('Registrace');
        });

        it('should have a link to login page', () => {
            const loginLink = fixture.debugElement.query(By.css('a[routerLink="/prihlaseni"]'));
            expect(loginLink).toBeTruthy();
            expect(loginLink.nativeElement.textContent).toContain('Přihlásit se');
        });

        it('should have all required form fields with correct labels', () => {
            const firstNameLabel = fixture.debugElement.query(By.css('label[for="firstName"]'));
            const lastNameLabel = fixture.debugElement.query(By.css('label[for="lastName"]'));
            const emailLabel = fixture.debugElement.query(By.css('label[for="email"]'));
            const passwordLabel = fixture.debugElement.query(By.css('label[for="password"]'));
            const confirmPasswordLabel = fixture.debugElement.query(By.css('label[for="confirmPassword"]'));

            expect(firstNameLabel).toBeTruthy();
            expect(lastNameLabel).toBeTruthy();
            expect(emailLabel).toBeTruthy();
            expect(passwordLabel).toBeTruthy();
            expect(confirmPasswordLabel).toBeTruthy();

            expect(firstNameLabel.nativeElement.textContent).toContain('Jméno');
            expect(lastNameLabel.nativeElement.textContent).toContain('Příjmení');
            expect(emailLabel.nativeElement.textContent).toContain('E-mail');
            expect(passwordLabel.nativeElement.textContent).toContain('Heslo');
            expect(confirmPasswordLabel.nativeElement.textContent).toContain('Potvrzovací heslo');
        });

        it('should have a register button with correct text and icon', () => {
            const button = fixture.debugElement.query(By.css('p-button'));
            expect(button).toBeTruthy();
            expect(button.attributes['label']).toBe('Registrovat se');
            expect(button.attributes['icon']).toBe('pi pi-user-plus');
        });
    });
});
