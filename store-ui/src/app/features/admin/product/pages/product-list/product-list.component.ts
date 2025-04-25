import {Component, OnInit} from '@angular/core';
import {ConfirmationService} from "primeng/api";
import {ProductOverviewResponse} from "../../../../../services/models/product/product-overview-response";
import {Router} from "@angular/router";
import {ProductService} from "../../../../../services/services/product.service";
import {ProductPageResponse} from "../../../../../services/models/product/product-page-response";
import {TableModule} from "primeng/table";
import {Toolbar} from "primeng/toolbar";
import {Button} from "primeng/button";
import {ConfirmDialog} from "primeng/confirmdialog";
import {Tag} from "primeng/tag";
import {forkJoin, Observable} from "rxjs";
import {PriceDisplayComponent} from "../../../../../shared/components/price-display/price-display.component";
import {ToastService} from "../../../../../shared/services/toast.service";

@Component({
    selector: 'app-product-list',
    imports: [
        Toolbar,
        Button,
        TableModule,
        ConfirmDialog,
        Tag,
        PriceDisplayComponent
    ],
    templateUrl: './product-list.component.html',
    standalone: true,
    styleUrl: './product-list.component.scss',
    providers: [ConfirmationService]
})
export class ProductListComponent implements OnInit {
    productsPage: ProductPageResponse = {};
    page = 0;
    size = 10;
    attribute = 'id';
    direction = 'desc';
    selectedProducts: Array<ProductOverviewResponse> = [];

    constructor(
        private productService: ProductService,
        private confirmationService: ConfirmationService,
        private toastService: ToastService,
        private router: Router
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

    createProduct() {
        this.router.navigate(['/admin/produkty/vytvorit'])
            .catch(error => {
                console.log("Při navigaci došlo k chybě: ", error);
            });
    }

    editProduct(product: ProductOverviewResponse) {
        this.router.navigate(['admin/produkty/upravit', product.id])
            .catch(error => {
                console.log("Při navigaci došlo k chybě: ", error);
            });
    }

    deleteProduct(product: ProductOverviewResponse) {
        this.confirmationService.confirm({
            header: 'Potvrzení odstranění',
            icon: 'pi pi-exclamation-triangle',
            message: 'Opravdu chceš odstranit produkt ' + product.name + "?",
            acceptLabel: 'Ano',
            acceptIcon: 'pi pi-check',
            acceptButtonStyleClass: 'p-button-danger p-button-outlined',
            rejectLabel: 'Ne',
            rejectIcon: 'pi pi-check',
            rejectButtonStyleClass: 'p-button-secondary p-button-outlined',

            accept: () => {
                this.productService.deleteProductById(product.id!).subscribe({
                    next: async () => {
                        try {
                            this.getAllProducts();
                            await this.toastService.showSuccessToast('Úspěch', 'Produkt byl úspěšně odstraněn.');
                        } catch (error) {
                            console.log("Chyba při operacích po odstranění:", error);
                        }
                    },
                    error: async (error) => {
                        console.log(error);
                        try {
                            await this.toastService.showErrorToast('Chyba', error.error.detail);
                        } catch (toastError) {
                            console.log("Chyba při zobrazení toastu:", toastError);
                        }
                    }
                });
            }
        });
    }

    deleteSelectedProducts() {
        this.confirmationService.confirm({
            header: 'Potvrzení odstranění',
            icon: 'pi pi-exclamation-triangle',
            message: 'Opravdu chceš odstranit vybrané produkty?',
            acceptLabel: 'Ano',
            acceptIcon: 'pi pi-check',
            acceptButtonStyleClass: 'p-button-danger p-button-outlined',
            rejectLabel: 'Ne',
            rejectIcon: 'pi pi-check',
            rejectButtonStyleClass: 'p-button-secondary p-button-outlined',

            accept: () => {
                const deleteOperations: { [key: string]: Observable<any> } = {};
                this.selectedProducts.forEach(product => {
                    if (product.id) {
                        deleteOperations[`product-${product.id}`] = this.productService.deleteProductById(product.id);
                    }
                });
                forkJoin(deleteOperations).subscribe({
                    next: async () => {
                        try {
                            this.getAllProducts();
                            this.selectedProducts = [];
                            await this.toastService.showSuccessToast('Úspěch', 'Produkty byly úspěšně odstraněny.');
                        } catch (error) {
                            console.log("Chyba při operacích po odstranění:", error);
                        }
                    },
                    error: async (error) => {
                        console.log(error);
                        try {
                            await this.toastService.showErrorToast('Chyba', error.error.detail);
                        } catch (toastError) {
                            console.log("Chyba při zobrazení toastu:", toastError);
                        }
                    }
                });
            }

        });
    }
}
