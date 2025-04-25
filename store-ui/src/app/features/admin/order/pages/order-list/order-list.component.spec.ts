import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { OrderListComponent } from './order-list.component';
import { OrderService } from '../../../../../services/services/order.service';
import { Router } from '@angular/router';
import { ConfirmationService } from 'primeng/api';
import { ToastService } from '../../../../../shared/services/toast.service';
import { TableModule } from 'primeng/table';
import { Tag } from 'primeng/tag';
import { CustomCurrencyPipe } from '../../../../../shared/pipes/CustomCurrencyPipe';
import { Button } from 'primeng/button';
import { DatePipe } from '@angular/common';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { OrderPageResponse } from '../../../../../services/models/order/order-page-response';
import { OrderOverviewResponse } from '../../../../../services/models/order/order-overview-response';
import { of, throwError } from 'rxjs';

describe('OrderListComponent', () => {
    let component: OrderListComponent;
    let fixture: ComponentFixture<OrderListComponent>;
    let orderServiceSpy: jasmine.SpyObj<OrderService>;
    let confirmationServiceSpy: jasmine.SpyObj<ConfirmationService>;
    let toastServiceSpy: jasmine.SpyObj<ToastService>;
    let routerSpy: jasmine.SpyObj<Router>;

    // Mock data
    const mockOrdersPage: OrderPageResponse = {
        content: [
            {
                id: 1,
                status: 'COMPLETED',
                userDetails: {
                    firstName: 'John',
                    lastName: 'Doe',
                    email: 'john.doe@example.com'
                },
                totalPrice: 1500
            },
            {
                id: 2,
                status: 'PENDING',
                userDetails: {
                    firstName: 'Jane',
                    lastName: 'Smith',
                    email: 'jane.smith@example.com'
                },
                totalPrice: 2500
            }
        ],
        totalElements: 2,
        totalPages: 1,
        number: 0,
        size: 10
    };

    beforeEach(async () => {
        // Create spies
        const orderSpy = jasmine.createSpyObj('OrderService', ['getAllOrders', 'deleteOrderById']);
        const confirmationSpy = jasmine.createSpyObj('ConfirmationService', ['confirm']);
        const toastSpy = jasmine.createSpyObj('ToastService', ['showSuccessToast', 'showErrorToast']);
        const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

        await TestBed.configureTestingModule({
            imports: [
                OrderListComponent,
                TableModule,
                Tag,
                CustomCurrencyPipe,
                Button,
                DatePipe,
                ConfirmDialog
            ],
            providers: [
                { provide: OrderService, useValue: orderSpy },
                { provide: ConfirmationService, useValue: confirmationSpy },
                { provide: ToastService, useValue: toastSpy },
                { provide: Router, useValue: routerSpy }
            ]
        }).compileComponents();

        orderServiceSpy = TestBed.inject(OrderService) as jasmine.SpyObj<OrderService>;
        confirmationServiceSpy = TestBed.inject(ConfirmationService) as jasmine.SpyObj<ConfirmationService>;
        toastServiceSpy = TestBed.inject(ToastService) as jasmine.SpyObj<ToastService>;

        // Setup default return values
        orderServiceSpy.getAllOrders.and.returnValue(of(mockOrdersPage));
        toastServiceSpy.showSuccessToast.and.returnValue(Promise.resolve());
        toastServiceSpy.showErrorToast.and.returnValue(Promise.resolve());
        routerSpy.navigate.and.returnValue(Promise.resolve(true));

        fixture = TestBed.createComponent(OrderListComponent);
        component = fixture.componentInstance;
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should load orders on init', fakeAsync(() => {
        component.ngOnInit();
        tick();

        expect(orderServiceSpy.getAllOrders).toHaveBeenCalledWith({
            pageNumber: 0,
            pageSize: 10,
            attribute: 'id',
            direction: 'desc'
        });
        expect(component.ordersPage).toEqual(mockOrdersPage);
    }));

    it('should handle page change', fakeAsync(() => {
        const pageEvent = {
            first: 10,
            rows: 10
        };

        component.onPageChange(pageEvent);
        tick();

        expect(component.page).toBe(1);
        expect(component.size).toBe(10);
        expect(orderServiceSpy.getAllOrders).toHaveBeenCalledWith({
            pageNumber: 1,
            pageSize: 10,
            attribute: 'id',
            direction: 'desc'
        });
    }));

    it('should delete order and show success toast on confirmation', fakeAsync(() => {
        const order: OrderOverviewResponse = {
            id: 1
        };
        orderServiceSpy.deleteOrderById.and.returnValue(of());

        component.deleteOrder(order);
        tick();

        expect(orderServiceSpy.deleteOrderById);
        expect(orderServiceSpy.getAllOrders);
        expect(toastServiceSpy.showSuccessToast);
    }));

    it('should show error toast when deletion fails', fakeAsync(() => {
        const order: OrderOverviewResponse = {
            id: 1
        };
        const errorResponse = {
            error: {
                detail: 'Order deletion failed'
            }
        };
        orderServiceSpy.deleteOrderById.and.returnValue(throwError(() => errorResponse));

        spyOn(console, 'log');
        component.deleteOrder(order);
        tick();

        expect(orderServiceSpy.deleteOrderById);
        expect(toastServiceSpy.showErrorToast);
    }));

    it('should handle toast errors when showing error message', fakeAsync(() => {
        const order: OrderOverviewResponse = {
            id: 1
        };
        const errorResponse = {
            error: {
                detail: 'Order deletion failed'
            }
        };
        orderServiceSpy.deleteOrderById.and.returnValue(throwError(() => errorResponse));

        spyOn(console, 'log');
        component.deleteOrder(order);
        tick();

        expect(toastServiceSpy.showErrorToast);
    }));

});
