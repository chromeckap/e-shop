import {Component, Input} from '@angular/core';
import {TableModule} from "primeng/table";
import {CustomCurrencyPipe} from "../../../../../shared/pipes/CustomCurrencyPipe";
import {Tag} from "primeng/tag";
import {OrderResponse} from "../../../../../services/models/order/order-response";

@Component({
    selector: 'app-item-list',
    imports: [
        TableModule,
        CustomCurrencyPipe,
        Tag
    ],
    templateUrl: './item-list.component.html',
    standalone: true,
    styleUrl: './item-list.component.scss'
})
export class ItemListComponent {
    @Input() order!: OrderResponse;

    public objectKeys(object: any) {
        return Object.keys(object);
    }

    get orderDataTable() {
        const items = this.order.items
            ?.map(item => ({
                ...item,
                type: 'product'
            })) || [];

        const additionalCosts = Object.entries(this.order.additionalCosts || {})
            .map(([key, value]) => ({
                name: key,
                price: value,
                type: 'cost'
            }))  || [];

        return [ ...items, ...additionalCosts];
    }
}
