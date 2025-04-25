import {Component, Input} from '@angular/core';
import {FormGroup, ReactiveFormsModule} from "@angular/forms";
import {PaymentGatewayType} from "../../../../../services/models/payment-method/payment-gateway-type";
import {Divider} from "primeng/divider";
import {FloatLabel} from "primeng/floatlabel";
import {Select} from "primeng/select";

@Component({
    selector: 'app-gateway-type-select',
    imports: [
        ReactiveFormsModule,
        Divider,
        FloatLabel,
        Select,
    ],
    templateUrl: './gateway-type-select.component.html',
    standalone: true,
    styleUrl: './gateway-type-select.component.scss'
})
export class GatewayTypeSelectComponent {
    @Input() form!: FormGroup;
    @Input() gatewayTypes: PaymentGatewayType[] = [];
}
