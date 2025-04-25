import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProductGridComponent } from './product-grid.component';
import { ProductPageResponse } from '../../../../../services/models/product/product-page-response';
import { ProductOverviewResponse } from '../../../../../services/models/product/product-overview-response';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { By } from '@angular/platform-browser';
import {provideHttpClient} from "@angular/common/http";
import {provideRouter} from "@angular/router";

describe('ProductGridComponent', () => {
    let component: ProductGridComponent;
    let fixture: ComponentFixture<ProductGridComponent>;

    const mockProducts: ProductOverviewResponse[] = [
        {
            id: 1,
            name: 'Product 1',
            price: 199.99,
            basePrice: 249.99,
            isPriceEqual: false
        },
        {
            id: 2,
            name: 'Product 2',
            price: 299.99,
            basePrice: 299.99,
            isPriceEqual: true
        },
        {
            id: 3,
            name: 'Product 3',
            price: 99.99,
            basePrice: 149.99,
            isPriceEqual: false
        }
    ];

    const mockProductPage: ProductPageResponse = {
        content: mockProducts,
        size: 10,
        totalElements: 25,
        totalPages: 3,
        number: 0
    };

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ProductGridComponent],
            providers: [
                provideRouter([]),
                provideHttpClient()
            ],
            schemas: [NO_ERRORS_SCHEMA] // For ProductCardComponent and Paginator
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(ProductGridComponent);
        component = fixture.componentInstance;
        component.productPage = mockProductPage;
        component.currentPage = 0;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should render product cards for each product in the page', () => {
        const productCards = fixture.debugElement.queryAll(By.css('app-product-card'));
        expect(productCards.length).toBe(mockProducts.length);
    });

    it('should emit pageChanged event with correct data when page changes', () => {
        spyOn(component.pageChanged, 'emit');

        const mockPageEvent = {
            first: 10,
            rows: 10,
            page: 1
        };

        component.onPageChange(mockPageEvent);

        expect(component.pageChanged.emit).toHaveBeenCalledWith({
            page: 1,
            size: 10
        });
    });

    it('should emit pageChanged event with correct page calculation', () => {
        spyOn(component.pageChanged, 'emit');

        // Test different page numbers
        const testCases = [
            { event: { first: 0, rows: 10 }, expected: { page: 0, size: 10 } },
            { event: { first: 10, rows: 10 }, expected: { page: 1, size: 10 } },
            { event: { first: 20, rows: 10 }, expected: { page: 2, size: 10 } },
            // Test with different page sizes
            { event: { first: 0, rows: 5 }, expected: { page: 0, size: 5 } },
            { event: { first: 5, rows: 5 }, expected: { page: 1, size: 5 } },
            { event: { first: 15, rows: 5 }, expected: { page: 3, size: 5 } }
        ];

        testCases.forEach(test => {
            component.onPageChange(test.event);
            expect(component.pageChanged.emit).toHaveBeenCalledWith(test.expected);
        });
    });

    it('should handle page change when size is undefined', () => {
        spyOn(component.pageChanged, 'emit');

        component.productPage = {
            content: mockProducts,
            // No size defined
            totalElements: 25,
            totalPages: 3,
            number: 0
        };

        const mockPageEvent = {
            first: 10,
            rows: 10,
            page: 1
        };

        component.onPageChange(mockPageEvent);

        expect(component.pageChanged.emit).toHaveBeenCalledWith({
            page: 1,
            size: 10
        });
    });

    it('should apply the correct CSS classes to product cards', () => {
        const productCards = fixture.debugElement.queryAll(By.css('app-product-card'));

        productCards.forEach(card => {
            expect(card.nativeElement.classList.contains('p-4')).toBeTrue();
            expect(card.nativeElement.classList.contains('h-full')).toBeTrue();
        });
    });

    it('should apply the correct grid layout classes', () => {
        const gridContainer = fixture.debugElement.query(By.css('.grid'));

        expect(gridContainer.nativeElement.classList.contains('gap-4')).toBeTrue();
        expect(gridContainer.nativeElement.classList.contains('items-stretch')).toBeTrue();
        expect(gridContainer.nativeElement.classList.contains('grid-cols-[repeat(auto-fill,minmax(18rem,1fr))]')).toBeTrue();
        expect(gridContainer.nativeElement.classList.contains('2xl:grid-cols-4')).toBeTrue();
    });
});
