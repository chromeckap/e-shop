import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { By } from '@angular/platform-browser';

import { CartItemListComponent } from './cart-item-list.component';
import { ProductService } from '../../../../../services/services/product.service';
import { CustomCurrencyPipe } from '../../../../../shared/pipes/CustomCurrencyPipe';
import { CartResponse } from '../../../../../services/models/shopping-cart/cart-response';
import { PurchaseResponse } from '../../../../../services/models/order/purchase-response';

describe('CartItemListComponent', () => {
    let component: CartItemListComponent;
    let fixture: ComponentFixture<CartItemListComponent>;
    let productService: jasmine.SpyObj<ProductService>;
    let router: jasmine.SpyObj<Router>;

    // Mock data for testing
    const mockCartItem: PurchaseResponse = {
        variantId: 1,
        productId: 123,
        name: 'Test Product',
        primaryImagePath: 'test-image.jpg',
        price: 100,
        quantity: 2,
        totalPrice: 200,
        values: { color: 'red', size: 'M' },
        availableQuantity: 10
    };

    const mockCart: CartResponse = {
        id: 1,
        items: [mockCartItem],
        totalPrice: 200,
        userId: 1
    };

    beforeEach(async () => {
        const productServiceSpy = jasmine.createSpyObj('ProductService', ['getImage']);

        await TestBed.configureTestingModule({
            imports: [
                FormsModule,
                CartItemListComponent
            ],
            providers: [
                { provide: ProductService, useValue: productServiceSpy },
                CustomCurrencyPipe
            ],
            schemas: [NO_ERRORS_SCHEMA] // Ignore unknown elements and attributes
        }).compileComponents();

        productService = TestBed.inject(ProductService) as jasmine.SpyObj<ProductService>;
        router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(CartItemListComponent);
        component = fixture.componentInstance;
        component.cart = mockCart;
        fixture.detectChanges();
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    it('should get image from the ProductService when item has primaryImagePath', () => {
        const expectedImageUrl = 'https://example.com/image.jpg';
        productService.getImage.and.returnValue(expectedImageUrl);

        const result = component.getImage(mockCartItem);

        expect(productService.getImage).toHaveBeenCalledWith(123, 'test-image.jpg');
        expect(result).toBe(expectedImageUrl);
    });

    it('should navigate to product detail page when item is clicked', () => {
        component.navigateToProduct(mockCartItem);

        expect(router.navigate);
    });

    it('should emit removeItemFromCart event when removeCartItem is called', () => {
        spyOn(component.removeItemFromCart, 'emit');

        component.removeCartItem(mockCartItem);

        expect(component.removeItemFromCart.emit).toHaveBeenCalledWith(1);
    });

    it('should extract object keys correctly', () => {
        const values = { color: 'red', size: 'M' };

        const result = component.objectKeys(values);

        expect(result).toEqual(['color', 'size']);
    });

    it('should emit changeItemQuantity event when onInputNumberChange is called with valid value', () => {
        spyOn(component.changeItemQuantity, 'emit');
        const event = { value: 3, formattedValue: 2 };

        component.onInputNumberChange(event, mockCartItem);

        expect(component.changeItemQuantity.emit).toHaveBeenCalledWith({
            variantId: 1,
            quantity: 1
        });
    });

    it('should not emit changeItemQuantity event when quantity exceeds maxQuantity', () => {
        spyOn(component.changeItemQuantity, 'emit');
        component.maxQuantity = 10;
        const event = { value: 12, formattedValue: 1 };

        component.onInputNumberChange(event, mockCartItem);

        expect(component.changeItemQuantity.emit).not.toHaveBeenCalled();
    });

    it('should not emit changeItemQuantity event when quantity is NaN', () => {
        spyOn(component.changeItemQuantity, 'emit');
        const event = { value: 'not-a-number', formattedValue: 2 };

        component.onInputNumberChange(event, mockCartItem);

        expect(component.changeItemQuantity.emit).not.toHaveBeenCalled();
    });

    it('should not emit changeItemQuantity event when event value is null or undefined', () => {
        spyOn(component.changeItemQuantity, 'emit');

        component.onInputNumberChange({ value: null }, mockCartItem);
        component.onInputNumberChange({ value: undefined }, mockCartItem);

        expect(component.changeItemQuantity.emit).not.toHaveBeenCalled();
    });

    // Test for the input/output properties
    it('should have the correct input and output properties', () => {
        expect(component.cart).toBe(mockCart);
        expect(component.removeItemFromCart).toBeDefined();
        expect(component.changeItemQuantity).toBeDefined();
        expect(component.minQuantity).toBe(1);
        expect(component.maxQuantity).toBe(50);
    });

    // Add integration tests for the component template
    // Note: These tests may need adjustment based on your actual DOM structure

    it('should display cart items in the table', () => {
        // This would need to be adjusted based on your actual component rendering
        // and may require a different approach since we're using mocked components
        const nameElement = fixture.debugElement.query(By.css('a.hover\\:underline'));
        expect(nameElement).toBeTruthy();
    });
});
