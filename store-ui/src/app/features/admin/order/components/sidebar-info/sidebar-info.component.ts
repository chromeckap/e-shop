import {Component, Input} from '@angular/core';
import {OrderResponse} from "../../../../../services/models/order/order-response";
import {Divider} from "primeng/divider";
import {Tag} from "primeng/tag";
import {DatePipe} from "@angular/common";
import {PaymentService} from "../../../../../services/services/payment.service";
import {PaymentResponse} from "../../../../../services/models/payment/payment-response";
import {getOrderStatusInfo} from "../../services/get-order-status-info";
import {getPaymentStatusInfo} from "../../services/get-payment-status-info";

@Component({
    selector: 'app-sidebar-info',
    imports: [
        Divider,
        Tag,
        DatePipe
    ],
    templateUrl: './sidebar-info.component.html',
    standalone: true,
    styleUrl: './sidebar-info.component.scss'
})
export class SidebarInfoComponent {
    @Input() order!: OrderResponse;

    payment: PaymentResponse = {};

    protected readonly getPaymentStatusInfo = getPaymentStatusInfo;

    constructor(
        private paymentService: PaymentService
    ) {}

    getPaymentByOrderId(id: number) {
        this.paymentService.getPaymentByOrderId(id).subscribe({
            next: (payment) => {
                this.payment = payment;
                console.log(payment)
            }
        });
    }
}
