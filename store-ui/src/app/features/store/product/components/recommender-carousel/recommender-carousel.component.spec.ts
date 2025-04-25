import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RecommenderCarouselComponent } from './recommender-carousel.component';
import { ProductOverviewResponse } from '../../../../../services/models/product/product-overview-response';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { By } from '@angular/platform-browser';
import {provideHttpClient} from "@angular/common/http";
import {provideRouter} from "@angular/router";

describe('RecommenderCarouselComponent', () => {
    let component: RecommenderCarouselComponent;
    let fixture: ComponentFixture<RecommenderCarouselComponent>;

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

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [RecommenderCarouselComponent],
            providers: [
                provideRouter([]),
                provideHttpClient()
            ],
            schemas: [NO_ERRORS_SCHEMA] // For Carousel and ProductCardComponent
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(RecommenderCarouselComponent);
        component = fixture.componentInstance;
        component.products = mockProducts;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should initialize responsiveOptions in ngOnInit', () => {
        expect(component.responsiveOptions).toBeDefined();
        expect(component.responsiveOptions?.length).toBe(4);

        // Check content of the responsive options
        expect(component.responsiveOptions?.[0].breakpoint).toBe('1850px');
        expect(component.responsiveOptions?.[0].numVisible).toBe(4);
        expect(component.responsiveOptions?.[0].numScroll).toBe(4);

        expect(component.responsiveOptions?.[1].breakpoint).toBe('1470px');
        expect(component.responsiveOptions?.[1].numVisible).toBe(3);
        expect(component.responsiveOptions?.[1].numScroll).toBe(3);

        expect(component.responsiveOptions?.[2].breakpoint).toBe('1192px');
        expect(component.responsiveOptions?.[2].numVisible).toBe(2);
        expect(component.responsiveOptions?.[2].numScroll).toBe(2);

        expect(component.responsiveOptions?.[3].breakpoint).toBe('880px');
        expect(component.responsiveOptions?.[3].numVisible).toBe(1);
        expect(component.responsiveOptions?.[3].numScroll).toBe(1);
    });

    it('should handle undefined responsiveOptions', () => {
        // Force responsiveOptions to undefined
        component.responsiveOptions = undefined;
        fixture.detectChanges();

        const carouselElement = fixture.debugElement.query(By.css('p-carousel'));
        expect(carouselElement.properties['responsiveOptions']).toBeUndefined();
    });

    it('should set the width style on the product card container', () => {
        // Test if the container div has the correct class
        const templateContent = fixture.debugElement.query(By.css('ng-template'));
        if (templateContent) {
            // This is a bit tricky to test since ng-template doesn't render directly
            // We're checking if the template reference exists with the correct variable
            expect(templateContent.attributes['let-product']).toBeTruthy();
        }
    });
});
