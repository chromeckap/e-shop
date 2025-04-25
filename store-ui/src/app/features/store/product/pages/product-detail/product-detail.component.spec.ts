import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ProductDetailComponent } from './product-detail.component';
import { ProductService } from '../../../../../services/services/product.service';
import { RecommendationService } from '../../../../../services/services/recommendation.service';
import { ActivatedRoute, Params } from '@angular/router';
import { of, Subject } from 'rxjs';
import { NO_ERRORS_SCHEMA, Component } from '@angular/core';
import { ProductResponse } from '../../../../../services/models/product/product-response';
import { ProductOverviewResponse } from '../../../../../services/models/product/product-overview-response';
import { ReviewContainerComponent } from '../../../review/components/review-container/review-container.component';
import { ProductInfoComponent } from '../../components/product-info/product-info.component';
import { By } from '@angular/platform-browser';
import {provideHttpClient} from "@angular/common/http";
import {MessageService} from "primeng/api";

// Create stub components for testing
@Component({
    selector: 'app-review-container',
    template: ''
})
class ReviewContainerStubComponent {
    setReviewsByProductId(id: number): void {}
}

@Component({
    selector: 'app-product-info',
    template: ''
})
class ProductInfoStubComponent {
    loadData(product: ProductResponse): void {}
}

@Component({
    selector: 'app-recommender-carousel',
    template: ''
})
class RecommenderCarouselStubComponent {
}

describe('ProductDetailComponent', () => {
    let component: ProductDetailComponent;
    let fixture: ComponentFixture<ProductDetailComponent>;
    let productServiceSpy: jasmine.SpyObj<ProductService>;
    let recommendationServiceSpy: jasmine.SpyObj<RecommendationService>;
    let paramsSubject: Subject<Params>;

    // Mock data
    const mockProductId = 123;
    const mockProduct: ProductResponse = {
        id: mockProductId,
        name: 'Test Product',
        price: 199.99,
        basePrice: 249.99,
        isPriceEqual: false,
        description: 'Test product description'
    };

    const mockRecommendedProducts: ProductOverviewResponse[] = [
        {
            id: 124,
            name: 'Recommended Product 1',
            price: 99.99,
            basePrice: 99.99,
            isPriceEqual: true
        },
        {
            id: 125,
            name: 'Recommended Product 2',
            price: 149.99,
            basePrice: 199.99,
            isPriceEqual: false
        }
    ];

    beforeEach(async () => {
        // Create spies for services
        productServiceSpy = jasmine.createSpyObj('ProductService', ['getProductById']);
        recommendationServiceSpy = jasmine.createSpyObj('RecommendationService', ['getRecommendationsByProductId']);

        // Configure the parameter Observable
        paramsSubject = new Subject<Params>();

        await TestBed.configureTestingModule({
            imports: [ProductDetailComponent],
            providers: [
                { provide: ProductService, useValue: productServiceSpy },
                { provide: RecommendationService, useValue: recommendationServiceSpy },
                {
                    provide: ActivatedRoute,
                    useValue: {
                        params: paramsSubject.asObservable()
                    }
                },
                provideHttpClient(),
                MessageService
            ],
            schemas: [NO_ERRORS_SCHEMA]
        }).compileComponents();

        // Configure spy return values
        productServiceSpy.getProductById.and.returnValue(of(mockProduct));
        recommendationServiceSpy.getRecommendationsByProductId.and.returnValue(of(mockRecommendedProducts));
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(ProductDetailComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should initialize with empty product and recommendations', () => {
        expect(component.product).toEqual({});
        expect(component.recommendedProducts).toEqual([]);
    });

    it('should display recommender carousel only when recommendations exist', fakeAsync(() => {
        // First load with recommendations
        component.recommendedProducts = mockRecommendedProducts;
        fixture.detectChanges();

        let recommenderEl = fixture.debugElement.query(By.css('app-recommender-carousel'));
        expect(recommenderEl).toBeTruthy();

        // Then with empty recommendations
        component.recommendedProducts = [];
        fixture.detectChanges();

        recommenderEl = fixture.debugElement.query(By.css('app-recommender-carousel'));
        expect(recommenderEl).toBeFalsy();
    }));
});
