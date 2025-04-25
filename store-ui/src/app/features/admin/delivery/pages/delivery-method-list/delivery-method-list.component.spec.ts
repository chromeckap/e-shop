import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { DeliveryMethodListComponent } from './delivery-method-list.component';
import { DeliveryMethodService } from '../../../../../services/services/delivery-method.service';
import { ToastService } from '../../../../../shared/services/toast.service';
import { Router } from '@angular/router';
import { ConfirmationService } from 'primeng/api';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { DeliveryMethodResponse } from '../../../../../services/models/delivery-method/delivery-method-response';
import { Table } from 'primeng/table';

describe('DeliveryMethodListComponent', () => {
    let component: DeliveryMethodListComponent;
    let fixture: ComponentFixture<DeliveryMethodListComponent>;
    let deliveryMethodService: jasmine.SpyObj<DeliveryMethodService>;
    let toastService: jasmine.SpyObj<ToastService>;
    let router: jasmine.SpyObj<Router>;
    let confirmationService: jasmine.SpyObj<ConfirmationService>;

    // Mock delivery methods
    const mockDeliveryMethods: DeliveryMethodResponse[] = [
        {
            id: 1,
            name: 'Standard Delivery',
            courierType: new Map([['id', '1'], ['name', 'Standard']]),
            price: 100,
            isActive: true,
            isFreeForOrderAbove: true,
            freeForOrderAbove: 1000
        },
        {
            id: 2,
            name: 'Express Delivery',
            courierType: new Map([['id', '2'], ['name', 'Express']]),
            price: 200,
            isActive: true,
            isFreeForOrderAbove: false,
            freeForOrderAbove: 0
        },
        {
            id: 3,
            name: 'Inactive Delivery',
            courierType: new Map([['id', '3'], ['name', 'Economy']]),
            price: 150,
            isActive: false,
            isFreeForOrderAbove: false,
            freeForOrderAbove: 0
        }
    ];

    beforeEach(async () => {
        // Create spies
        deliveryMethodService = jasmine.createSpyObj('DeliveryMethodService', [
            'getAllDeliveryMethods',
            'deleteDeliveryMethodById'
        ]);
        toastService = jasmine.createSpyObj('ToastService', ['showSuccessToast', 'showErrorToast']);
        router = jasmine.createSpyObj('Router', ['navigate']);
        confirmationService = jasmine.createSpyObj('ConfirmationService', ['confirm']);

        // Setup default return values
        deliveryMethodService.getAllDeliveryMethods.and.returnValue(of(mockDeliveryMethods));
        toastService.showSuccessToast.and.returnValue(Promise.resolve());
        toastService.showErrorToast.and.returnValue(Promise.resolve());
        router.navigate.and.returnValue(Promise.resolve(true));

        await TestBed.configureTestingModule({
            imports: [DeliveryMethodListComponent],
            providers: [
                { provide: DeliveryMethodService, useValue: deliveryMethodService },
                { provide: ToastService, useValue: toastService },
                { provide: Router, useValue: router },
                { provide: ConfirmationService, useValue: confirmationService }
            ],
            schemas: [NO_ERRORS_SCHEMA]
        }).compileComponents();

        fixture = TestBed.createComponent(DeliveryMethodListComponent);
        component = fixture.componentInstance;
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    describe('Initialization', () => {
        it('should load delivery methods on init', () => {
            component.ngOnInit();
            expect(deliveryMethodService.getAllDeliveryMethods).toHaveBeenCalled();
            expect(component.deliveryMethods).toEqual(mockDeliveryMethods);
        });
    });

    describe('Navigation', () => {
        it('should navigate to create page', () => {
            component.createDeliveryMethod();
            expect(router.navigate).toHaveBeenCalledWith(['admin/dopravni-metody/vytvorit']);
        });

        it('should navigate to edit page with proper ID', () => {
            const deliveryMethod = mockDeliveryMethods[0];
            component.editDeliveryMethod(deliveryMethod);
            expect(router.navigate).toHaveBeenCalledWith(['admin/dopravni-metody/upravit', deliveryMethod.id]);
        });

        it('should handle navigation errors gracefully', fakeAsync(() => {
            // Setup router to reject
            router.navigate.and.returnValue(Promise.reject('Navigation error'));

            // Spy on console.log
            spyOn(console, 'log');

            component.createDeliveryMethod();
            tick(); // Wait for the Promise to resolve/reject

            expect(console.log).toHaveBeenCalledWith("Při navigaci došlo k chybě: ", 'Navigation error');
        }));
    });

    describe('Table filtering', () => {
        it('should call table filterGlobal when onGlobalFilter is called', () => {
            const tableMock = jasmine.createSpyObj('Table', ['filterGlobal']);
            const event = { target: { value: 'search text' } } as unknown as Event;

            component.onGlobalFilter(tableMock as unknown as Table, event);

            expect(tableMock.filterGlobal).toHaveBeenCalledWith('search text', 'contains');
        });
    });
});
