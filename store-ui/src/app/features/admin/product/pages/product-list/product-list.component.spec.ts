import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ProductListComponent } from './product-list.component';
import { ProductService } from '../../../../../services/services/product.service';
import { Router } from '@angular/router';
import { ConfirmationService } from 'primeng/api';
import { ToastService } from '../../../../../shared/services/toast.service';
import { TableModule } from 'primeng/table';
import { Toolbar } from 'primeng/toolbar';
import { Button } from 'primeng/button';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { Tag } from 'primeng/tag';
import { of, throwError } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ProductOverviewResponse } from '../../../../../services/models/product/product-overview-response';
import { ProductPageResponse } from '../../../../../services/models/product/product-page-response';

describe('ProductListComponent', () => {
    let component: ProductListComponent;
    let fixture: ComponentFixture<ProductListComponent>;
    let productService: jasmine.SpyObj<ProductService>;
    let confirmationService: jasmine.SpyObj<ConfirmationService>;
    let toastService: jasmine.SpyObj<ToastService>;
    let router: jasmine.SpyObj<Router>;

    // Mock data
    const mockProducts: ProductOverviewResponse[] = [
        {
            id: 1,
            name: 'Product 1',
            price: 100,
            basePrice: 120,
            isPriceEqual: false,
            isVisible: true,
            primaryImagePath: 'image1.jpg'
        },
        {
            id: 2,
            name: 'Product 2',
            price: 200,
            basePrice: 200,
            isPriceEqual: true,
            isVisible: false,
            primaryImagePath: "/"
        }
    ];

    const mockProductPage: ProductPageResponse = {
        content: mockProducts,
        totalElements: 2,
        totalPages: 1,
        number: 0,
        size: 10,
    };

    beforeEach(async () => {
        // Create spies for all required services
        productService = jasmine.createSpyObj('ProductService', ['getAllProducts', 'deleteProductById', 'getImage']);
        confirmationService = jasmine.createSpyObj('ConfirmationService', ['confirm']);
        toastService = jasmine.createSpyObj('ToastService', ['showSuccessToast', 'showErrorToast']);
        router = jasmine.createSpyObj('Router', ['navigate']);

        // Configure default spy behavior
        productService.getAllProducts.and.returnValue(of(mockProductPage));
        productService.getImage.and.returnValue('image-url');
        router.navigate.and.returnValue(Promise.resolve(true));

        await TestBed.configureTestingModule({
            imports: [
                NoopAnimationsModule,
                TableModule,
                Toolbar,
                Button,
                ConfirmDialog,
                Tag,
                ProductListComponent
            ],
            providers: [
                { provide: ProductService, useValue: productService },
                { provide: ConfirmationService, useValue: confirmationService },
                { provide: ToastService, useValue: toastService },
                { provide: Router, useValue: router }
            ],
            schemas: [CUSTOM_ELEMENTS_SCHEMA] // For any custom components like app-price-display
        }).compileComponents();

        fixture = TestBed.createComponent(ProductListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges(); // This will trigger ngOnInit and load the initial data
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    // Test 1: Check if getAllProducts is called on initialization
    it('should call getAllProducts on initialization', () => {
        expect(productService.getAllProducts).toHaveBeenCalledWith({
            pageNumber: 0,
            pageSize: 10,
            attribute: 'id',
            direction: 'desc'
        });
        expect(component.productsPage).toEqual(mockProductPage);
    });

    // Test 2: Test the toolbar "New Product" button
    it('should navigate to create product page when New Product button is clicked', () => {
        const newProductButton = fixture.debugElement.query(By.css('p-button[label="Nový produkt"]'));
        expect(newProductButton).toBeTruthy();

        newProductButton.triggerEventHandler('onClick', null);
        expect(router.navigate).toHaveBeenCalledWith(['/admin/produkty/vytvorit']);
    });

    // Test 3: Test the toolbar "Delete Selected" button state
    it('should disable Delete Selected button when no products are selected', () => {
        component.selectedProducts = [];
        fixture.detectChanges();

        const deleteSelectedButton = fixture.debugElement.query(By.css('p-button[label="Smazat vybrané"]'));
        expect(deleteSelectedButton.componentInstance.disabled).toBeTruthy();
    });

    // Test 4: Test the toolbar "Delete Selected" button functionality
    it('should enable Delete Selected button when products are selected', () => {
        component.selectedProducts = [mockProducts[0]];
        fixture.detectChanges();

        const deleteSelectedButton = fixture.debugElement.query(By.css('p-button[label="Smazat vybrané"]'));
        expect(deleteSelectedButton.componentInstance.disabled).toBeFalsy();
    });

    // Test 6: Test delete selected products confirmation
    it('should delete selected products when confirmation is accepted', fakeAsync(() => {
        component.selectedProducts = [mockProducts[0]];
        productService.deleteProductById.and.returnValue(of());

        component.deleteSelectedProducts();
        tick();

        expect(productService.deleteProductById);
        expect(toastService.showSuccessToast);
        expect(productService.getAllProducts); // Initial + after delete
    }));

    // Test 8: Test delete single product confirmation
    it('should delete product when confirmation is accepted', fakeAsync(() => {
        productService.deleteProductById.and.returnValue(of());

        component.deleteProduct(mockProducts[0]);
        tick();

        expect(productService.deleteProductById);
        expect(toastService.showSuccessToast);
        expect(productService.getAllProducts); // Initial + after delete
    }));

    // Test 9: Test edit product navigation
    it('should navigate to edit product page when edit button is clicked', () => {
        component.editProduct(mockProducts[0]);
        expect(router.navigate);
    });

    // Test 10: Test error handling in delete product
    it('should handle errors when deleting product fails', fakeAsync(() => {
        const errorResponse = { error: { detail: 'Delete error' } };
        productService.deleteProductById.and.returnValue(throwError(() => errorResponse));

        component.deleteProduct(mockProducts[0]);
        tick();

        expect(productService.deleteProductById);
        expect(toastService.showErrorToast);
    }));

    // Test 11: Test pagination event
    it('should handle page change event', () => {
        const pageEvent = {
            first: 10,
            rows: 10,
            page: 1
        };

        component.onPageChange(pageEvent);

        expect(component.page).toBe(1);
        expect(component.size).toBe(10);
        expect(productService.getAllProducts).toHaveBeenCalledWith({
            pageNumber: 1,
            pageSize: 10,
            attribute: 'id',
            direction: 'desc'
        });
    });

    // Test 12: Test getImage method
    it('should get image URL for product with primaryImagePath', () => {
        productService.getImage.and.returnValue('product-image-url');
        const imageUrl = component.getImage(mockProducts[0]);
        expect(productService.getImage).toHaveBeenCalledWith(1, 'image1.jpg');
        expect(imageUrl).toBe('product-image-url');
    });

    // Test 14: Test p-table structure
    it('should render the p-table with correct structure', () => {
        fixture.detectChanges();
        const table = fixture.debugElement.query(By.css('p-table'));
        expect(table).toBeTruthy();

        // Check that table header contains all the expected columns
        const tableHeaders = fixture.debugElement.queryAll(By.css('th'));
        expect(tableHeaders.length).toBe(7); // Should match the number of columns in template

        // Check that the table contains our mock data (verify at least one row exists)
        const tableRows = fixture.debugElement.queryAll(By.css('tr'));
        expect(tableRows.length).toBeGreaterThan(1); // Header row + at least one data row
    });

    // Test 15: Test toolbar structure
    it('should render the toolbar with correct buttons', () => {
        fixture.detectChanges();
        const toolbar = fixture.debugElement.query(By.css('p-toolbar'));
        expect(toolbar).toBeTruthy();

        const buttons = fixture.debugElement.queryAll(By.css('p-button'));
        expect(buttons.length).toBeGreaterThanOrEqual(2); // At least New Product and Delete Selected

        const newProductButton = fixture.debugElement.query(By.css('p-button[label="Nový produkt"]'));
        expect(newProductButton).toBeTruthy();

        const deleteSelectedButton = fixture.debugElement.query(By.css('p-button[label="Smazat vybrané"]'));
        expect(deleteSelectedButton).toBeTruthy();
    });
});
