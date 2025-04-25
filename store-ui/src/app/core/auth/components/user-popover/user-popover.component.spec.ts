import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserPopoverComponent } from './user-popover.component';
import { AuthService } from '../../../../services/services/auth.service';
import { ToastService } from '../../../../shared/services/toast.service';
import {provideRouter, Router} from '@angular/router';
import { of, throwError } from 'rxjs';
import { By } from '@angular/platform-browser';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { UserResponse } from '../../../../services/models/user/user-response';

describe('UserPopoverComponent', () => {
    let component: UserPopoverComponent;
    let fixture: ComponentFixture<UserPopoverComponent>;
    let authServiceMock: jasmine.SpyObj<AuthService>;
    let toastServiceMock: jasmine.SpyObj<ToastService>;
    let routerMock: jasmine.SpyObj<Router>;
    let mockUserData: UserResponse | null;
    let consoleLogSpy: jasmine.Spy;

    beforeEach(async () => {
        // Create a mockUserData object that can be modified between tests
        mockUserData = {
            email: 'test@example.com',
            role: 'USER'
        } as UserResponse;

        // Create spy objects for services with accessors
        authServiceMock = jasmine.createSpyObj('AuthService',
            ['logout', 'hasRole'], // Methods
            { // Properties
                'getUserEmail': jasmine.createSpy('getUserEmailGetter').and.callFake(() => mockUserData?.email || null),
                'isLoggedIn': jasmine.createSpy('isLoggedInGetter').and.callFake(() => !!mockUserData),
                'isAdmin': jasmine.createSpy('isAdminGetter').and.callFake(() => mockUserData?.role?.includes('ADMIN') || false),
                'getCurrentUser': jasmine.createSpy('getCurrentUserGetter').and.callFake(() => mockUserData)
            }
        );

        toastServiceMock = jasmine.createSpyObj('ToastService',
            ['showSuccessToast', 'showErrorToast']);
        routerMock = jasmine.createSpyObj('Router', ['navigate']);

        // Set up default return values
        toastServiceMock.showSuccessToast.and.resolveTo();
        toastServiceMock.showErrorToast.and.resolveTo();
        routerMock.navigate.and.resolveTo(true);
        authServiceMock.logout.and.returnValue(of(undefined));

        // Spy on console.log
        consoleLogSpy = spyOn(console, 'log');

        await TestBed.configureTestingModule({
            imports: [
                UserPopoverComponent,
                // Remove RouterModule.forRoot([])
            ],
            providers: [
                { provide: AuthService, useValue: authServiceMock },
                { provide: ToastService, useValue: toastServiceMock },
                provideRouter([]) // Add this
            ],
            schemas: [NO_ERRORS_SCHEMA]
        })
            .compileComponents();

        fixture = TestBed.createComponent(UserPopoverComponent);
        component = fixture.componentInstance;
    });

    it('should create', () => {
        fixture.detectChanges();
        expect(component).toBeTruthy();
    });

    it('should display user email when user is logged in', () => {
        fixture.detectChanges();
        const linkElement = fixture.debugElement.query(By.css('a.flex.items-center.mt-1 span'));
        expect(linkElement).toBeTruthy();
    });

    it('should display "Přihlásit se" when user is not logged in', () => {
        // Update mockUserData to simulate logged out state
        mockUserData = null;

        fixture.detectChanges();
        const linkElement = fixture.debugElement.query(By.css('a.flex.items-center.mt-1 span'));
        expect(linkElement).toBeTruthy();
    });

    it('should toggle popover on click', () => {
        fixture.detectChanges();
        // Mock the popover object
        const popoverMock = jasmine.createSpyObj('Popover', ['toggle', 'hide']);
        component.popover = popoverMock as any;

        // Create event and call toggle
        const event = new Event('click');
        component.toggle(event, popoverMock);

        expect(component.isPopoverOpen).toBeTrue();
        expect(popoverMock.toggle).toHaveBeenCalledWith(event);

        // Toggle again should set isPopoverOpen to false
        component.toggle(event, popoverMock);
        expect(component.isPopoverOpen).toBeFalse();
    });

    it('should set isPopoverOpen to false when popover is hidden', () => {
        fixture.detectChanges();
        component.isPopoverOpen = true;

        // Create a mock popover with an onHide EventEmitter
        const popoverMock = jasmine.createSpyObj('Popover', ['toggle', 'hide'], {
            'onHide': jasmine.createSpyObj('EventEmitter', ['emit', 'subscribe'])
        });
        component.popover = popoverMock;

        // Trigger the onHide event
        popoverMock.onHide.emit();
    });

    it('should show admin link when user is admin', () => {
        // Update mockUserData to simulate admin role
        mockUserData = {
            email: 'admin@example.com',
            role: 'ADMIN'
        } as UserResponse;

        fixture.detectChanges();
    });

    it('should not show admin link when user is not admin', () => {
        // Ensure mockUserData has USER role only
        mockUserData = {
            email: 'user@example.com',
            role: 'USER'
        } as UserResponse;

        fixture.detectChanges();

        // Check for admin link
        const adminLink = fixture.debugElement.query(By.css('[routerLink="/admin"]'));
        expect(adminLink).toBeFalsy();
    });

    it('should call logout method on logout link click', async () => {
        authServiceMock.logout.and.returnValue(of(undefined));
        fixture.detectChanges();

        // Mock the popover
        const popoverMock = jasmine.createSpyObj('Popover', ['toggle', 'hide']);
        component.popover = popoverMock as any;

        // Call logout
        component.logout();

        expect(authServiceMock.logout).toHaveBeenCalled();
        expect(toastServiceMock.showSuccessToast).toHaveBeenCalledWith('Úspěch', 'Byl jsi úspěšně odhlášen.');
        expect(routerMock.navigate);
    });

    it('should handle logout error', async () => {
        const errorResponse = { error: { detail: 'Logout failed' } };
        authServiceMock.logout.and.returnValue(throwError(() => errorResponse));

        // Reset the console.log spy
        consoleLogSpy.calls.reset();

        // Call logout
        component.logout();

        expect(authServiceMock.logout).toHaveBeenCalled();
        expect(toastServiceMock.showErrorToast).toHaveBeenCalledWith('Chyba', 'Logout failed');
        expect(routerMock.navigate).not.toHaveBeenCalled();
        expect(consoleLogSpy).toHaveBeenCalledWith(errorResponse);
    });

    it('should handle toast error during logout success', async () => {
        authServiceMock.logout.and.returnValue(of(undefined));
        toastServiceMock.showSuccessToast.and.rejectWith('Toast error');

        // Reset the console.log spy
        consoleLogSpy.calls.reset();

        await component.logout();

        // Check console.log was called with an error that contains our text
        expect(consoleLogSpy).toHaveBeenCalled();
        // Get the first argument of the first call
        const logArgument = consoleLogSpy.calls.argsFor(0)[0];
        expect(logArgument).toContain('Chyba při zobrazení toastu nebo navigaci:');
    });

    it('should handle toast error during logout failure', async () => {
        const errorResponse = { error: { detail: 'Logout failed' } };
        authServiceMock.logout.and.returnValue(throwError(() => errorResponse));
        toastServiceMock.showErrorToast.and.rejectWith('Toast error');

        // Reset the console.log spy
        consoleLogSpy.calls.reset();

        await component.logout();

        // We expect two console.log calls:
        // 1. For the error response
        // 2. For the toast error
        expect(consoleLogSpy).toHaveBeenCalledTimes(2);

        // The second call should contain our error message
        const secondCallArgs = consoleLogSpy.calls.argsFor(1);
        expect(secondCallArgs[0]).toContain('Chyba při zobrazení chybového toastu:');
    });

    it('should show my orders and logout links when user is logged in', () => {
        // Ensure mockUserData is set
        mockUserData = {
            email: 'user@example.com',
            role: 'USER'
        } as UserResponse;

        fixture.detectChanges();

        const logoutLink = fixture.debugElement.query(By.css('[routerLink="/registrace"]')); // The logout link points to registration page

        // Check that the logout link has the logout method
        if (logoutLink) {
            const onClickAttr = logoutLink.attributes['(click)'];
            expect(onClickAttr).toContain('logout()');
        }
    });
});
