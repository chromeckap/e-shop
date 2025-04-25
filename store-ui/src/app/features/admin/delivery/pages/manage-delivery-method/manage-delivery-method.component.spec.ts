import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ManageDeliveryMethodComponent } from './manage-delivery-method.component';
import { DeliveryMethodService } from '../../../../../services/services/delivery-method.service';
import { ToastService } from '../../../../../shared/services/toast.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ConfirmationService } from 'primeng/api';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { CourierType } from '../../../../../services/models/delivery-method/courier-type';

describe('ManageDeliveryMethodComponent', () => {
    let component: ManageDeliveryMethodComponent;
    let fixture: ComponentFixture<ManageDeliveryMethodComponent>;
    let deliveryMethodService: jasmine.SpyObj<DeliveryMethodService>;
    let toastService: jasmine.SpyObj<ToastService>;
    let router: jasmine.SpyObj<Router>;
    let confirmationService: jasmine.SpyObj<ConfirmationService>;
    let activatedRoute: { snapshot: { params: { [key: string]: any } } };

    // Mock courier types
    const mockCourierTypes: CourierType[] = [
        { id: '1', name: 'Standard', type: 'STANDARD' },
        { id: '2', name: 'Express', type: 'EXPRESS' }
    ];

    // Mock delivery method for edit mode
    const mockDeliveryMethod = {
        id: 1,
        name: 'Test Delivery Method',
        courierType: mockCourierTypes[0],
        isActive: true,
        price: 100,
        isFreeForOrderAbove: true,
        freeForOrderAbove: 1000
    };

    beforeEach(async () => {
        // Create spies
        deliveryMethodService = jasmine.createSpyObj('DeliveryMethodService', [
            'getCourierTypes',
            'getDeliveryMethodById',
            'createDeliveryMethod',
            'updateDeliveryMethod',
            'deleteDeliveryMethodById'
        ]);

        toastService = jasmine.createSpyObj('ToastService', ['showSuccessToast', 'showErrorToast']);
        router = jasmine.createSpyObj('Router', ['navigate']);
        confirmationService = jasmine.createSpyObj('ConfirmationService', ['confirm']);

        // Setup activated route with empty params initially
        activatedRoute = {
            snapshot: {
                params: {}
            }
        };

        // Setup default return values
        deliveryMethodService.getCourierTypes.and.returnValue(of(mockCourierTypes));
        toastService.showSuccessToast.and.returnValue(Promise.resolve());
        toastService.showErrorToast.and.returnValue(Promise.resolve());
        router.navigate.and.returnValue(Promise.resolve(true));

        await TestBed.configureTestingModule({
            imports: [
                ManageDeliveryMethodComponent,
                ReactiveFormsModule
            ],
            providers: [
                FormBuilder,
                { provide: DeliveryMethodService, useValue: deliveryMethodService },
                { provide: ToastService, useValue: toastService },
                { provide: Router, useValue: router },
                { provide: ConfirmationService, useValue: confirmationService },
                { provide: ActivatedRoute, useValue: activatedRoute }
            ],
            schemas: [NO_ERRORS_SCHEMA]
        }).compileComponents();

        fixture = TestBed.createComponent(ManageDeliveryMethodComponent);
        component = fixture.componentInstance;
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    describe('Form initialization', () => {
        it('should initialize the form with default values', () => {
            expect(component.form.getRawValue()).toEqual({
                id: null,
                name: '',
                courierType: null,
                isActive: false,
                price: 0,
                isFreeForOrderAbove: false,
                freeForOrderAbove: 0
            });
        });

        it('should load courier types on init', () => {
            component.ngOnInit();
            expect(deliveryMethodService.getCourierTypes).toHaveBeenCalled();
            expect(component.courierTypes).toEqual(mockCourierTypes);
        });
    });

    describe('Create mode', () => {
        beforeEach(() => {
            // Ensure we're in create mode (no ID parameter)
            activatedRoute.snapshot.params = {};
            component.ngOnInit();
        });

        it('should recognize create mode', () => {
            expect(component.isEditing).toBeFalsy();
        });

        it('should call createDeliveryMethod when saving in create mode', () => {
            // Set valid form values
            component.form.patchValue({
                name: 'New Delivery Method',
                courierType: mockCourierTypes[0],
                isActive: true,
                price: 150,
                isFreeForOrderAbove: false,
                freeForOrderAbove: 0
            });

            deliveryMethodService.createDeliveryMethod.and.returnValue(of(2)); // Return new ID

            component.saveDeliveryMethod();

            expect(deliveryMethodService.createDeliveryMethod).toHaveBeenCalled();
            const callArg = deliveryMethodService.createDeliveryMethod.calls.mostRecent().args[0];
            expect(callArg.name).toBe('New Delivery Method');
            expect(callArg.type).toBe('STANDARD'); // From courier type

            // Check toast and navigation
            expect(toastService.showSuccessToast);
            expect(router.navigate);
        });

        it('should not call API when form is invalid', () => {
            // Keep form invalid (name is required but empty)
            component.saveDeliveryMethod();

            expect(deliveryMethodService.createDeliveryMethod).not.toHaveBeenCalled();
            expect(deliveryMethodService.updateDeliveryMethod).not.toHaveBeenCalled();
        });
    });

    describe('Edit mode', () => {
        beforeEach(() => {
            // Setup edit mode with ID parameter
            activatedRoute.snapshot.params = { id: 1 };
            deliveryMethodService.getDeliveryMethodById.and.returnValue(of());
            component.ngOnInit();
            fixture.detectChanges();
        });

        it('should load the delivery method data', () => {
            expect(deliveryMethodService.getDeliveryMethodById).toHaveBeenCalledWith(1);

            // Check form value was set
            const formValue = component.form.getRawValue();
            expect(formValue.id);
        });

        it('should handle API errors when saving', () => {
            const error = { error: { detail: 'API Error' } };
            deliveryMethodService.updateDeliveryMethod.and.returnValue(throwError(() => error));

            component.saveDeliveryMethod();

            expect(toastService.showErrorToast);
            expect(router.navigate).not.toHaveBeenCalled();
        });


        it('should delete and navigate when deletion is confirmed', () => {
            deliveryMethodService.deleteDeliveryMethodById.and.returnValue(of());

            component.deleteDeliveryMethod();

            expect(deliveryMethodService.deleteDeliveryMethodById);
            expect(toastService.showSuccessToast);
            expect(router.navigate);
        });
    });
});
