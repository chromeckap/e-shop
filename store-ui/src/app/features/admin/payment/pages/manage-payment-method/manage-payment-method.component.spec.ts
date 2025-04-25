import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ManagePaymentMethodComponent } from './manage-payment-method.component';
import { PaymentMethodService } from '../../../../../services/services/payment-method.service';
import { ConfirmationService } from 'primeng/api';
import { ToastService } from '../../../../../shared/services/toast.service';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Button } from 'primeng/button';
import { NgClass } from '@angular/common';
import { Toolbar } from 'primeng/toolbar';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { OverviewComponent } from '../../components/overview/overview.component';
import { GatewayTypeSelectComponent } from '../../components/gateway-type-select/gateway-type-select.component';
import { of, throwError } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';
import { Component, Input } from '@angular/core';
import { PaymentGatewayType } from '../../../../../services/models/payment-method/payment-gateway-type';
import { FormGroup } from '@angular/forms';

// Mock components
@Component({
    selector: 'app-overview',
    template: ''
})
class MockOverviewComponent {
    @Input() form!: FormGroup;
}

@Component({
    selector: 'app-gateway-type-select',
    template: ''
})
class MockGatewayTypeSelectComponent {
    @Input() form!: FormGroup;
    @Input() gatewayTypes: PaymentGatewayType[] = [];
}

describe('ManagePaymentMethodComponent', () => {
    let component: ManagePaymentMethodComponent;
    let fixture: ComponentFixture<ManagePaymentMethodComponent>;
    let paymentMethodServiceSpy: jasmine.SpyObj<PaymentMethodService>;
    let confirmationServiceSpy: jasmine.SpyObj<ConfirmationService>;
    let toastServiceSpy: jasmine.SpyObj<ToastService>;
    let routerSpy: jasmine.SpyObj<Router>;
    let activatedRouteSpy: any;

    // Mock data
    const mockGatewayTypes: PaymentGatewayType[] = [
        { id: '1', name: 'Credit Card', code: 'CREDIT_CARD' },
        { id: '2', name: 'PayPal', code: 'PAYPAL' }
    ];

    beforeEach(async () => {
        // Create spies
        const paymentMethodSpy = jasmine.createSpyObj('PaymentMethodService', [
            'getPaymentGatewayTypes',
            'getPaymentMethodById',
            'createPaymentMethod',
            'updatePaymentMethod',
            'deletePaymentMethodById'
        ]);
        const confirmationSpy = jasmine.createSpyObj('ConfirmationService', ['confirm']);
        const toastSpy = jasmine.createSpyObj('ToastService', ['showSuccessToast', 'showErrorToast']);
        const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

        // Mock ActivatedRoute with params
        const activatedRouteMock = {
            snapshot: {
                params: {}
            }
        };

        await TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                NoopAnimationsModule,
                Button,
                NgClass,
                Toolbar,
                ConfirmDialog,
                ManagePaymentMethodComponent
            ],
            providers: [
                FormBuilder,
                { provide: PaymentMethodService, useValue: paymentMethodSpy },
                { provide: ConfirmationService, useValue: confirmationSpy },
                { provide: ToastService, useValue: toastSpy },
                { provide: Router, useValue: routerSpy },
                { provide: ActivatedRoute, useValue: activatedRouteMock }
            ]
        })
            .overrideComponent(ManagePaymentMethodComponent, {
                remove: {
                    imports: [OverviewComponent, GatewayTypeSelectComponent]
                },
                add: {
                    imports: [MockOverviewComponent, MockGatewayTypeSelectComponent]
                }
            })
            .compileComponents();

        paymentMethodServiceSpy = TestBed.inject(PaymentMethodService) as jasmine.SpyObj<PaymentMethodService>;
        confirmationServiceSpy = TestBed.inject(ConfirmationService) as jasmine.SpyObj<ConfirmationService>;
        toastServiceSpy = TestBed.inject(ToastService) as jasmine.SpyObj<ToastService>;
        activatedRouteSpy = TestBed.inject(ActivatedRoute);

        // Setup default return values
        paymentMethodServiceSpy.getPaymentGatewayTypes.and.returnValue(of(mockGatewayTypes));
        toastServiceSpy.showSuccessToast.and.returnValue(Promise.resolve());
        toastServiceSpy.showErrorToast.and.returnValue(Promise.resolve());
        routerSpy.navigate.and.returnValue(Promise.resolve(true));

        fixture = TestBed.createComponent(ManagePaymentMethodComponent);
        component = fixture.componentInstance;
    });

    it('should create', () => {
        fixture.detectChanges();
        expect(component).toBeTruthy();
    });

    it('should initialize the form correctly', () => {
        expect(component.form).toBeTruthy();
        expect(component.form.get('id')?.disabled).toBeTrue();
        expect(component.form.get('name')).toBeTruthy();
        expect(component.form.get('gatewayType')).toBeTruthy();
        expect(component.form.get('isActive')).toBeTruthy();
        expect(component.form.get('price')).toBeTruthy();
        expect(component.form.get('isFreeForOrderAbove')).toBeTruthy();
        expect(component.form.get('freeForOrderAbove')).toBeTruthy();
    });

    it('should load gateway types on init', fakeAsync(() => {
        fixture.detectChanges();
        tick();

        expect(paymentMethodServiceSpy.getPaymentGatewayTypes).toHaveBeenCalled();
        expect(component.gatewayTypes).toEqual(mockGatewayTypes);
    }));

    it('should not load payment method data when id is not provided', fakeAsync(() => {
        // Set route param without ID
        activatedRouteSpy.snapshot.params = {};

        fixture.detectChanges();
        tick();

        expect(paymentMethodServiceSpy.getPaymentMethodById).not.toHaveBeenCalled();
        expect(component.form.getRawValue().id).toBeNull();
    }));

    it('should determine editing mode correctly', fakeAsync(() => {
        // Initially without ID
        fixture.detectChanges();
        tick();

        expect(component.isEditing).toBeFalsy();

        // Set ID in form
        component.form.patchValue({ id: '123' });

        expect(component.isEditing).toBeTruthy();
    }));

    it('should show appropriate title based on editing mode', fakeAsync(() => {
        fixture.detectChanges();
        tick();

        // Initially in create mode
        let title = fixture.debugElement.query(By.css('h5')).nativeElement.textContent;
        expect(title).toContain('Vytvoření');

        // Switch to edit mode
        component.form.patchValue({
            id: '123',
            name: 'Test Payment Method'
        });
        fixture.detectChanges();

        title = fixture.debugElement.query(By.css('h5')).nativeElement.textContent;
        expect(title).toContain('Editace');
        expect(title).toContain('Test Payment Method');
    }));

    it('should disable save button when form is invalid', fakeAsync(() => {
        fixture.detectChanges();
        tick();

        // Form should be invalid initially (required fields empty)
        const saveButton = fixture.debugElement.query(By.css('p-button[icon="pi pi-save"]'));
        expect(saveButton.attributes['ng-reflect-disabled']).toBe('true');

        // Make form valid
        component.form.patchValue({
            name: 'Test Method',
            gatewayType: mockGatewayTypes[0],
            price: 50,
            freeForOrderAbove: 1000
        });
        fixture.detectChanges();

        expect(saveButton.attributes['ng-reflect-disabled']).toBe('false');
    }));

    it('should not save when form is invalid', fakeAsync(() => {
        fixture.detectChanges();
        tick();

        // Leave form invalid (missing required fields)
        component.form.patchValue({
            name: '' // required field empty
        });

        component.savePaymentMethod();
        tick();

        expect(paymentMethodServiceSpy.createPaymentMethod).not.toHaveBeenCalled();
        expect(paymentMethodServiceSpy.updatePaymentMethod).not.toHaveBeenCalled();
    }));

    it('should handle save errors', fakeAsync(() => {
        fixture.detectChanges();
        tick();

        // Fill in required values
        component.form.patchValue({
            name: 'New Method',
            gatewayType: { type: 'CREDIT_CARD' },
            price: 75,
            freeForOrderAbove: 2000
        });

        // Make service throw error
        const errorResponse = {
            error: {
                detail: 'Server error occurred'
            }
        };
        paymentMethodServiceSpy.createPaymentMethod.and.returnValue(throwError(() => errorResponse));

        spyOn(console, 'log');
        component.savePaymentMethod();
        tick();

        expect(toastServiceSpy.showErrorToast).toHaveBeenCalledWith('Chyba', 'Server error occurred');
        expect(console.log).toHaveBeenCalled();
    }));

    it('should show delete button only in edit mode', fakeAsync(() => {
        fixture.detectChanges();
        tick();

        // Initially in create mode
        let deleteButton = fixture.debugElement.query(By.css('p-button[icon="pi pi-trash"]'));
        expect(deleteButton).toBeNull();

        // Switch to edit mode
        component.form.patchValue({ id: '123' });
        fixture.detectChanges();

        deleteButton = fixture.debugElement.query(By.css('p-button[icon="pi pi-trash"]'));
        expect(deleteButton).toBeTruthy();
    }));

    it('should handle delete errors', fakeAsync(() => {
        // Set up edit mode
        component.form.patchValue({ id: '123' });

        // Make service throw error
        const errorResponse = {
            error: {
                detail: 'Cannot delete payment method'
            }
        };
        paymentMethodServiceSpy.deletePaymentMethodById.and.returnValue(throwError(() => errorResponse));

        spyOn(console, 'log');
        component.deletePaymentMethod();
        tick();

        expect(toastServiceSpy.showErrorToast);
    }));
});
