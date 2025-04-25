import {Component, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {DeliverySelectComponent} from "../../components/delivery-select/delivery-select.component";
import {PaymentSelectComponent} from "../../components/payment-select/payment-select.component";
import {CartInfoComponent} from "../../../cart/pages/cart-info/cart-info.component";
import {AdditionalAddressComponent} from "../../components/additional-address/additional-address.component";
import {TotalOrderPriceComponent} from "../../components/total-order-price/total-order-price.component";
import {Button} from "primeng/button";
import {OrderRequest} from "../../../../../services/models/order/order-request";
import {AddressRequest} from "../../../../../services/models/address/address-request";
import {UserDetailsRequest} from "../../../../../services/models/user-details/user-details-request";
import {AuthService} from "../../../../../services/services/auth.service";
import {PurchaseRequest} from "../../../../../services/models/order/purchase-request";
import {OrderService} from "../../../../../services/services/order.service";
import {ToastService} from "../../../../../shared/services/toast.service";
import {Router} from "@angular/router";
import {CustomCurrencyPipe} from "../../../../../shared/pipes/CustomCurrencyPipe";

@Component({
    selector: 'app-checkout',
    imports: [
        DeliverySelectComponent,
        PaymentSelectComponent,
        CartInfoComponent,
        AdditionalAddressComponent,
        TotalOrderPriceComponent,
        Button,
        CustomCurrencyPipe
    ],
    templateUrl: './checkout.component.html',
    standalone: true,
    styleUrl: './checkout.component.scss'
})
export class CheckoutComponent {
    form: FormGroup;
    cartTotal: number = 0;
    @ViewChild(CartInfoComponent) cartInfo!: CartInfoComponent;

    MAX_PRODUCT_PRICE: number = 100_000.0;

    constructor(
        private orderService: OrderService,
        private authService: AuthService,
        private toastService: ToastService,
        private router: Router,
        private formBuilder: FormBuilder
    ) {
        this.form = this.formBuilder.group({
            paymentMethod: [Validators.required],
            deliveryMethod: [Validators.required],
            isManualAddressRequired: [false],
            street: ['', Validators.required],
            city: ['', Validators.required],
            postalCode: ['', Validators.required],
        });
    }

    onCartTotalChange(total: number): void {
        this.cartTotal = total;
    }

    createOrder() {
        if (this.form.invalid) return;

        const products = this.cartInfo.cart?.items?.map(item => {
            return {
                id: item.variantId,
                quantity: item.quantity
            } as PurchaseRequest;
        }) || [];

        if (products.length < 1) {
            this.toastService.showErrorToast('Chyba', 'V košíku musí být alespoň 1 produkt.')
                .catch();
            return;
        }

        const address: AddressRequest = {
            street: this.form.getRawValue().street,
            city: this.form.getRawValue().city,
            postalCode: this.form.getRawValue().postalCode
        };

        const user = this.authService.getCurrentUser;
        const userDetails: UserDetailsRequest = {
            id: user?.id,
            firstName: user?.firstName,
            lastName: user?.lastName,
            email: user?.email,
            address: address
        };

        const request: OrderRequest = {
            userDetails: userDetails,
            products: products,
            paymentMethodId: this.form.getRawValue().paymentMethod.id,
            deliveryMethodId: this.form.getRawValue().deliveryMethod.id
        };

        this.orderService.createOrder(request).subscribe({
            next: async (id: number) => {
                try {
                    this.cartInfo.loadCart();
                    await this.router.navigate(['/moje-objednavky/' + id]);
                    await this.toastService.showSuccessToast('Úspěch', 'Objednávka byla úspěšně vytvořena.');
                } catch (error) {
                    console.log("Chyba při zobrazení toastu nebo navigaci:", error);
                }
            },
            error: async (error) => {
                try {
                    await this.toastService.showErrorToast('Chyba', error.error.detail);
                } catch (toastError) {
                    console.log("Chyba při zobrazení chybového toastu:", toastError);
                }
            }
        });

    }
}
