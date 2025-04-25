import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RelatedProductMultiSelectComponent } from './related-product-multiselect.component';
import { ProductService } from '../../../../../services/services/product.service';
import { of } from 'rxjs';
import { FormControl, FormGroup } from '@angular/forms';
import { ProductOverviewResponse } from '../../../../../services/models/product/product-overview-response';
import { ProductPageResponse } from '../../../../../services/models/product/product-page-response';

describe('RelatedProductMultiSelectComponent', () => {
    let component: RelatedProductMultiSelectComponent;
    let fixture: ComponentFixture<RelatedProductMultiSelectComponent>;
    let productServiceSpy: jasmine.SpyObj<ProductService>;

    const mockProducts: ProductOverviewResponse[] = [
        { id: 1, name: 'Product 1', primaryImagePath: 'image1.jpg' },
        { id: 2, name: 'Product 2', primaryImagePath: 'image2.jpg' }
    ];

    const mockResponse: ProductPageResponse = {
        content: mockProducts,
        totalElements: 2
    };

    beforeEach(async () => {
        const spy = jasmine.createSpyObj('ProductService', ['getAllProducts', 'getImage']);

        await TestBed.configureTestingModule({
            imports: [RelatedProductMultiSelectComponent],
            providers: [{ provide: ProductService, useValue: spy }]
        }).compileComponents();

        productServiceSpy = TestBed.inject(ProductService) as jasmine.SpyObj<ProductService>;
        productServiceSpy.getAllProducts.and.returnValue(of(mockResponse));
        productServiceSpy.getImage.and.callFake((id: number, path: string) => `url/${id}/${path}`);

        fixture = TestBed.createComponent(RelatedProductMultiSelectComponent);
        component = fixture.componentInstance;
        component.form = new FormGroup({
            relatedProducts: new FormControl([])
        });
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should load products on init', () => {
        expect(productServiceSpy.getAllProducts).toHaveBeenCalledWith({
            pageNumber: 0,
            pageSize: 5,
            attribute: 'id',
            direction: 'desc'
        });
        expect(component.productsPage.content?.length).toBe(2);
    });

    it('should handle page change', () => {
        const event = { first: 5, rows: 5 };
        component.onPageChange(event);
        expect(component.page).toBe(1);
        expect(component.size).toBe(5);
        expect(productServiceSpy.getAllProducts).toHaveBeenCalledTimes(2); // once on init, once on change
    });

    it('should update form value on selection change', () => {
        component.onSelectionChange([mockProducts[0]]);
        const formValue = component.form.get('relatedProducts')?.value;
        expect(formValue).toEqual([mockProducts[0]]);
    });

    it('should return correct image path', () => {
        const path = component.getImage(mockProducts[0]);
        expect(path).toBe('url/1/image1.jpg');
    });

    it('should return fallback image when no path is set', () => {
        const noImageProduct = { id: 3, name: 'No image' };
        const path = component.getImage(noImageProduct as any);
        expect(path).toBe('assets/img/image-not-found.png');
    });
});
