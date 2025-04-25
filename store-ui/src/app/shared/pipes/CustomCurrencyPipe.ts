import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'customCurrency',
    standalone: true,
})
export class CustomCurrencyPipe implements PipeTransform {
    transform(value: number | null | undefined, currency: string = 'CZK'): string {
        if (value == null) return '';

        return new Intl.NumberFormat('cs-CZ', {
            style: 'currency',
            currency: currency,
            minimumFractionDigits: 0,
            maximumFractionDigits: 2
        }).format(value);
    }
}
