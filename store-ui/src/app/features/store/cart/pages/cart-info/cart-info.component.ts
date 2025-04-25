import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ShoppingCartService} from "../../../../../services/services/shopping-cart.service";
import {CartResponse} from "../../../../../services/models/shopping-cart/cart-response";
import {CartItemListComponent} from "../../components/cart-item-list/cart-item-list.component";
import {ItemCartRequest} from "../../../../../services/models/shopping-cart/item-cart-request";
import {ToastService} from "../../../../../shared/services/toast.service";
import {Button} from "primeng/button";
import {Router} from "@angular/router";

@Component({
    selector: 'app-cart-info',
    imports: [
        CartItemListComponent,
        Button
    ],
    templateUrl: './cart-info.component.html',
    standalone: true,
    styleUrl: './cart-info.component.scss'
})
export class CartInfoComponent implements OnInit {
    cart: CartResponse | null = {};
    @Input() showNextButton: boolean = true;
    @Output() cartTotalChange = new EventEmitter<number>();

    constructor(
        private shoppingCartService: ShoppingCartService,
        private toastService: ToastService,
        private router: Router
    ) {}

    ngOnInit(): void {
        this.loadCart();
    }

    loadCart(): void {
        this.shoppingCartService.getCartForCurrentUser().subscribe({
            next: (cart) => {
                this.cart = cart;

                if (cart && cart.totalPrice) {
                    this.cartTotalChange.emit(cart.totalPrice);
                }
            },
            error: (error) => {
                console.error('Chyba při načítání košíku:', error);
            }
        });
    }

    onItemRemove(variantId: number) {
        if (!variantId) return;

        const variant: ItemCartRequest = {
            productId: variantId
        }

        this.shoppingCartService.removeItemFromCartForCurrentUser(variant)
            .subscribe({
                next: async () => {
                    try {
                        this.loadCart();
                        await this.toastService.showSuccessToast('Úspěch', 'Varianta byla úspěšně odebrána z košíku.');
                    } catch (error) {
                        console.log("Chyba při zobrazení toastu:", error);
                    }
                },
                error: async (error) => {
                    console.log(error);
                    try {
                        await this.toastService.showErrorToast('Chyba', error.error.detail);
                    } catch (toastError) {
                        console.log("Chyba při zobrazení chybového toastu:", toastError);
                    }
                }
            });
    }

    onItemQuantityChange(event: {variantId: number; quantity: number}) {
        const request: ItemCartRequest = {
            productId: event.variantId,
            quantity: event.quantity
        };

        this.shoppingCartService.addItemToCartForCurrentUser(request).subscribe({
            next: async () => {
                try {
                    this.loadCart();
                    await this.toastService.showSuccessToast('Úspěch', 'Množství varianty bylo úspěšně změněno.');
                } catch (error) {
                    console.log("Chyba při zobrazení toastu :", error);
                }
            },
            error: async (error) => {
                console.log(error);
                try {
                    await this.toastService.showErrorToast('Chyba', error.error.detail);
                } catch (toastError) {
                    console.log("Chyba při zobrazení chybového toastu:", toastError);
                }
            }
        });
    }

    navigateToCheckout() {
        this.router.navigate(['/pokladna'])
            .catch(error => {
                console.error('Chyba při navigaci:', error);
            });
    }
}
