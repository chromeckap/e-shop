import {Component, ViewChild} from '@angular/core';
import {AutoComplete, AutoCompleteSelectEvent} from "primeng/autocomplete";
import {FormControl, ReactiveFormsModule} from "@angular/forms";
import {ProductService} from "../../../../../services/services/product.service";
import {Router} from "@angular/router";
import {ProductOverviewResponse} from "../../../../../services/models/product/product-overview-response";
import {PriceDisplayComponent} from "../../../../../shared/components/price-display/price-display.component";
import {PrimeTemplate} from "primeng/api";

@Component({
    selector: 'search-product-topbar',
    imports: [
        AutoComplete,
        PriceDisplayComponent,
        PrimeTemplate,
        ReactiveFormsModule
    ],
    templateUrl: './search-product.topbar.html',
    standalone: true
})
export class SearchProductTopbar {
    @ViewChild('autoComplete') autoComplete!: AutoComplete;

    searchControl = new FormControl('');
    suggestions: any[] = [];

    constructor(
        private productService: ProductService,
        private router: Router
    ) {}

    private searchProducts(query: string) {
        this.productService.searchProductsByQuery(query).subscribe({
            next: (response) => {
                this.suggestions = response.content || [];

                if (this.suggestions.length > 0 && this.autoComplete) {
                    this.autoComplete.show();
                }
            }
        });
    }

    protected getImage(product: ProductOverviewResponse) {
        return product.primaryImagePath
            ? this.productService.getImage(product.id!, product.primaryImagePath)
            : 'assets/img/image-not-found.png';
    }

    search(event: any) {
        const query = event.query;
        if (query && query.trim() !== '') {
            this.searchProducts(query);
        }
    }

    onFocus() {
        const currentValue = this.searchControl.value;
        if (currentValue && currentValue.trim() !== '') {
            this.searchProducts(currentValue);
        }
    }

    onSelect(event: AutoCompleteSelectEvent) {
        const product = event.value;

        if (product && product.id) {
            this.router.navigate(['/produkt/' + product.id])
                .catch((error) => {
                    console.log('Při navigaci došlo k chybě:', error);
                });
            this.searchControl.setValue('', { emitEvent: false });
        }
    }
}
