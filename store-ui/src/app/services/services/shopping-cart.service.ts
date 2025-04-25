import { Injectable } from '@angular/core';
import { HttpClient, HttpContext } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';
import { getCartOrCreateByUserId } from "../fn/shopping-cart/get-cart-or-create-by-user-id";
import { CartResponse } from "../models/shopping-cart/cart-response";
import { addItemToCart } from "../fn/shopping-cart/add-item-to-cart";
import { ItemCartRequest } from "../models/shopping-cart/item-cart-request";
import { removeItemFromCart } from "../fn/shopping-cart/remove-item-from-cart";
import { clearCartByUserId } from "../fn/shopping-cart/clear-cart-by-user-id";
import { AuthService } from './auth.service';

@Injectable({
    providedIn: 'root'
})
export class ShoppingCartService extends BaseService {
    private cartItemsCountSubject = new BehaviorSubject<number>(0);
    cartItemsCount$ = this.cartItemsCountSubject.asObservable();

    constructor(
        config: ApiConfiguration,
        http: HttpClient,
        private authService: AuthService
    ) {
        super(config, http);

        const currentUser = this.authService.getCurrentUser;
        if (currentUser?.id) {
            this.getCartOrCreateByUserId(currentUser.id).subscribe();
        }

        this.authService.currentUser$.subscribe(user => {
            if (user?.id) {
                this.getCartOrCreateByUserId(user.id).subscribe();
            } else {
                this.cartItemsCountSubject.next(0);
            }
        });
    }

    private updateCartItemsCount(cart: CartResponse): void {
        const count = cart.items?.reduce((total, item) => total + (item.quantity || 0), 0) || 0;
        this.cartItemsCountSubject.next(count);
    }

    private getCurrentUserId(): number | null {
        const user = this.authService.getCurrentUser;
        return user?.id || null;
    }

    getCartOrCreateByUserId(userId: number, context?: HttpContext): Observable<CartResponse> {
        return getCartOrCreateByUserId(this.http, this.rootUrl, userId, context).pipe(
            map((r: StrictHttpResponse<CartResponse>) => r.body!),
            tap(cart => this.updateCartItemsCount(cart))
        );
    }

    getCartForCurrentUser(context?: HttpContext): Observable<CartResponse | null> {
        const userId = this.getCurrentUserId();
        if (!userId) {
            this.cartItemsCountSubject.next(0);
            return new Observable(subscriber => {
                subscriber.next(null);
                subscriber.complete();
            });
        }

        return this.getCartOrCreateByUserId(userId, context);
    }

    addItemToCart(userId: number, request: ItemCartRequest, context?: HttpContext): Observable<void> {
        return addItemToCart(this.http, this.rootUrl, userId, { requestParam: request }, context).pipe(
            map((r: StrictHttpResponse<void>) => r.body!),
            tap(() => {
                this.getCartOrCreateByUserId(userId).subscribe();
            })
        );
    }

    addItemToCartForCurrentUser(request: ItemCartRequest, context?: HttpContext): Observable<void | null> {
        const userId = this.getCurrentUserId();
        if (!userId) {
            return new Observable(subscriber => {
                subscriber.next(null);
                subscriber.complete();
            });
        }

        return this.addItemToCart(userId, request, context);
    }

    removeItemFromCart(userId: number, request: ItemCartRequest, context?: HttpContext): Observable<void> {
        return removeItemFromCart(this.http, this.rootUrl, userId, { requestParam: request }, context).pipe(
            map((r: StrictHttpResponse<void>) => r.body!),
            tap(() => {
                this.getCartOrCreateByUserId(userId).subscribe();
            })
        );
    }

    removeItemFromCartForCurrentUser(request: ItemCartRequest, context?: HttpContext): Observable<void | null> {
        const userId = this.getCurrentUserId();
        if (!userId) {
            return new Observable(subscriber => {
                subscriber.next(null);
                subscriber.complete();
            });
        }

        return this.removeItemFromCart(userId, request, context);
    }

    clearCartByUserId(userId: number, context?: HttpContext): Observable<void> {
        return clearCartByUserId(this.http, this.rootUrl, userId, context).pipe(
            map((r: StrictHttpResponse<void>) => r.body!),
            tap(() => {
                this.cartItemsCountSubject.next(0);
            })
        );
    }

    clearCartForCurrentUser(context?: HttpContext): Observable<void | null> {
        const userId = this.getCurrentUserId();
        if (!userId) {
            return new Observable(subscriber => {
                subscriber.next(null);
                subscriber.complete();
            });
        }

        return this.clearCartByUserId(userId, context);
    }
}
