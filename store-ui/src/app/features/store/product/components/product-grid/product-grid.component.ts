import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ProductPageResponse} from "../../../../../services/models/product/product-page-response";
import {ProductService} from "../../../../../services/services/product.service";
import {ProductOverviewResponse} from "../../../../../services/models/product/product-overview-response";
import {ProductCardComponent} from "../product-card/product-card.component";
import {Paginator} from "primeng/paginator";

@Component({
    selector: 'app-product-grid',
    imports: [
        ProductCardComponent,
        Paginator
    ],
    templateUrl: './product-grid.component.html',
    standalone: true,
    styleUrl: './product-grid.component.scss'
})
export class ProductGridComponent {
    @Input() productPage: ProductPageResponse = {};
    @Output() pageChanged = new EventEmitter<{ page: number, size: number }>();
    @Input() currentPage = 0;

    onPageChange(event: any) {
        this.pageChanged.emit({
            page: event.first / event.rows,
            size: event.rows
        });
    }
}
