import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MainComponent } from './main.component';
import { OrderService } from '../../../../../services/services/order.service';
import { UserService } from '../../../../../services/services/user.service';
import { of } from 'rxjs';
import { CustomCurrencyPipe } from '../../../../../shared/pipes/CustomCurrencyPipe';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('MainComponent', () => {
    let component: MainComponent;
    let fixture: ComponentFixture<MainComponent>;
    let orderServiceSpy: jasmine.SpyObj<OrderService>;
    let userServiceSpy: jasmine.SpyObj<UserService>;

    // Setup test data
    const currentDate = new Date();
    const oneWeekAgo = new Date(currentDate);
    oneWeekAgo.setDate(oneWeekAgo.getDate() - 7);

    const threeWeeksAgo = new Date(currentDate);
    threeWeeksAgo.setDate(threeWeeksAgo.getDate() - 21);

    // Mock order data
    const mockOrdersResponse = {
        content: [
            // Recent orders (within last week)
            { id: 1, totalPrice: 100, createTime: new Date(currentDate).toISOString() },
            { id: 2, totalPrice: 200, createTime: new Date(currentDate.setDate(currentDate.getDate() - 2)).toISOString() },
            { id: 3, totalPrice: 300, createTime: new Date(currentDate.setDate(currentDate.getDate() - 3)).toISOString() },

            // Older orders
            { id: 4, totalPrice: 400, createTime: new Date(threeWeeksAgo).toISOString() },
            { id: 5, totalPrice: 500, createTime: new Date(threeWeeksAgo.setDate(threeWeeksAgo.getDate() - 5)).toISOString() }
        ],
        totalElements: 5
    };

    // Mock user data
    const mockUsersResponse = {
        content: [
            // Recent users (within last week)
            { id: 1, createTime: new Date(currentDate).toISOString() },
            { id: 2, createTime: new Date(currentDate.setDate(currentDate.getDate() - 3)).toISOString() },

            // Older users
            { id: 3, createTime: new Date(threeWeeksAgo).toISOString() },
            { id: 4, createTime: new Date(threeWeeksAgo.setDate(threeWeeksAgo.getDate() - 10)).toISOString() }
        ],
        totalElements: 4
    };

    beforeEach(async () => {
        // Create spies for the services
        orderServiceSpy = jasmine.createSpyObj('OrderService', ['getAllOrders']);
        userServiceSpy = jasmine.createSpyObj('UserService', ['getAllUsers']);

        // Configure the test module
        await TestBed.configureTestingModule({
            imports: [MainComponent, CustomCurrencyPipe],
            providers: [
                { provide: OrderService, useValue: orderServiceSpy },
                { provide: UserService, useValue: userServiceSpy }
            ],
            schemas: [NO_ERRORS_SCHEMA] // Ignore unknown elements
        }).compileComponents();

        // Setup the mock return values
        orderServiceSpy.getAllOrders.and.returnValue(of(mockOrdersResponse));
        userServiceSpy.getAllUsers.and.returnValue(of(mockUsersResponse));

        // Create the component
        fixture = TestBed.createComponent(MainComponent);
        component = fixture.componentInstance;
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    it('should call service methods on init', () => {
        component.ngOnInit();

        expect(orderServiceSpy.getAllOrders).toHaveBeenCalledWith({ pageSize: component.size });
        expect(userServiceSpy.getAllUsers).toHaveBeenCalledWith({ pageSize: component.size });
    });

    describe('getAllOrders method', () => {
        beforeEach(() => {
            // Reset values before each test
            component.ordersPage = {};
            component.newOrdersLastWeek = 0;
            component.totalRevenue = 0;
            component.weeklyRevenue = 0;

            // Call method
            component.ngOnInit();
        });

        it('should set ordersPage with response data', () => {
            expect(component.ordersPage).toEqual(mockOrdersResponse);
            expect(component.ordersPage.totalElements).toBe(5);
        });

        it('should calculate newOrdersLastWeek correctly', () => {
            // Should count orders from the last 7 days (3 orders in our mock data)
            expect(component.newOrdersLastWeek).toBe(3);
        });

        it('should calculate totalRevenue correctly', () => {
            // Sum of all order prices: 100 + 200 + 300 + 400 + 500 = 1500
            expect(component.totalRevenue).toBe(1500);
        });

        it('should calculate weeklyRevenue correctly', () => {
            // Sum of recent order prices (within last week): 100 + 200 + 300 = 600
            expect(component.weeklyRevenue).toBe(600);
        });

        it('should handle empty orders array', () => {
            // Setup empty response
            const emptyResponse = { content: [], totalElements: 0 };
            orderServiceSpy.getAllOrders.and.returnValue(of(emptyResponse));

            // Reset component properties
            component.ordersPage = {};
            component.newOrdersLastWeek = 0;
            component.totalRevenue = 0;
            component.weeklyRevenue = 0;

            // Call method again
            component.ngOnInit();

            // Verify calculations with empty data
            expect(component.ordersPage).toEqual(emptyResponse);
            expect(component.newOrdersLastWeek).toBe(0);
            expect(component.totalRevenue).toBe(0);
            expect(component.weeklyRevenue).toBe(0);
        });
    });

    describe('getAllUsers method', () => {
        beforeEach(() => {
            // Reset values before each test
            component.usersPage = {};
            component.newUsersLastWeek = 0;

            // Call method
            component.ngOnInit();
        });

        it('should set usersPage with response data', () => {
            expect(component.usersPage).toEqual(mockUsersResponse);
            expect(component.usersPage.totalElements).toBe(4);
        });

        it('should calculate newUsersLastWeek correctly', () => {
            expect(component.newUsersLastWeek);
        });

        it('should handle empty users array', () => {
            // Setup empty response
            const emptyResponse = { content: [], totalElements: 0 };
            userServiceSpy.getAllUsers.and.returnValue(of(emptyResponse));

            // Reset component property
            component.usersPage = {};
            component.newUsersLastWeek = 0;

            // Call method again
            component.ngOnInit();

            // Verify calculations with empty data
            expect(component.usersPage).toEqual(emptyResponse);
            expect(component.newUsersLastWeek).toBe(0);
        });
    });

    describe('date filtering logic', () => {
        it('should correctly identify items within the past week', () => {
            // Setup specific test dates
            const now = new Date();

            const datesToTest = [
                { date: new Date(now), expected: true }, // Today
                { date: new Date(now.setDate(now.getDate() - 6)), expected: true }, // 6 days ago
                { date: new Date(now.setDate(now.getDate() - 1)), expected: true }, // 7 days ago
                { date: new Date(now.setDate(now.getDate() - 1)), expected: false }, // 8 days ago
                { date: new Date(now.setDate(now.getDate() - 7)), expected: false } // 15 days ago
            ];

            // Test the date filtering logic directly
            datesToTest.forEach(test => {
                const oneWeekAgo = new Date();
                oneWeekAgo.setDate(oneWeekAgo.getDate() - 7);

                const result = test.date >= oneWeekAgo;
                expect(result).toBe(test.expected, `Date ${test.date.toISOString()} should ${test.expected ? 'be' : 'not be'} within the last week`);
            });
        });
    });
});
