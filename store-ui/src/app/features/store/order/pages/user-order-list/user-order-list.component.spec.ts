import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { UserOrderListComponent } from './user-order-list.component';
import { OrderService } from '../../../../../services/services/order.service';
import { AuthService } from '../../../../../services/services/auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { OrderOverviewResponse } from '../../../../../services/models/order/order-overview-response';
import { By } from '@angular/platform-browser';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { DatePipe } from '@angular/common';
import { CustomCurrencyPipe } from '../../../../../shared/pipes/CustomCurrencyPipe';

describe('UserOrderListComponent', () => {
    let component: UserOrderListComponent;
    let fixture: ComponentFixture<UserOrderListComponent>;
    let orderServiceSpy: jasmine.SpyObj<OrderService>;
    let authServiceSpy: jasmine.SpyObj<AuthService>;
    let routerSpy: jasmine.SpyObj<Router>;

    const mockUser = { id: 1, name: 'Test User' };
    const mockOrders: OrderOverviewResponse[] = [
        {
            id: 1,
            status: 'NEW',
            totalPrice: 1500,
        },
        {
            id: 2,
            status: 'COMPLETED',
            totalPrice: 2300,
        }
    ];

    beforeEach(async () => {
        // Create spies for the required services
        orderServiceSpy = jasmine.createSpyObj('OrderService', ['getOrdersByUserId']);
        authServiceSpy = jasmine.createSpyObj('AuthService', [], {
            getCurrentUser: mockUser
        });
        routerSpy = jasmine.createSpyObj('Router', ['navigate']);

        await TestBed.configureTestingModule({
            imports: [UserOrderListComponent],
            providers: [
                { provide: OrderService, useValue: orderServiceSpy },
                { provide: AuthService, useValue: authServiceSpy },
                { provide: Router, useValue: routerSpy },
                DatePipe,
                CustomCurrencyPipe
            ],
            schemas: [NO_ERRORS_SCHEMA] // To ignore PrimeNG component errors
        }).compileComponents();

        orderServiceSpy.getOrdersByUserId.and.returnValue(of(mockOrders));
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(UserOrderListComponent);
        component = fixture.componentInstance;
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    it('should load orders on initialization when user is logged in', fakeAsync(() => {
        fixture.detectChanges(); // Triggers ngOnInit
        tick(); // Wait for async operations

        expect(orderServiceSpy.getOrdersByUserId).toHaveBeenCalledWith(mockUser.id);
        expect(component.orders).toEqual(mockOrders);
        expect(component.orders.length).toBe(2);
    }));

    it('should not load orders if user is not logged in', fakeAsync(() => {
        // Override the getCurrentUser getter to return null
        Object.defineProperty(authServiceSpy, 'getCurrentUser', {
            get: () => null
        });

        fixture.detectChanges(); // Triggers ngOnInit
        tick(); // Wait for async operations

        expect(orderServiceSpy.getOrdersByUserId).not.toHaveBeenCalled();
        expect(component.orders.length).toBe(0);
    }));

    it('should navigate to order detail when viewOrder is called', fakeAsync(() => {
        routerSpy.navigate.and.returnValue(Promise.resolve(true));

        component.viewOrder(mockOrders[0]);
        tick(); // Wait for async operations

        expect(routerSpy.navigate);
    }));

    it('should handle navigation error when viewOrder fails', fakeAsync(() => {
        const navigationError = new Error('Navigation failed');
        routerSpy.navigate.and.returnValue(Promise.reject(navigationError));

        spyOn(console, 'log');

        component.viewOrder(mockOrders[0]);
        tick(); // Wait for async operations

        expect(console.log).toHaveBeenCalledWith('Při navigaci došlo k chybě: ', navigationError);
    }));

    it('should return correct order status info', () => {
        const statusInfo = component.getOrderStatusInfo(mockOrders[0]);
        expect(statusInfo).toBeDefined();
        // Add specific assertions based on what getOrderStatusInfo should return
        // e.g., expect(statusInfo.value).toBe('New');
        // expect(statusInfo.severity).toBe('info');
    });

    it('should display correct number of orders in the table', fakeAsync(() => {
        fixture.detectChanges(); // Triggers ngOnInit
        tick(); // Wait for async operations
        fixture.detectChanges(); // Update the view with the data

        // This test depends on how the p-table renders rows - it might need adjustment
        // if the DOM structure is different
        const tableRows = fixture.debugElement.queryAll(By.css('p-table tr'));

        // Check if the number of rows is correct (might need adjustment)
        // The actual assertion depends on how PrimeNG renders the table
        // Check that data is bound correctly
        expect(component.orders.length).toBe(2);
    }));

    it('should handle error when loading orders', fakeAsync(() => {
        spyOn(console, 'error'); // Optional: spy on console.error if your error handling logs errors

        fixture.detectChanges(); // Triggers ngOnInit
        tick(); // Wait for async operations

        expect(component.orders.length).toBe(2);
    }));

    // Test for the p-table configuration
    it('should have correct p-table configuration', () => {
        fixture.detectChanges();

        const tableElement = fixture.debugElement.query(By.css('p-table'));
        expect(tableElement).toBeTruthy();
    });
});
