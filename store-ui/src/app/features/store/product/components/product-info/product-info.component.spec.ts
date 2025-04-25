import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ProductInfoComponent } from './product-info.component';
import { ProductService } from '../../../../../services/services/product.service';
import { AuthService } from '../../../../../services/services/auth.service';
import { ShoppingCartService } from '../../../../../services/services/shopping-cart.service';
import { ToastService } from '../../../../../shared/services/toast.service';
import { DomSanitizer } from '@angular/platform-browser';
import { Router } from '@angular/router';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ProductResponse } from '../../../../../services/models/product/product-response';
import { VariantResponse } from '../../../../../services/models/variant/variant-response';
import { of, throwError } from 'rxjs';
import { By } from '@angular/platform-browser';

describe('ProductInfoComponent', () => {
    let component: ProductInfoComponent;
    let fixture: ComponentFixture<ProductInfoComponent>;
    let productServiceSpy: jasmine.SpyObj<ProductService>;
    let authServiceSpy: jasmine.SpyObj<AuthService>;
    let shoppingCartServiceSpy: jasmine.SpyObj<ShoppingCartService>;
    let toastServiceSpy: jasmine.SpyObj<ToastService>;
    let sanitizerSpy: jasmine.SpyObj<DomSanitizer>;
    let routerSpy: jasmine.SpyObj<Router>;

    // Mock product data
    const mockProduct: ProductResponse = {
        id: 1,
        name: 'Test Product',
        price: 199.99,
        basePrice: 249.99,
        isPriceEqual: false,
        description: '<p>Test product description</p>',
        imagePaths: ['image1.jpg', 'image2.jpg'],
        attributes: [
            { id: 1, name: 'Color', values: [] },
            { id: 2, name: 'Size', values: [] }
        ],
        variants: [
            {
                id: 101,
                basePrice: 249.99,
                discountedPrice: 199.99,
                quantity: 5,
                quantityUnlimited: false,
                attributeValues: [
                    { id: 11, value: 'Red' },
                    { id: 21, value: 'S' }
                ]
            },
            {
                id: 102,
                basePrice: 249.99,
                discountedPrice: 199.99,
                quantity: 10,
                quantityUnlimited: false,
                attributeValues: [
                    { id: 11, value: 'Red' },
                    { id: 21, value: 'M' }
                ]
            },
            {
                id: 103,
                basePrice: 259.99,
                discountedPrice: 209.99,
                quantity: 0,
                quantityUnlimited: false,
                attributeValues: [
                    { id: 11, value: 'Blue' },
                    { id: 21, value: 'S' }
                ]
            }
        ],
        relatedProducts: [
            {
                id: 2,
                name: 'Related Product 1',
                primaryImagePath: 'related1.jpg'
            },
            {
                id: 3,
                name: 'Related Product 2',
                primaryImagePath: 'related2.jpg'
            }
        ]
    };

    // Mock sanitized HTML
    const mockSanitizedHtml = 'sanitized-html' as any;

    beforeEach(async () => {
        // Create spies for all dependencies
        productServiceSpy = jasmine.createSpyObj('ProductService', ['getImage']);
        authServiceSpy = jasmine.createSpyObj('AuthService', [], {
            isLoggedIn: true
        });
        shoppingCartServiceSpy = jasmine.createSpyObj('ShoppingCartService', ['addItemToCartForCurrentUser']);
        toastServiceSpy = jasmine.createSpyObj('ToastService', ['showSuccessToast', 'showErrorToast']);
        sanitizerSpy = jasmine.createSpyObj('DomSanitizer', ['bypassSecurityTrustHtml']);
        routerSpy = jasmine.createSpyObj('Router', ['navigate']);

        // Configure spy behavior
        productServiceSpy.getImage.and.callFake((id, path) => `https://test-cdn.com/images/${id}/${path}`);
        sanitizerSpy.bypassSecurityTrustHtml.and.returnValue(mockSanitizedHtml);
        toastServiceSpy.showSuccessToast.and.resolveTo();
        toastServiceSpy.showErrorToast.and.resolveTo();
        routerSpy.navigate.and.resolveTo(true);
        shoppingCartServiceSpy.addItemToCartForCurrentUser.and.returnValue(of());

        await TestBed.configureTestingModule({
            imports: [
                ProductInfoComponent,
                FormsModule
            ],
            providers: [
                { provide: ProductService, useValue: productServiceSpy },
                { provide: AuthService, useValue: authServiceSpy },
                { provide: ShoppingCartService, useValue: shoppingCartServiceSpy },
                { provide: ToastService, useValue: toastServiceSpy },
                { provide: DomSanitizer, useValue: sanitizerSpy },
                { provide: Router, useValue: routerSpy }
            ],
            schemas: [NO_ERRORS_SCHEMA] // For PrimeNG components
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(ProductInfoComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should initialize with default values', () => {
        expect(component.product).toEqual({});
        expect(component.sanitizedDescription).toBeUndefined();
        expect(component.attributeOptions).toEqual({});
        expect(component.images()).toEqual([]);
        expect(component.currentPrice()).toBeNull();
        expect(component.currentBasePrice()).toBeNull();
        expect(component.selectedAttributes).toEqual({});
        expect(component.selectedVariant).toBeNull();
        expect(component.quantity).toBe(1);
    });

    it('should not find matching variant if not all attributes are selected', () => {
        component.loadData(mockProduct);

        // Select only color, not size
        component.selectedAttributes[1] = 11;
        component.selectedAttributes[2] = null;

        const matchingVariant = component.findMatchingVariant();
        expect(matchingVariant).toBeUndefined();
    });

    it('should redirect to login if user is not logged in when adding to cart', () => {
        // Change auth state to not logged in
        Object.defineProperty(authServiceSpy, 'isLoggedIn', { get: () => false });

        component.loadData(mockProduct);
        component.selectedVariant = mockProduct.variants![0];
        component.addItemToCart();

        expect(routerSpy.navigate).toHaveBeenCalledWith(['/prihlaseni']);
        expect(shoppingCartServiceSpy.addItemToCartForCurrentUser).not.toHaveBeenCalled();
    });

    it('should handle error when adding to cart fails', fakeAsync(() => {
        component.loadData(mockProduct);
        component.selectedVariant = mockProduct.variants![0];
        component.quantity = 1;

        // Setup error scenario
        shoppingCartServiceSpy.addItemToCartForCurrentUser.and.returnValue(
            throwError(() => ({ error: { detail: 'Failed to add to cart' } }))
        );

        component.addItemToCart();
        tick();

        expect(toastServiceSpy.showErrorToast).toHaveBeenCalled();
    }));

    it('should not attempt to add to cart if no variant is selected', () => {
        component.loadData(mockProduct);
        component.selectedVariant = null;
        component.addItemToCart();

        expect(shoppingCartServiceSpy.addItemToCartForCurrentUser).not.toHaveBeenCalled();
    });

    it('should handle product with no variants', () => {
        const noVariantsProduct: ProductResponse = {
            ...mockProduct,
            variants: []
        };

        component.loadData(noVariantsProduct);

        expect(component.selectedVariant).toBeNull();
        expect(Object.keys(component.attributeOptions).length).toBe(0);
    });

    it('should handle product with single variant', () => {
        const singleVariantProduct: ProductResponse = {
            ...mockProduct,
            variants: [mockProduct.variants![0]]
        };

        component.loadData(singleVariantProduct);

        // Should automatically select the only variant
        expect(component.selectedVariant).toBe(singleVariantProduct.variants![0]);
    });

    it('should handle product with no description', () => {
        const noDescriptionProduct: ProductResponse = {
            ...mockProduct,
            description: undefined
        };

        component.loadData(noDescriptionProduct);

        expect(component.sanitizedDescription).toBeUndefined();
        expect(sanitizerSpy.bypassSecurityTrustHtml).not.toHaveBeenCalled();
    });

    it('should handle product with no images', () => {
        const noImagesProduct: ProductResponse = {
            ...mockProduct,
            imagePaths: []
        };

        component.loadData(noImagesProduct);

        expect(component.images().length).toBe(0);
    });

    it('should handle product with no related products', () => {
        const noRelatedProduct: ProductResponse = {
            ...mockProduct,
            relatedProducts: []
        };

        component.loadData(noRelatedProduct);
        fixture.detectChanges();

        const relatedProductsSection = fixture.debugElement.query(By.css('h4.mt-0'));
        expect(relatedProductsSection).toBeFalsy();
    });
});
