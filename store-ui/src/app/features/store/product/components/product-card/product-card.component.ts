import {Component, Input} from '@angular/core';
import {ProductOverviewResponse} from "../../../../../services/models/product/product-overview-response";
import {PriceDisplayComponent} from "../../../../../shared/components/price-display/price-display.component";
import {RouterLink} from "@angular/router";
import {NgClass} from "@angular/common";
import {ProductService} from "../../../../../services/services/product.service";

@Component({
    selector: 'app-product-card',
    imports: [
        PriceDisplayComponent,
        RouterLink,
        NgClass,
    ],
    templateUrl: './product-card.component.html',
    standalone: true,
    styleUrl: './product-card.component.scss',
    styles: [`
        :host {
            display: block;
            height: 100%;
        }
    `]
})
export class ProductCardComponent {
    @Input() product: ProductOverviewResponse = {};
    isLoading: boolean = true;

    constructor(
        private productService: ProductService
    ) {}

    getImage(product: ProductOverviewResponse) {
        if (!product.id) return 'assets/img/image-not-found.png';

        return product.primaryImagePath
            ? this.productService.getImage(product.id, product.primaryImagePath)
            : 'assets/img/image-not-found.png';
    }

    onImageLoaded(): void {
        this.isLoading = false;
    }

    onImageError(): void {
        this.isLoading = false;
    }

    get productUrl() {
        return '/produkt/' + this.product.id;
    }
}
