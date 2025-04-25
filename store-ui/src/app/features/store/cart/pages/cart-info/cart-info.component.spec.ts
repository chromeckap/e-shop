import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

import { CartInfoComponent } from './cart-info.component';
import { ShoppingCartService } from '../../../../../services/services/shopping-cart.service';
import { ToastService } from '../../../../../shared/services/toast.service';
import { CartResponse } from '../../../../../services/models/shopping-cart/cart-response';
import {provideHttpClient} from "@angular/common/http";

describe('CartInfoComponent', () => {
    let component: CartInfoComponent;
    let fixture: ComponentFixture<CartInfoComponent>;
    let shoppingCartService: jasmine.SpyObj<ShoppingCartService>;
    let toastService: jasmine.SpyObj<ToastService>;
    let router: jasmine.SpyObj<Router>;

    // Mock data
    const mockCart: CartResponse = {
        id: 1,
        items: [
            {
                variantId: 101,
                productId: 201,
                name: 'Test Product',
                price: 150,
                quantity: 2,
                totalPrice: 300,
                values: { color: 'blue', size: 'L' },
                availableQuantity: 10
            }
        ],
        totalPrice: 300,
        userId: 1
    };

    beforeEach(async () => {
        const shoppingCartServiceSpy = jasmine.createSpyObj('ShoppingCartService', [
            'getCartForCurrentUser',
            'removeItemFromCartForCurrentUser',
            'addItemToCartForCurrentUser'
        ]);
        const toastServiceSpy = jasmine.createSpyObj('ToastService', [
            'showSuccessToast',
            'showErrorToast'
        ]);
        const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

        await TestBed.configureTestingModule({
            imports: [
                CartInfoComponent
            ],
            providers: [
                { provide: ShoppingCartService, useValue: shoppingCartServiceSpy },
                { provide: ToastService, useValue: toastServiceSpy },
                { provide: Router, useValue: routerSpy },
                provideHttpClient(),
            ],
            schemas: [NO_ERRORS_SCHEMA]
        }).compileComponents();

        shoppingCartService = TestBed.inject(ShoppingCartService) as jasmine.SpyObj<ShoppingCartService>;
        toastService = TestBed.inject(ToastService) as jasmine.SpyObj<ToastService>;
        router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(CartInfoComponent);
        component = fixture.componentInstance;

        // Setup default successful response
        shoppingCartService.getCartForCurrentUser.and.returnValue(of(mockCart));

        // Setup toast service to return resolved promises
        toastService.showSuccessToast.and.returnValue(Promise.resolve());
        toastService.showErrorToast.and.returnValue(Promise.resolve());

        fixture.detectChanges();
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    it('should load cart on initialization', () => {
        expect(shoppingCartService.getCartForCurrentUser).toHaveBeenCalled();
        expect(component.cart).toEqual(mockCart);
    });

    it('should emit cartTotalChange when cart is loaded with totalPrice', () => {
        spyOn(component.cartTotalChange, 'emit');
        component.loadCart();
        expect(component.cartTotalChange.emit).toHaveBeenCalledWith(300);
    });

    it('should handle error when loading cart fails', () => {
        shoppingCartService.getCartForCurrentUser.and.returnValue(throwError(() => new Error('Test error')));
        spyOn(console, 'error');

        component.loadCart();

        expect(console.error).toHaveBeenCalledWith('Chyba při načítání košíku:', jasmine.any(Error));
    });

    it('should not emit cartTotalChange when cart or totalPrice is undefined', () => {
        const emptyCart: CartResponse = { id: 1 };
        shoppingCartService.getCartForCurrentUser.and.returnValue(of(emptyCart));

        spyOn(component.cartTotalChange, 'emit');
        component.loadCart();

        expect(component.cartTotalChange.emit).not.toHaveBeenCalled();
    });

    it('should remove item from cart', fakeAsync(() => {
        shoppingCartService.removeItemFromCartForCurrentUser.and.returnValue(of());
        spyOn(component, 'loadCart');

        component.onItemRemove(101);
        tick();

        expect(shoppingCartService.removeItemFromCartForCurrentUser).toHaveBeenCalledWith({
            productId: 101
        });
        expect(component.loadCart);
        expect(toastService.showSuccessToast);
    }));

    it('should not remove item when variantId is falsy', () => {
        component.onItemRemove(0);
        expect(shoppingCartService.removeItemFromCartForCurrentUser).not.toHaveBeenCalled();
    });

    it('should handle error when removing item fails', fakeAsync(() => {
        const errorResponse = {
            error: {
                detail: 'Cannot remove item'
            }
        };
        shoppingCartService.removeItemFromCartForCurrentUser.and.returnValue(throwError(() => errorResponse));
        spyOn(console, 'log');

        component.onItemRemove(101);
        tick();

        expect(console.log).toHaveBeenCalledWith(errorResponse);
        expect(toastService.showErrorToast).toHaveBeenCalledWith('Chyba', 'Cannot remove item');
    }));

    it('should handle toast error when showing error toast fails', fakeAsync(() => {
        const errorResponse = { error: { detail: 'Cannot remove item' } };
        shoppingCartService.removeItemFromCartForCurrentUser.and.returnValue(throwError(() => errorResponse));

        toastService.showErrorToast.and.returnValue(Promise.reject('Toast error'));
        spyOn(console, 'log');

        component.onItemRemove(101);
        tick();

        expect(console.log).toHaveBeenCalledWith("Chyba při zobrazení chybového toastu:", 'Toast error');
    }));

    it('should change item quantity', fakeAsync(() => {
        shoppingCartService.addItemToCartForCurrentUser.and.returnValue(of());
        spyOn(component, 'loadCart');

        component.onItemQuantityChange({ variantId: 101, quantity: 3 });
        tick();

        expect(shoppingCartService.addItemToCartForCurrentUser).toHaveBeenCalledWith({
            productId: 101,
            quantity: 3
        });
        expect(component.loadCart);
        expect(toastService.showSuccessToast);
    }));

    it('should handle error when changing quantity fails', fakeAsync(() => {
        const errorResponse = {
            error: {
                detail: 'Cannot change quantity'
            }
        };
        shoppingCartService.addItemToCartForCurrentUser.and.returnValue(throwError(() => errorResponse));
        spyOn(console, 'log');

        component.onItemQuantityChange({ variantId: 101, quantity: 3 });
        tick();

        expect(console.log).toHaveBeenCalledWith(errorResponse);
        expect(toastService.showErrorToast).toHaveBeenCalledWith('Chyba', 'Cannot change quantity');
    }));

    it('should navigate to checkout page', () => {
        router.navigate.and.returnValue(Promise.resolve(true));

        component.navigateToCheckout();

        expect(router.navigate).toHaveBeenCalledWith(['/pokladna']);
    });

    it('should handle error when navigation fails', async () => {
        router.navigate.and.returnValue(Promise.reject('Navigation error'));
        spyOn(console, 'error');

        await component.navigateToCheckout();

        expect(console.error).toHaveBeenCalledWith('Chyba při navigaci:', 'Navigation error');
    });
});
