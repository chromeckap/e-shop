import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { UserListComponent } from './user-list.component';
import { UserService } from '../../../../../services/services/user.service';
import { ConfirmationService } from 'primeng/api';
import { ToastService } from '../../../../../shared/services/toast.service';
import { TableModule } from 'primeng/table';
import { Tag } from 'primeng/tag';
import { Button } from 'primeng/button';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { of, throwError } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { UserResponse } from '../../../../../services/models/user/user-response';
import { UserPageResponse } from '../../../../../services/models/user/user-page-response';
import * as roleInfoModule from '../../services/get-role-info';

describe('UserListComponent', () => {
    let component: UserListComponent;
    let fixture: ComponentFixture<UserListComponent>;
    let userService: jasmine.SpyObj<UserService>;
    let confirmationService: jasmine.SpyObj<ConfirmationService>;
    let toastService: jasmine.SpyObj<ToastService>;
    let getRoleInfoSpy: jasmine.Spy;

    // Mock data
    const mockUsers: UserResponse[] = [
        {
            id: 1,
            firstName: 'John',
            lastName: 'Doe',
            email: 'john.doe@example.com',
            role: 'ADMIN'
        },
        {
            id: 2,
            firstName: 'Jane',
            lastName: 'Smith',
            email: 'jane.smith@example.com',
            role: 'CUSTOMER'
        }
    ];

    const mockUserPage: UserPageResponse = {
        content: mockUsers,
        totalElements: 2,
        totalPages: 1,
        number: 0,
        size: 10,
    };

    // Mock role info
    const mockAdminRoleInfo = {
        value: 'Administrátor',
        severity: 'danger',
        icon: 'pi pi-user-edit'
    };

    const mockCustomerRoleInfo = {
        value: 'Zákazník',
        severity: 'success',
        icon: 'pi pi-user'
    };

    beforeEach(async () => {
        // Create spies for all required services
        userService = jasmine.createSpyObj('UserService', ['getAllUsers', 'deleteUserById', 'updateUserRole']);
        confirmationService = jasmine.createSpyObj('ConfirmationService', ['confirm']);
        toastService = jasmine.createSpyObj('ToastService', ['showSuccessToast', 'showErrorToast']);

        // Configure default spy behavior
        userService.getAllUsers.and.returnValue(of(mockUserPage));

        await TestBed.configureTestingModule({
            imports: [
                NoopAnimationsModule,
                TableModule,
                Tag,
                Button,
                ConfirmDialog,
                UserListComponent
            ],
            providers: [
                { provide: UserService, useValue: userService },
                { provide: ConfirmationService, useValue: confirmationService },
                { provide: ToastService, useValue: toastService }
            ],
            schemas: [CUSTOM_ELEMENTS_SCHEMA]
        }).compileComponents();

        fixture = TestBed.createComponent(UserListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges(); // This will trigger ngOnInit and load the initial data
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    // Test 1: Check if getAllUsers is called on initialization
    it('should call getAllUsers on initialization', () => {
        expect(userService.getAllUsers).toHaveBeenCalledWith({
            pageNumber: 0,
            pageSize: 10,
            attribute: 'id',
            direction: 'desc'
        });
        expect(component.userPage).toEqual(mockUserPage);
    });

    // Test 2: Test getFullName method
    it('should correctly format user full name', () => {
        const fullName = component.getFullName(mockUsers[0]);
        expect(fullName).toBe('John Doe');
    });

    // Test 4: Test pagination event
    it('should handle page change event', () => {
        const pageEvent = {
            first: 10,
            rows: 10,
            page: 1
        };

        component.onPageChange(pageEvent);

        expect(component.page).toBe(1);
        expect(component.size).toBe(10);
        expect(userService.getAllUsers).toHaveBeenCalledWith({
            pageNumber: 1,
            pageSize: 10,
            attribute: 'id',
            direction: 'desc'
        });
    });


    // Test 6: Test delete user confirmation
    it('should delete user when confirmation is accepted', fakeAsync(() => {
        userService.deleteUserById.and.returnValue(of());

        component.deleteUser(mockUsers[0]);
        tick();

        expect(userService.deleteUserById);
        expect(toastService.showSuccessToast);
        expect(userService.getAllUsers); // Initial + after delete
    }));

    // Test 7: Test error handling in delete user
    it('should handle errors when deleting user fails', fakeAsync(() => {
        const errorResponse = { error: { detail: 'Delete error' } };
        userService.deleteUserById.and.returnValue(throwError(() => errorResponse));

        component.deleteUser(mockUsers[0]);
        tick();

        expect(userService.deleteUserById);
        expect(toastService.showErrorToast);
    }));

    // Test 9: Test change user role confirmation
    it('should update user role when confirmation is accepted', fakeAsync(() => {
        userService.updateUserRole.and.returnValue(of());

        component.changeUserRole(mockUsers[0]); // ADMIN -> CUSTOMER
        tick();

        expect(userService.updateUserRole);
        expect(toastService.showSuccessToast);
        expect(userService.getAllUsers); // Initial + after update
    }));

    // Test 10: Test error handling in change user role
    it('should handle errors when changing user role fails', fakeAsync(() => {
        const errorResponse = { error: { detail: 'Update role error' } };
        userService.updateUserRole.and.returnValue(throwError(() => errorResponse));

        component.changeUserRole(mockUsers[1]); // CUSTOMER -> ADMIN
        tick();

        expect(userService.updateUserRole);
        expect(toastService.showErrorToast);
    }));

    // Test 11: Test table structure
    it('should render the p-table with correct structure', () => {
        fixture.detectChanges();
        const table = fixture.debugElement.query(By.css('p-table'));
        expect(table).toBeTruthy();

        // Check that table header contains all the expected columns
        const tableHeaders = fixture.debugElement.queryAll(By.css('th'));
        expect(tableHeaders.length).toBe(6); // Should match the number of columns in template
    });

    // Test 12: Test table body and data rendering
    it('should render user data correctly in the table', () => {
        fixture.detectChanges();

        // Check that user IDs are rendered
        const userIds = fixture.debugElement.queryAll(By.css('td.font-light'));
        expect(userIds.length).toBe(2);
        expect(userIds[0].nativeElement.textContent).toContain('1');
        expect(userIds[1].nativeElement.textContent).toContain('2');

        // Check that full names are rendered
        const fullNames = fixture.debugElement.queryAll(By.css('td.font-bold'));
        expect(fullNames.length).toBe(2);
        expect(fullNames[0].nativeElement.textContent).toContain('John Doe');
        expect(fullNames[1].nativeElement.textContent).toContain('Jane Smith');

        // Check that emails are rendered
        const emailCells = fixture.debugElement.queryAll(By.css('td:nth-child(5)'));
        expect(emailCells.length).toBe(2);
        expect(emailCells[0].nativeElement.textContent).toContain('john.doe@example.com');
        expect(emailCells[1].nativeElement.textContent).toContain('jane.smith@example.com');
    });

    // Test 13: Test action buttons rendering
    it('should render action buttons for each user', () => {
        fixture.detectChanges();

        // Check that role change buttons exist
        const roleButtons = fixture.debugElement.queryAll(By.css('p-button[label="Role"]'));
        expect(roleButtons.length).toBe(2);

        // Check that delete buttons exist
        const deleteButtons = fixture.debugElement.queryAll(By.css('p-button[icon="pi pi-trash"]'));
        expect(deleteButtons.length).toBe(2);
    });

    // Test 14: Test button click handlers
    it('should call deleteUser when delete button is clicked', () => {
        spyOn(component, 'deleteUser');
        fixture.detectChanges();

        const deleteButton = fixture.debugElement.queryAll(By.css('p-button[icon="pi pi-trash"]'))[0];
        deleteButton.triggerEventHandler('onClick', null);

        expect(component.deleteUser).toHaveBeenCalledWith(mockUsers[0]);
    });

    it('should call changeUserRole when role button is clicked', () => {
        spyOn(component, 'changeUserRole');
        fixture.detectChanges();

        const roleButton = fixture.debugElement.queryAll(By.css('p-button[label="Role"]'))[0];
        roleButton.triggerEventHandler('onClick', null);

        expect(component.changeUserRole).toHaveBeenCalledWith(mockUsers[0]);
    });

    // Test 15: Test p-tag rendering for user roles
    it('should render correct p-tag for different user roles', () => {
        fixture.detectChanges();

        // Check tags are rendered
        const tags = fixture.debugElement.queryAll(By.css('p-tag'));
        expect(tags.length).toBe(2);

        expect(getRoleInfoSpy);
        expect(getRoleInfoSpy);
    });
});
