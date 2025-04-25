import {Component, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ProductService} from "../../../../../services/services/product.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ConfirmationService} from "primeng/api";
import {Button} from "primeng/button";
import {NgClass} from "@angular/common";
import {Toolbar} from "primeng/toolbar";
import {OverviewComponent} from "../../components/overview/overview.component";
import {PictureUploaderComponent} from "../../components/picture-uploader/picture-uploader.component";
import {CategoryTreeSelectComponent} from "../../components/category-treeselect/category-treeselect.component";
import {AttributeMultiSelectComponent} from "../../components/attribute-multiselect/attribute-multiselect.component";
import {
    RelatedProductMultiSelectComponent
} from "../../components/related-product-multiselect/related-product-multiselect.component";
import {VariantListComponent} from "../../components/variant-list/variant-list.component";
import {RefreshService} from "../../services/refresh.service";
import {ConfirmDialog} from "primeng/confirmdialog";
import {AttributeResponse} from "../../../../../services/models/attribute/attribute-response";
import {ProductOverviewResponse} from "../../../../../services/models/product/product-overview-response";
import {ProductRequest} from "../../../../../services/models/product/product-request";
import {ToastService} from "../../../../../shared/services/toast.service";
import {forkJoin, of} from "rxjs";

@Component({
    selector: 'app-manage-product',
    imports: [
        Button,
        Toolbar,
        NgClass,
        OverviewComponent,
        PictureUploaderComponent,
        CategoryTreeSelectComponent,
        AttributeMultiSelectComponent,
        RelatedProductMultiSelectComponent,
        VariantListComponent,
        ConfirmDialog
    ],
    templateUrl: './manage-product.component.html',
    standalone: true,
    styleUrl: './manage-product.component.scss',
    providers: [ConfirmationService]
})
export class ManageProductComponent implements OnInit {
    form: FormGroup;
    @ViewChild(VariantListComponent) variantList!: VariantListComponent;
    @ViewChild(PictureUploaderComponent) pictureUploader!: PictureUploaderComponent;
    @ViewChild(CategoryTreeSelectComponent) categoryTree!: CategoryTreeSelectComponent;

    constructor(
        private productService: ProductService,
        private confirmationService: ConfirmationService,
        private toastService: ToastService,
        private formBuilder: FormBuilder,
        private router: Router,
        private activatedRoute: ActivatedRoute,
        private refreshService: RefreshService
    ) {
        this.form = this.formBuilder.group({
            id: [{value: null, disabled: true}],
            name: ['', Validators.required],
            description: [''],
            isVisible: [null],
            variants: [[]],
            relatedProducts: [[]],
            categoryIds: [[]],
            attributes: [[]],
            imagePaths: [[]]
        });
    }

    ngOnInit(): void {
        this.loadData();
    }


    private loadData() {
        const id = this.activatedRoute.snapshot.params['id'];

        if (id) {
            this.productService.getProductById(id)
                .subscribe(product => {
                    this.form.patchValue(product);
                    this.variantList.setStoredVariants(structuredClone(product.variants!));
                    this.pictureUploader.loadExistingFiles();
                    this.refreshService.triggerRefresh();
                });
        }
    }

    get isEditing() {
        return this.form.getRawValue().id;
    }

    saveProduct(isVisible: boolean) {
        if (this.form.invalid) return;

        const product = this.mapFormToProduct(isVisible);
        const observable = this.isEditing
            ? this.productService.updateProduct(product.id!, product)
            : this.productService.createProduct(product);

        observable.subscribe({
            next: (id) => {
                const variantsObservable = this.variantList.manageVariants(id);
                const imagesObservable = this.pictureUploader.uploadFiles(id);

                forkJoin({
                    variants: variantsObservable || of(null),
                    images: imagesObservable || of(null)
                }).subscribe({
                    next: async () => {
                        try {
                            await this.toastService.showSuccessToast('Úspěch', 'Produkt byl úspěšně uložen.');
                            await this.router.navigate(['admin/produkty/upravit/' + id]);
                        } catch (error) {
                            console.error('Chyba při navigaci nebo zobrazení toastu:', error);
                        }
                    },
                    error: async (error) => {
                        console.error('Chyba při zpracování dat:', error);
                        await this.toastService.showErrorToast('Chyba', 'Při zpracování dat došlo k chybě.');
                    }
                });
            },
            error: async (error) => {
                console.log(error);
                await this.toastService.showErrorToast('Chyba', error.error.detail);
            }
        });
    }

    deleteProduct() {
        this.confirmationService.confirm({
            header: 'Potvrzení odstranění',
            icon: 'pi pi-exclamation-triangle',
            message: 'Opravdu chceš odstranit produkt ' + this.form.getRawValue().name + "?",
            acceptLabel: 'Ano',
            acceptIcon: 'pi pi-check',
            acceptButtonStyleClass: 'p-button-danger p-button-outlined',
            rejectLabel: 'Ne',
            rejectIcon: 'pi pi-check',
            rejectButtonStyleClass: 'p-button-secondary p-button-outlined',

            accept: () => {
                this.productService.deleteProductById(this.form.getRawValue().id).subscribe({
                    next: async () => {
                        try {
                            await this.toastService.showSuccessToast('Úspěch', 'Produkt byl úspěšně odstraněn.');
                            await this.router.navigate(['/admin/produkty']);
                        } catch (error) {
                            console.log("Chyba při zobrazení toastu nebo navigaci:", error);
                        }
                    },
                    error: async (error) => {
                        console.log(error);
                        try {
                            await this.toastService.showErrorToast('Chyba', error.error.detail);
                        } catch (toastError) {
                            console.log("Chyba při zobrazení chybového toastu:", toastError);
                        }
                    }
                });
            }
        });
    }

    private mapFormToProduct(isVisible: boolean): ProductRequest {
        const formValue = this.form.getRawValue();

        return {
            id: formValue.id,
            name: formValue.name,
            description: formValue.description,
            isVisible,
            categoryIds: this.categoryTree.getSelectedCategoryIds(),
            attributeIds: formValue.attributes.map((attr: AttributeResponse) => attr.id),
            relatedProductIds: formValue.relatedProducts.map((relatedProduct: ProductOverviewResponse) => relatedProduct.id)
        };
    }
}
