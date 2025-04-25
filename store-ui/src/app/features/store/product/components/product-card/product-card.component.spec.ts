import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ProductCardComponent } from './product-card.component';
import { ProductService } from '../../../../../services/services/product.service';
import { ProductOverviewResponse } from '../../../../../services/models/product/product-overview-response';
import { By } from '@angular/platform-browser';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { NgClass } from '@angular/common';
import {provideRouter, RouterLink} from '@angular/router';

describe('ProductCardComponent', () => {
    let component: ProductCardComponent;
    let fixture: ComponentFixture<ProductCardComponent>;
    let productServiceSpy: jasmine.SpyObj<ProductService>;

    const mockProduct: ProductOverviewResponse = {
        id: 1,
        name: 'Test Product',
        price: 199.99,
        basePrice: 249.99,
        isPriceEqual: false,
        primaryImagePath: 'test-image.jpg'
    };

    beforeEach(async () => {
        productServiceSpy = jasmine.createSpyObj('ProductService', ['getImage']);
        productServiceSpy.getImage.and.returnValue('https://test-cdn.com/images/prod123/test-image.jpg');

        await TestBed.configureTestingModule({
            imports: [
                ProductCardComponent,
                NgClass,
                RouterLink,
            ],
            providers: [
                provideRouter([]),
                { provide: ProductService, useValue: productServiceSpy }
            ],
            schemas: [NO_ERRORS_SCHEMA] // For PriceDisplayComponent
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(ProductCardComponent);
        component = fixture.componentInstance;
        component.product = mockProduct;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should initialize with loading state true', () => {
        // Create a fresh component to check initial state
        fixture = TestBed.createComponent(ProductCardComponent);
        component = fixture.componentInstance;

        expect(component.isLoading).toBeTrue();
    });

    it('should call productService.getImage when product has primaryImagePath', () => {
        component.getImage(mockProduct);
        expect(productServiceSpy.getImage).toHaveBeenCalledWith(1, 'test-image.jpg');
    });

    it('should return fallback image when product has no ID', () => {
        const noIdProduct: ProductOverviewResponse = {};
        const result = component.getImage(noIdProduct);
        expect(result).toBe('assets/img/image-not-found.png');
        expect(productServiceSpy.getImage);
    });

    it('should return fallback image when product has no primaryImagePath', () => {
        const noImageProduct: ProductOverviewResponse = { id: 1 };
        const result = component.getImage(noImageProduct);
        expect(result).toBe('assets/img/image-not-found.png');
        expect(productServiceSpy.getImage);
    });

    it('should set isLoading to false when image loads', () => {
        component.isLoading = true;
        component.onImageLoaded();
        expect(component.isLoading).toBeFalse();
    });

    it('should set isLoading to false when image fails to load', () => {
        component.isLoading = true;
        component.onImageError();
        expect(component.isLoading).toBeFalse();
    });

    it('should display product name', () => {
        const nameElement = fixture.debugElement.query(By.css('h2'));
        expect(nameElement.nativeElement.textContent.trim()).toBe('Test Product');
    });

    it('should show loading placeholder when isLoading is true', () => {
        component.isLoading = true;
        fixture.detectChanges();

        const placeholderElement = fixture.debugElement.query(By.css('.bg-gray-200'));
        expect(placeholderElement).toBeTruthy();
    });

    it('should not show loading placeholder when isLoading is false', () => {
        component.isLoading = false;
        fixture.detectChanges();

        const placeholderElement = fixture.debugElement.query(By.css('.bg-gray-200'));
        expect(placeholderElement).toBeFalsy();
    });

    it('should trigger onImageLoaded when image loads', () => {
        spyOn(component, 'onImageLoaded');

        const imgElement = fixture.debugElement.query(By.css('img'));
        imgElement.triggerEventHandler('load', null);

        expect(component.onImageLoaded).toHaveBeenCalled();
    });

    it('should trigger onImageError when image fails to load', () => {
        spyOn(component, 'onImageError');

        const imgElement = fixture.debugElement.query(By.css('img'));
        imgElement.triggerEventHandler('error', null);

        expect(component.onImageError).toHaveBeenCalled();
    });
});
