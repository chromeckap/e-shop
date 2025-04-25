import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ItemListComponent } from './item-list.component';
import { OrderResponse } from '../../../../../services/models/order/order-response';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { CustomCurrencyPipe } from '../../../../../shared/pipes/CustomCurrencyPipe';

describe('ItemListComponent', () => {
    let component: ItemListComponent;
    let fixture: ComponentFixture<ItemListComponent>;

    // Mock order data
    const mockOrder: OrderResponse = {
        id: 1,
        totalPrice: 1500,
        items: [
            {
                id: 101,
                name: 'Product 1',
                price: 250,
                quantity: 2,
                totalPrice: 500,
                values: []
            },
            {
                id: 102,
                name: 'Product 2',
                price: 750,
                quantity: 1,
                totalPrice: 750,
                values: []
            }
        ],
        additionalCosts: [
            {'Shipping': 150},
            {'Tax': 100}
        ]
    };

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [
                ItemListComponent,
                CustomCurrencyPipe
            ],
            schemas: [NO_ERRORS_SCHEMA]
        }).compileComponents();

        fixture = TestBed.createComponent(ItemListComponent);
        component = fixture.componentInstance;

        // Provide the mock order data
        component.order = mockOrder;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    describe('Data transformation', () => {
        it('should correctly transform order items and additional costs', () => {
            const tableData = component.orderDataTable;

            // Should have 4 rows (2 products + 2 additional costs)
            expect(tableData.length).toBe(4);

            // Check product items
            expect(tableData[0].type).toBe('product');
            expect(tableData[0].name).toBe('Product 1');
            expect(tableData[0].price).toBe(250);

            expect(tableData[1].type).toBe('product');
        });

        it('should handle missing items gracefully', () => {
            component.order = { id: 1, totalPrice: 0 }; // Order with no items
            const tableData = component.orderDataTable;

            expect(tableData).toEqual([]);
        });

        it('should handle missing additionalCosts gracefully', () => {
            component.order = {
                id: 1,
                totalPrice: 500,
                items: [{ id: 101, name: 'Product', price: 500, quantity: 1, totalPrice: 500, values: [] }]
            };

            const tableData = component.orderDataTable;

            expect(tableData.length).toBe(1);
            expect(tableData[0].type).toBe('product');
        });
    });

    describe('Helper methods', () => {
        it('should extract object keys correctly', () => {
            const testObject = { key1: 'value1', key2: 'value2' };

            const keys = component.objectKeys(testObject);

            expect(keys).toEqual(['key1', 'key2']);
        });

        it('should handle empty objects', () => {
            const keys = component.objectKeys({});
            expect(keys).toEqual([]);
        });
    });
});
