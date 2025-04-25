import {Component, Input, OnInit} from '@angular/core';
import {ProductService} from "../../../../../services/services/product.service";
import {ProductPageResponse} from "../../../../../services/models/product/product-page-response";
import {TableModule} from "primeng/table";
import {ProductOverviewResponse} from "../../../../../services/models/product/product-overview-response";
import {FormGroup, ReactiveFormsModule} from "@angular/forms";
import {Divider} from "primeng/divider";

@Component({
    selector: 'app-related-product-multiselect',
    imports: [
        TableModule,
        ReactiveFormsModule,
        Divider
    ],
    templateUrl: './related-product-multiselect.component.html',
    standalone: true,
    styleUrl: './related-product-multiselect.component.scss'
})
export class RelatedProductMultiSelectComponent implements OnInit {
    @Input() selectedProducts: ProductOverviewResponse[] = [];
    @Input() form!: FormGroup;
    productsPage: ProductPageResponse = {};
    page = 0;
    size = 5;
    attribute = 'id';
    direction = 'desc';

    constructor(
        private productService: ProductService
    ) {}

    ngOnInit(): void {
        this.getAllProducts();
    }

    private getAllProducts() {
        this.productService.getAllProducts({
            pageNumber: this.page,
            pageSize: this.size,
            attribute: this.attribute,
            direction: this.direction
        })
            .subscribe({
                next: (products) => {
                    this.productsPage = products;
                }
            });
    }

    getImage(product: ProductOverviewResponse) {
        return product.primaryImagePath
            ? this.productService.getImage(product.id!, product.primaryImagePath)
            : 'assets/img/image-not-found.png';
    }

    onPageChange(event: any) {
        this.page = event.first / event.rows;
        this.size = event.rows;
        this.getAllProducts();
    }

    onSelectionChange(products: ProductOverviewResponse[]) {
        this.form.get('relatedProducts')?.setValue(products);
    }
}
