import {Component, signal} from '@angular/core';
import { ProductResponse } from "../../../../../services/models/product/product-response";
import { ProductService } from "../../../../../services/services/product.service";
import { GalleriaModule } from "primeng/galleria";
import { DropdownModule } from "primeng/dropdown";
import { FormsModule } from '@angular/forms';
import { PriceDisplayComponent } from "../../../../../shared/components/price-display/price-display.component";
import {AttributeValueResponse} from "../../../../../services/models/attribute/attribute-value-response";
import {VariantResponse} from "../../../../../services/models/variant/variant-response";
import {Button} from "primeng/button";
import {Router, RouterLink} from "@angular/router";
import {DomSanitizer, SafeHtml} from "@angular/platform-browser";
import {Tag} from "primeng/tag";
import {Select} from "primeng/select";
import {AuthService} from "../../../../../services/services/auth.service";
import {ShoppingCartService} from "../../../../../services/services/shopping-cart.service";
import {ToastService} from "../../../../../shared/services/toast.service";
import {InputNumber} from "primeng/inputnumber";

interface ImageItem {
    itemImageSrc: string;
    thumbnailImageSrc: string;
    alt: string;
    title: string;
}

interface SelectedAttributes {
    [attributeId: number]: number | null;
}

@Component({
    selector: 'app-product-info',
    imports: [
        GalleriaModule,
        DropdownModule,
        FormsModule,
        PriceDisplayComponent,
        Button,
        RouterLink,
        Tag,
        Select,
        InputNumber
    ],
    templateUrl: './product-info.component.html',
    standalone: true,
    styleUrl: './product-info.component.scss'
})
export class ProductInfoComponent {
    product: ProductResponse = {};
    sanitizedDescription: SafeHtml | undefined;

    attributeOptions: { [attributeId: number]: AttributeValueResponse[] } = {};

    images = signal<ImageItem[]>([]);
    currentPrice = signal<number | null>(null);
    currentBasePrice = signal<number | null>(null);

    selectedAttributes: SelectedAttributes = {};
    selectedVariant: VariantResponse | null = null;
    quantity: number = 1;

    constructor(
        private productService: ProductService,
        private authService: AuthService,
        private shoppingCartService: ShoppingCartService,
        private toastService: ToastService,
        private sanitizer: DomSanitizer,
        private router: Router
    ) {}

    loadData(product: ProductResponse) {
        this.product = product;
        if (product.description)
            this.sanitizedDescription = this.sanitizer.bypassSecurityTrustHtml(product.description);

        this.selectedVariant = null;
        this.selectedAttributes = {};
        this.currentPrice.set(this.product.price!);
        this.currentBasePrice.set(this.product.basePrice!);

        this.loadImages();
        this.prepareAttributeOptions();
    }

    prepareAttributeOptions() {
        if (!this.product.attributes || !this.product.variants?.length) return;

        if (this.product.variants.length == 1) {
            this.selectedVariant = this.product.variants.at(0)!;
        }

        this.currentPrice.set(this.product.price!);
        this.currentBasePrice.set(this.product.basePrice!);

        this.product.attributes.forEach(attribute => {
            this.attributeOptions[attribute.id] =
                (this.product.variants || [])
                    .filter((variant): variant is VariantResponse =>
                        !!variant.attributeValues &&
                        variant.attributeValues[attribute.id] !== undefined
                    )
                    .map(variant => {
                        const attributeValue = variant.attributeValues![attribute.id];
                        return {
                            ...attributeValue
                        };
                    })
                    .filter((value, index, self) =>
                        self.findIndex(t => t.id === value.id) === index
                    ) || [];

            this.selectedAttributes[attribute.id] = null;
        });
    }

    onAttributeChange() {
        const matchingVariant = this.findMatchingVariant();

        if (matchingVariant) {
            this.selectedVariant = matchingVariant;
            this.currentPrice.set(matchingVariant.discountedPrice!);
            this.currentBasePrice.set(matchingVariant.basePrice!);
        } else {
            this.selectedVariant = null;
            this.currentPrice.set(this.product.price!);
            this.currentBasePrice.set(this.product.basePrice!);
        }
    }

    findMatchingVariant(): VariantResponse | undefined {
        if (!this.product.variants) return undefined;

        const effectiveAttributeIds = Object.keys(this.selectedAttributes)
            .filter(attrId => this.attributeOptions[Number(attrId)] && this.attributeOptions[Number(attrId)].length > 0);

        const allEffectiveSelected = effectiveAttributeIds.every(attrId => this.selectedAttributes[Number(attrId)] !== null);
        if (!allEffectiveSelected) return undefined;

        const hasMatchingAttributes = (variant: VariantResponse): boolean =>
            effectiveAttributeIds.every(attrId => {
                const numAttrId = Number(attrId);
                return variant.attributeValues &&
                    variant.attributeValues[numAttrId] &&
                    variant.attributeValues[numAttrId].id === this.selectedAttributes[numAttrId];
            });

        return this.product.variants.find(hasMatchingAttributes);
    }


    loadImages() {
        let pictures;

        if (this.product.imagePaths && this.product.imagePaths.length > 0) {
            pictures = this.product.imagePaths.map(imagePath => {
                const picture = this.getImage(this.product.id!, imagePath);
                return {
                    itemImageSrc: picture,
                    thumbnailImageSrc: picture,
                    title: imagePath,
                    alt: imagePath,
                };
            });
        } else {
            const fallback = 'assets/img/main-image-not-found.png';
            pictures = [{
                itemImageSrc: fallback,
                thumbnailImageSrc: fallback,
                title: 'Image not found',
                alt: 'Image not found',
            }];
        }

        const loadedPictures = Promise.resolve(pictures);

        loadedPictures.then(images => {
            this.images.set(images);
        });
    }


    getImage(id: number, imagePath: string) {
        if (!imagePath) return 'assets/img/image-not-found.png';

        return this.productService.getImage(id, imagePath);
    }

    variantIsAvailable() {
        if (!this.selectedVariant) return false;
        return this.selectedVariant.quantityUnlimited || this.selectedVariant.quantity! > 0;
    }

    addItemToCart() {
        if (!this.selectedVariant) return;

        if (!this.authService.isLoggedIn) {
            this.router.navigate(['/prihlaseni'])
                .catch((error) => {
                    console.log('Při navigaci došlo k chybě:', error);
                });

            return;
        }

        const request = {
            productId: this.selectedVariant.id,
            quantity: this.quantity
        };

        this.shoppingCartService.addItemToCartForCurrentUser(request).subscribe({
            next: async () => {
                try {
                    await this.toastService.showSuccessToast('Úspěch', 'Varianta byla úspěšně přidána do košíku.');
                } catch (error) {
                    console.log("Chyba při zobrazení toastu:", error);
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

    responsiveOptions: any[] = [
        {
            breakpoint: '1850px',
            numVisible: 4
        },
        {
            breakpoint: '1600px',
            numVisible: 3
        },
        {
            breakpoint: '1400px',
            numVisible: 2
        },
        {
            breakpoint: '1090px',
            numVisible: 1
        },
        {
            breakpoint: '992px',
            numVisible: 3
        },
        {
            breakpoint: '760px',
            numVisible: 4
        },
        {
            breakpoint: '650px',
            numVisible: 3
        },
        {
            breakpoint: '550px',
            numVisible: 2
        },
        {
            breakpoint: '450px',
            numVisible: 1
        }
    ];
}
