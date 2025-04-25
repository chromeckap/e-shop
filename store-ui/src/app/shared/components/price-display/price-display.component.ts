import {Component, Input} from '@angular/core';
import {CustomCurrencyPipe} from "../../pipes/CustomCurrencyPipe";
import {Tag} from "primeng/tag";

@Component({
    selector: 'app-price-display',
    imports: [
        CustomCurrencyPipe,
        Tag
    ],
    templateUrl: './price-display.component.html',
    standalone: true,
    styleUrl: './price-display.component.scss'
})
export class PriceDisplayComponent {
    @Input() price: number | null = null;
    @Input() basePrice: number | null = null;
    @Input() isPriceEqual: boolean = false;

    calculateDiscount(basePrice: number, price: number): number {
        if (!basePrice || basePrice <= price) return 0;
        return Math.round(((basePrice - price) / basePrice) * 100);
    }
}
