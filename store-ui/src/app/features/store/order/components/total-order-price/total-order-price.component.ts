import {Component, Input} from '@angular/core';
import {TableModule} from "primeng/table";
import {CustomCurrencyPipe} from "../../../../../shared/pipes/CustomCurrencyPipe";
import {Divider} from "primeng/divider";
import {FormGroup} from "@angular/forms";
import {Tag} from "primeng/tag";

@Component({
    selector: 'app-total-order-price',
    imports: [
        TableModule,
        CustomCurrencyPipe,
        Divider,
        Tag
    ],
    templateUrl: './total-order-price.component.html',
    standalone: true,
    styleUrl: './total-order-price.component.scss'
})
export class TotalOrderPriceComponent {
    @Input() form!: FormGroup;
    @Input() cartTotal!: number;

    getPaymentPrice() {
        const paymentMethod = this.form.get('paymentMethod')?.value;

        if (!paymentMethod) return "0";

        if (paymentMethod.price === undefined) return "0";

        if (paymentMethod.price === 0) return paymentMethod.price;

        if (paymentMethod.isFreeForOrderAbove === false)
            return paymentMethod.price;

        if (paymentMethod.isFreeForOrderAbove! && paymentMethod.freeForOrderAbove! > this.cartTotal) {
            return paymentMethod.price;
        } else {
            return 0;
        }
    }

    getDeliveryPrice() {
        const deliveryMethod = this.form.get('deliveryMethod')?.value;

        if (!deliveryMethod || deliveryMethod.price === undefined) return "0";

        if (deliveryMethod.price === 0) return deliveryMethod.price;

        if (deliveryMethod.isFreeForOrderAbove === false)
            return deliveryMethod.price;

        if (deliveryMethod.isFreeForOrderAbove! && deliveryMethod.freeForOrderAbove! > this.cartTotal) {
            return deliveryMethod.price;
        } else {
            return 0;
        }
    }

    getTotalPrice() {
        let totalPrice: number = 0;
        totalPrice += this.cartTotal || 0;

        const paymentPrice = this.getPaymentPrice();
        if (paymentPrice !== "0" && paymentPrice > 0) {
            totalPrice += paymentPrice;
        }

        const deliveryPrice = this.getDeliveryPrice();
        if (deliveryPrice !== "0" && deliveryPrice > 0) {
            totalPrice += deliveryPrice;
        }

        return totalPrice;
    }
}
