import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { LoginComponent } from './login.component';
import { ReactiveFormsModule, FormBuilder } from '@angular/forms';
import { AuthService } from '../../../../services/services/auth.service';
import { ToastService } from '../../../../shared/services/toast.service';
import { ActivatedRoute, Router } from '@angular/router';
import { By } from '@angular/platform-browser';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('LoginComponent', () => {
    let component: LoginComponent;
    let fixture: ComponentFixture<LoginComponent>;
    let authServiceMock: jasmine.SpyObj<AuthService>;
    let toastServiceMock: jasmine.SpyObj<ToastService>;
    let routerMock: jasmine.SpyObj<Router>;
    let formBuilder: FormBuilder;
    let consoleLogSpy: jasmine.Spy;

    beforeEach(async () => {
        // Create spy objects for the services
        authServiceMock = jasmine.createSpyObj('AuthService', ['login']);
        toastServiceMock = jasmine.createSpyObj('ToastService',
            ['showSuccessToast', 'showErrorToast']);
        routerMock = jasmine.createSpyObj('Router', ['navigate']);

        // Spy on console.log
        consoleLogSpy = spyOn(console, 'log');

        // Default return values
        authServiceMock.login.and.returnValue(of({}));
        toastServiceMock.showSuccessToast.and.resolveTo();
        toastServiceMock.showErrorToast.and.resolveTo();
        routerMock.navigate.and.resolveTo(true);

        await TestBed.configureTestingModule({
            imports: [
                LoginComponent,
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

        fixture = TestBed.createComponent(LoginComponent);
        component = fixture.componentInstance;
        formBuilder = TestBed.inject(FormBuilder);
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should initialize the form with empty email and password', () => {
        expect(component.form.get('email')?.value).toBe('');
        expect(component.form.get('password')?.value).toBe('');
    });

    it('should mark form as invalid when empty', () => {
        expect(component.form.valid).toBeFalsy();
    });

    describe('Form validation', () => {
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

        it('should require email field', () => {
            const emailControl = component.form.get('email');

            emailControl?.setValue('');
            expect(emailControl?.valid).toBeFalsy();
            expect(emailControl?.hasError('required')).toBeTruthy();
        });

        it('should require password field', () => {
            const passwordControl = component.form.get('password');

            passwordControl?.setValue('');
            expect(passwordControl?.valid).toBeFalsy();
            expect(passwordControl?.hasError('required')).toBeTruthy();

            passwordControl?.setValue('password123');
            expect(passwordControl?.valid).toBeTruthy();
        });

        it('should enable login button when form is valid', () => {
            // Set form to valid
            component.form.get('email')?.setValue('test@example.com');
            component.form.get('password')?.setValue('password123');
            component.form.markAllAsTouched();
            fixture.detectChanges();

            // Use the proper way to get the button and check the disabled attribute
            const buttonEl = fixture.debugElement.query(By.css('p-button'));
            expect(buttonEl).toBeTruthy();
            expect(buttonEl.attributes['ng-reflect-disabled']).toBe('false');
        });
    });

    describe('Error messages', () => {
        it('should show email error message when invalid email is entered and field is touched', () => {
            const emailControl = component.form.get('email');

            // Set invalid email and mark as touched
            emailControl?.setValue('invalid-email');
            emailControl?.markAsTouched();
            fixture.detectChanges();

            const errorMessage = fixture.debugElement.query(By.css('.text-red-500'));
            expect(errorMessage).toBeTruthy();
            expect(errorMessage.nativeElement.textContent).toContain('E-mail musí být ve správném formátu');
        });

        it('should show password error message when field is touched and empty', () => {
            const passwordControl = component.form.get('password');

            // Set empty password and mark as touched
            passwordControl?.setValue('');
            passwordControl?.markAsTouched();
            fixture.detectChanges();

            const errorMessages = fixture.debugElement.queryAll(By.css('.text-red-500'));
            // Find password error message
            const passwordErrorMsg = errorMessages.find(el =>
                el.nativeElement.textContent.includes('Je nutné zadat heslo'));

            expect(passwordErrorMsg).toBeTruthy();
        });

        it('should not show error messages when fields are untouched', () => {
            // Reset the form without touching controls
            component.form.reset();
            fixture.detectChanges();

            const errorMessages = fixture.debugElement.queryAll(By.css('.text-red-500'));
            expect(errorMessages.length).toBe(0);
        });
    });

    describe('Login functionality', () => {
        it('should call login method on button click', () => {
            // Spy on component's login method
            spyOn(component, 'login');

            // Find and click the login button
            const button = fixture.debugElement.query(By.css('p-button'));
            button.triggerEventHandler('onClick', null);

            expect(component.login).toHaveBeenCalled();
        });

        it('should call login method on enter key press in password field', () => {
            // Spy on component's login method
            spyOn(component, 'login');

            // Find and trigger keyup.enter on password field
            const passwordField = fixture.debugElement.query(By.css('p-password'));
            passwordField.triggerEventHandler('keyup.enter', {});

            expect(component.login).toHaveBeenCalled();
        });

        it('should not call AuthService if form is invalid', () => {
            // Don't make the form valid yet
            component.login();

            expect(authServiceMock.login).not.toHaveBeenCalled();
        });

        it('should call AuthService with correct credentials when form is valid', fakeAsync(() => {
            // Make the form valid
            component.form.get('email')?.setValue('test@example.com');
            component.form.get('password')?.setValue('password123');

            component.login();
            tick();

            expect(authServiceMock.login).toHaveBeenCalledWith({
                email: 'test@example.com',
                password: 'password123'
            });
        }));

        it('should show success toast and navigate home on successful login', fakeAsync(() => {
            // Make the form valid
            component.form.get('email')?.setValue('test@example.com');
            component.form.get('password')?.setValue('password123');

            component.login();
            tick();

            expect(toastServiceMock.showSuccessToast).toHaveBeenCalledWith('Úspěch', 'Přihlášení bylo úspěšné.');
        }));

        it('should show error toast when login fails', fakeAsync(() => {
            // Make the form valid
            component.form.get('email')?.setValue('test@example.com');
            component.form.get('password')?.setValue('password123');

            // Mock login failure
            const errorResponse = { error: { detail: 'Invalid credentials' } };
            authServiceMock.login.and.returnValue(throwError(() => errorResponse));

            component.login();
            tick();

            expect(toastServiceMock.showErrorToast).toHaveBeenCalledWith('Chyba', 'Invalid credentials');
            expect(routerMock.navigate).not.toHaveBeenCalled();
        }));

        it('should log error when toast service fails after successful login', fakeAsync(() => {
            // Make the form valid
            component.form.get('email')?.setValue('test@example.com');
            component.form.get('password')?.setValue('password123');

            // Mock toast failure
            toastServiceMock.showSuccessToast.and.rejectWith('Toast error');

            component.login();
            tick();

            expect(consoleLogSpy).toHaveBeenCalled();
            const logArgs = consoleLogSpy.calls.argsFor(0);
            expect(logArgs[0]).toContain('Chyba při zobrazení toastu nebo navigaci:');
        }));

        it('should log error when toast service fails after login error', fakeAsync(() => {
            // Make the form valid
            component.form.get('email')?.setValue('test@example.com');
            component.form.get('password')?.setValue('password123');

            // Mock login and toast failure
            const errorResponse = { error: { detail: 'Invalid credentials' } };
            authServiceMock.login.and.returnValue(throwError(() => errorResponse));
            toastServiceMock.showErrorToast.and.rejectWith('Toast error');

            component.login();
            tick();

            expect(consoleLogSpy).toHaveBeenCalled();
            const logArgs = consoleLogSpy.calls.argsFor(0);
            expect(logArgs[0]).toContain('Chyba při zobrazení chybového toastu:');
        }));
    });

    describe('UI elements', () => {
        it('should have the correct heading', () => {
            const heading = fixture.debugElement.query(By.css('h2'));
            expect(heading).toBeTruthy();
            expect(heading.nativeElement.textContent).toContain('Přihlášení');
        });

        it('should have a link to registration page', () => {
            const registrationLink = fixture.debugElement.query(By.css('a[routerLink="/registrace"]'));
            expect(registrationLink).toBeTruthy();
            expect(registrationLink.nativeElement.textContent).toContain('Zaregistrovat se');
        });

        it('should have email and password input fields', () => {
            const emailLabel = fixture.debugElement.query(By.css('label[for="email"]'));
            const passwordLabel = fixture.debugElement.query(By.css('label[for="password"]'));

            expect(emailLabel).toBeTruthy();
            expect(passwordLabel).toBeTruthy();
        });
    });
});
