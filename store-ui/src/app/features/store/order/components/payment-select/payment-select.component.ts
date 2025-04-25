import {Component, Input, OnInit} from '@angular/core';
import {FormGroup, ReactiveFormsModule} from "@angular/forms";
import {PaymentMethodService} from "../../../../../services/services/payment-method.service";
import {PaymentMethodResponse} from "../../../../../services/models/payment-method/payment-method-response";
import {RadioButton} from "primeng/radiobutton";
import {NgClass} from "@angular/common";
import {Tag} from "primeng/tag";
import {CustomCurrencyPipe} from "../../../../../shared/pipes/CustomCurrencyPipe";

@Component({
    selector: 'app-payment-select',
    imports: [
        ReactiveFormsModule,
        RadioButton,
        Tag,
        CustomCurrencyPipe,
        NgClass
    ],
    templateUrl: './payment-select.component.html',
    standalone: true,
    styleUrl: './payment-select.component.scss'
})
export class PaymentSelectComponent implements OnInit {
    @Input() form!: FormGroup;
    availablePaymentMethods: PaymentMethodResponse[] = [];
    @Input() cartTotal!: number;

    constructor(
        private paymentMethodService: PaymentMethodService
    ) {}

    ngOnInit(): void {
        this.paymentMethodService.getActivePaymentMethods().subscribe({
            next: (paymentMethods) => {
                this.availablePaymentMethods = paymentMethods;
            },
            error: (error) => {
                console.error('Při načítání dostupných platebních metod došlo k chybě:', error);
            }
        });
    }

    getPaymentPrice(paymentMethod: PaymentMethodResponse) {
        if (paymentMethod.price !== 0) {
            if (paymentMethod.isFreeForOrderAbove === false)
                return true;

            return paymentMethod.isFreeForOrderAbove! && paymentMethod.freeForOrderAbove! > this.cartTotal;
        }
        return false;
    }
}
