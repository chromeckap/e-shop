import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ProductCatalogComponent } from './product-catalog.component';
import { ProductService } from '../../../../../services/services/product.service';
import { CategoryService } from '../../../../../services/services/category.service';
import { ActivatedRoute, Router, Params } from '@angular/router';
import { of, Subject, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ProductPageResponse } from '../../../../../services/models/product/product-page-response';
import { CategoryResponse } from '../../../../../services/models/category/category-response';
import { FilterRangesResponse } from '../../../../../services/models/product/filter-ranges-response';
import { By } from '@angular/platform-browser';

describe('ProductCatalogComponent', () => {
    let component: ProductCatalogComponent;
    let fixture: ComponentFixture<ProductCatalogComponent>;
    let productServiceSpy: jasmine.SpyObj<ProductService>;
    let categoryServiceSpy: jasmine.SpyObj<CategoryService>;
    let routerSpy: jasmine.SpyObj<Router>;
    let paramsSubject: Subject<Params>;

    // Mock data
    const mockCategoryId = 123;
    const mockProductPage: ProductPageResponse = {
        content: [
            { id: 1, name: 'Product 1', price: 100 },
            { id: 2, name: 'Product 2', price: 200 }
        ],
        size: 27,
        totalElements: 50,
        totalPages: 2,
        number: 0
    };

    const mockCategory: CategoryResponse = {
        id: mockCategoryId,
        name: 'Test Category',
        description: 'Test description',
        children: [
            { id: 124, name: 'Child Category 1' },
            { id: 125, name: 'Child Category 2' }
        ]
    };

    const mockFilterRanges: FilterRangesResponse = {
        lowPrice: 50,
        maxPrice: 500,
        attributes: [
            {
                id: 1,
                name: 'Color',
                values: [
                    { id: 11, value: 'Red' },
                    { id: 12, value: 'Blue' }
                ]
            }
        ]
    };

    beforeEach(async () => {
        // Create spies for services and router
        productServiceSpy = jasmine.createSpyObj('ProductService', ['getProductsByCategory', 'getFilterRangesByCategory']);
        categoryServiceSpy = jasmine.createSpyObj('CategoryService', ['getCategoryById']);
        routerSpy = jasmine.createSpyObj('Router', ['navigate']);

        // Configure the parameter Observable
        paramsSubject = new Subject<Params>();

        await TestBed.configureTestingModule({
            imports: [ProductCatalogComponent],
            providers: [
                { provide: ProductService, useValue: productServiceSpy },
                { provide: CategoryService, useValue: categoryServiceSpy },
                { provide: Router, useValue: routerSpy },
                {
                    provide: ActivatedRoute,
                    useValue: {
                        params: paramsSubject.asObservable()
                    }
                }
            ],
            schemas: [NO_ERRORS_SCHEMA] // For child components
        }).compileComponents();

        // Configure spy return values
        productServiceSpy.getProductsByCategory.and.returnValue(of(mockProductPage));
        productServiceSpy.getFilterRangesByCategory.and.returnValue(of(mockFilterRanges));
        categoryServiceSpy.getCategoryById.and.returnValue(of(mockCategory));
        routerSpy.navigate.and.resolveTo(true);
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(ProductCatalogComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should initialize with default values', () => {
        expect(component.productsPage).toEqual({});
        expect(component.category).toEqual({});
        expect(component.childCategories).toEqual([]);
        expect(component.filterRanges).toEqual({});
        expect(component.page).toBe(0);
        expect(component.size).toBe(27);
        expect(component.attribute).toBe('id');
        expect(component.direction).toBe('desc');
        expect(component.currentPage).toBe(0);
        expect(component.currentFilterState).toEqual({
            lowPrice: 0,
            maxPrice: 0,
            attributeValueIds: []
        });
    });

    it('should load category data when route params change', fakeAsync(() => {
        // Simulate route parameter change
        paramsSubject.next({ id: mockCategoryId });
        tick();

        expect(categoryServiceSpy.getCategoryById).toHaveBeenCalledWith(mockCategoryId);
        expect(productServiceSpy.getFilterRangesByCategory).toHaveBeenCalledWith(mockCategoryId);
        expect(component.category).toEqual(mockCategory);
        expect(component.filterRanges).toEqual(mockFilterRanges);
        expect(component.childCategories.length).toBe(2);
        expect(productServiceSpy.getProductsByCategory).toHaveBeenCalled();
        expect(component.productsPage).toEqual(mockProductPage);
    }));

    it('should convert category children to menu items correctly', fakeAsync(() => {
        paramsSubject.next({ id: mockCategoryId });
        tick();

        expect(component.childCategories.length).toBe(2);
        expect(component.childCategories[0].label).toBe('Child Category 1');
        expect(component.childCategories[0].id).toBe('124');
        expect(component.childCategories[1].label).toBe('Child Category 2');
        expect(component.childCategories[1].id).toBe('125');
    }));

    it('should navigate when a child category is selected', fakeAsync(() => {
        paramsSubject.next({ id: mockCategoryId });
        tick();

        // Simulate clicking on a child category
        component.childCategories[0].command!({} as any);

        expect(routerSpy.navigate).toHaveBeenCalledWith(['/kategorie/124/produkty']);
    }));

    it('should update page and load products when page changes', fakeAsync(() => {
        paramsSubject.next({ id: mockCategoryId });
        tick();

        productServiceSpy.getProductsByCategory.calls.reset();

        component.onPageChange({ page: 1, size: 27 });

        expect(component.page).toBe(1);
        expect(component.currentPage).toBe(1);
        expect(component.size).toBe(27);
        expect(productServiceSpy.getProductsByCategory).toHaveBeenCalledWith(
            mockCategoryId,
            { pageNumber: 1, pageSize: 27, attribute: 'id', direction: 'desc' },
            { lowPrice: 50, maxPrice: 500, attributeValueIds: [] }
        );
    }));

    it('should update sort parameters and load products when sort changes', fakeAsync(() => {
        paramsSubject.next({ id: mockCategoryId });
        tick();

        productServiceSpy.getProductsByCategory.calls.reset();

        component.onSortChange({ attribute: 'price', direction: 'asc' });

        expect(component.attribute).toBe('price');
        expect(component.direction).toBe('asc');
        expect(component.page).toBe(0);
        expect(component.currentPage).toBe(0);
        expect(productServiceSpy.getProductsByCategory).toHaveBeenCalledWith(
            mockCategoryId,
            { pageNumber: 0, pageSize: 27, attribute: 'price', direction: 'asc' },
            { lowPrice: 50, maxPrice: 500, attributeValueIds: [] }
        );
    }));

    it('should update filter state and load products when filter changes', fakeAsync(() => {
        paramsSubject.next({ id: mockCategoryId });
        tick();

        productServiceSpy.getProductsByCategory.calls.reset();

        const filterChangeEvent = {
            rangeValues: [100, 400],
            selectedAttributeValues: {
                1: [11, 12] // Color: Red, Blue
            }
        };

        component.onFilterChange(filterChangeEvent);

        expect(component.currentFilterState).toEqual({
            lowPrice: 100,
            maxPrice: 400,
            attributeValueIds: [11, 12]
        });
        expect(component.page).toBe(0);
        expect(component.currentPage).toBe(0);
        expect(productServiceSpy.getProductsByCategory).toHaveBeenCalledWith(
            mockCategoryId,
            { pageNumber: 0, pageSize: 27, attribute: 'id', direction: 'desc' },
            { lowPrice: 100, maxPrice: 400, attributeValueIds: [11, 12] }
        );
    }));

    it('should handle complex filter changes with multiple attributes', fakeAsync(() => {
        paramsSubject.next({ id: mockCategoryId });
        tick();

        productServiceSpy.getProductsByCategory.calls.reset();

        const filterChangeEvent = {
            rangeValues: [100, 400],
            selectedAttributeValues: {
                1: [11, 12], // Color: Red, Blue
                2: [21, 22]  // Size: S, M
            }
        };

        component.onFilterChange(filterChangeEvent);

        expect(component.currentFilterState.attributeValueIds).toEqual([11, 12, 21, 22]);
        expect(productServiceSpy.getProductsByCategory).toHaveBeenCalled();
    }));

    it('should handle error when loading products', fakeAsync(() => {
        productServiceSpy.getProductsByCategory.and.returnValue(
            throwError(() => new Error('Failed to load products'))
        );

        spyOn(console, 'error');

        paramsSubject.next({ id: mockCategoryId });
        tick();

        expect(console.error).toHaveBeenCalled();
    }));

    it('should unsubscribe from route subscription on component destruction', fakeAsync(() => {
        paramsSubject.next({ id: mockCategoryId });
        tick();

        spyOn(component['routeSubscription']!, 'unsubscribe');

        component.ngOnDestroy();

        expect(component['routeSubscription']!.unsubscribe).toHaveBeenCalled();
    }));

    it('should handle case when category has no children', fakeAsync(() => {
        const noChildrenCategory = { ...mockCategory, children: [] };
        categoryServiceSpy.getCategoryById.and.returnValue(of(noChildrenCategory));

        paramsSubject.next({ id: mockCategoryId });
        tick();

        expect(component.childCategories).toEqual([]);
    }));

    it('should handle case when category has undefined children', fakeAsync(() => {
        const noChildrenCategory = { ...mockCategory, children: undefined };
        categoryServiceSpy.getCategoryById.and.returnValue(of(noChildrenCategory));

        paramsSubject.next({ id: mockCategoryId });
        tick();

        expect(component.childCategories).toEqual([]);
    }));
});
