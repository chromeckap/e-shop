import {Component, OnDestroy, OnInit} from '@angular/core';
import {ProductPageResponse} from "../../../../../services/models/product/product-page-response";
import {ProductService} from "../../../../../services/services/product.service";
import {ActivatedRoute, Router} from "@angular/router";
import {forkJoin, Subscription} from "rxjs";
import {Divider} from "primeng/divider";
import {CategoryService} from "../../../../../services/services/category.service";
import {CategoryResponse} from "../../../../../services/models/category/category-response";
import {MenuItem} from "primeng/api";
import {CategoryOverviewResponse} from "../../../../../services/models/category/category-overview-response";
import {TableModule} from "primeng/table";
import {FormsModule} from "@angular/forms";
import {DropdownModule} from "primeng/dropdown";
import {FilterRangesResponse} from "../../../../../services/models/product/filter-ranges-response";
import {ProductGridComponent} from "../../components/product-grid/product-grid.component";
import {ProductFilterComponent} from "../../components/product-filter/product-filter.component";
import {CategoryNavComponent} from "../../components/category-nav/category-nav.component";

@Component({
    selector: 'app-product-catalog',
    imports: [
        Divider,
        TableModule,
        FormsModule,
        DropdownModule,
        ProductGridComponent,
        ProductFilterComponent,
        CategoryNavComponent
    ],
    templateUrl: './product-catalog.component.html',
    standalone: true,
    styleUrl: './product-catalog.component.scss'
})
export class ProductCatalogComponent implements OnInit, OnDestroy {
    productsPage: ProductPageResponse = {};
    category: CategoryResponse = {};
    childCategories: MenuItem[] = [];
    filterRanges: FilterRangesResponse = {};

    private routeSubscription: Subscription | undefined;

    page = 0;
    size = 27;
    attribute = 'id';
    direction = 'desc';

    currentPage = 0;

    currentFilterState: {
        lowPrice: number,
        maxPrice: number,
        attributeValueIds: number[]
    } = {
        lowPrice: 0,
        maxPrice: 0,
        attributeValueIds: []
    };

    constructor(
        private productService: ProductService,
        private categoryService: CategoryService,
        private router: Router,
        private activatedRoute: ActivatedRoute,
    ) {}

    ngOnInit(): void {
        this.routeSubscription = this.activatedRoute.params.subscribe(params => {
            const id = +params['id'];
            if (id) {
                this.loadCategoryData(id);
            }
        });
    }

    ngOnDestroy(): void {
        this.routeSubscription?.unsubscribe();
    }

    private loadCategoryData(categoryId: number): void {
        forkJoin({
            category: this.categoryService.getCategoryById(categoryId),
            filterRanges: this.productService.getFilterRangesByCategory(categoryId)
        }).subscribe(({ category, filterRanges }) => {
            this.category = category;
            this.filterRanges = filterRanges;
            this.childCategories = this.convertCategoriesToMenuItems(category.children || []);

            this.currentFilterState = {
                lowPrice: filterRanges.lowPrice || 0,
                maxPrice: filterRanges.maxPrice || 0,
                attributeValueIds: []
            };

            this.loadProducts();
        });
    }

    loadProducts(): void {
        this.productService.getProductsByCategory(
            this.category.id!,
            {
                pageNumber: this.page,
                pageSize: this.size,
                attribute: this.attribute,
                direction: this.direction
            },
            {
                lowPrice: this.currentFilterState.lowPrice,
                maxPrice: this.currentFilterState.maxPrice,
                attributeValueIds: this.currentFilterState.attributeValueIds
            }
        ).subscribe({
            next: (products) => {
                this.productsPage = products;
            },
            error: (err) => {
                console.error('Error loading products', err);
            }
        });
    }

    onPageChange(event: { page: number, size: number }): void {
        this.page = event.page;
        this.currentPage = event.page;
        this.size = event.size;
        this.loadProducts();
    }

    onSortChange(sortOption: { attribute: string, direction: string }): void {
        this.attribute = sortOption.attribute;
        this.direction = sortOption.direction;
        this.page = 0;
        this.currentPage = 0;
        this.loadProducts();
    }

    private convertCategoriesToMenuItems(categories: CategoryOverviewResponse[]): MenuItem[] {
        return categories.map(category => ({
            label: category.name,
            id: category.id?.toString(),
            command: () => {
                this.router.navigate(['/kategorie/' + category.id + '/produkty'])
                    .catch((error) => {
                        console.error('Navigation error:', error);
                    });
            }
        }));
    }

    onFilterChange(filterState: {
        rangeValues: number[],
        selectedAttributeValues: { [attributeId: number]: number[] }
    }): void {
        this.currentFilterState = {
            lowPrice: filterState.rangeValues[0],
            maxPrice: filterState.rangeValues[1],
            attributeValueIds: Object.values(filterState.selectedAttributeValues)
                .reduce((acc, curr) => [...acc, ...curr], [])
        };

        this.page = 0;
        this.currentPage = 0;
        this.loadProducts();
    }

}
