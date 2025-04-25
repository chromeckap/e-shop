import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReviewContainerComponent } from './review-container.component';
import { ReviewService } from '../../../../../services/services/review.service';
import { NO_ERRORS_SCHEMA, Component } from '@angular/core';
import { of } from 'rxjs';
import { ReviewPageResponse } from '../../../../../services/models/review/review-page-response';
import { ProductRatingSummary } from '../../../../../services/models/review/product-rating-summary';
import { ReviewResponse } from '../../../../../services/models/review/review-response';
import { By } from '@angular/platform-browser';
import { ManageReviewComponent } from '../manage-review/manage-review.component';
import {provideHttpClient} from "@angular/common/http";
import {MessageService} from "primeng/api";
import {ActivatedRoute, Router} from "@angular/router";

// Create stub components for testing
@Component({
    selector: 'app-review-summary',
    template: ''
})
class ReviewSummaryStubComponent {
    summary: ProductRatingSummary = {};
}

@Component({
    selector: 'app-review-list',
    template: ''
})
class ReviewListStubComponent {
    reviewsPage: ReviewPageResponse = {};
    currentPage = 0;
}

@Component({
    selector: 'app-manage-review',
    template: ''
})
class ManageReviewStubComponent {
    showDialog(review?: any): void {}
}

describe('ReviewContainerComponent', () => {
    let component: ReviewContainerComponent;
    let fixture: ComponentFixture<ReviewContainerComponent>;
    let reviewServiceSpy: jasmine.SpyObj<ReviewService>;

    // Mock data
    const mockProductId = 123;
    const mockReviewsPage: ReviewPageResponse = {
        content: [
            { id: 1, rating: 5, text: 'Great product', userId: 1, productId: mockProductId },
            { id: 1, rating: 4, text: 'Good product', userId: 2, productId: mockProductId }
        ],
        size: 5,
        totalElements: 10,
        totalPages: 2,
        number: 0
    };

    const mockSummary: ProductRatingSummary = {
        averageRating: 4.5,
        totalRatingsCount: 10,
        ratingCounts: new Map<number, number>([
            [1, 0],
            [2, 1],
            [3, 1],
            [4, 2],
            [5, 6]
        ])
    };

    beforeEach(async () => {
        // Create spies for services
        reviewServiceSpy = jasmine.createSpyObj('ReviewService', ['getReviewsByProductId', 'getSummaryByProductId']);

        // Configure spy return values
        reviewServiceSpy.getReviewsByProductId.and.returnValue(of(mockReviewsPage));
        reviewServiceSpy.getSummaryByProductId.and.returnValue(of(mockSummary));

        await TestBed.configureTestingModule({
            imports: [ReviewContainerComponent],
            providers: [
                { provide: ReviewService, useValue: reviewServiceSpy },
                provideHttpClient(),
                MessageService,
                {
                    provide: ActivatedRoute,
                    useValue: {}
                },
            ],
            schemas: [NO_ERRORS_SCHEMA] // For PrimeNG components
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(ReviewContainerComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should initialize with default values', () => {
        expect(component.productId).toBeUndefined();
        expect(component.reviewsPage).toEqual({});
        expect(component.summary).toEqual({});
        expect(component.page).toBe(0);
        expect(component.size).toBe(5);
        expect(component.attribute).toBe('createTime');
        expect(component.direction).toBe('desc');
        expect(component.currentPage).toBe(0);
    });

    it('should load reviews and summary when setReviewsByProductId is called', fakeAsync(() => {
        component.setReviewsByProductId(mockProductId);
        tick();

        expect(component.productId).toBe(mockProductId);
        expect(reviewServiceSpy.getReviewsByProductId).toHaveBeenCalledWith(
            mockProductId,
            { pageNumber: 0, pageSize: 5, attribute: 'createTime', direction: 'desc' }
        );
        expect(reviewServiceSpy.getSummaryByProductId).toHaveBeenCalledWith(mockProductId);
        expect(component.reviewsPage).toEqual(mockReviewsPage);
        expect(component.summary).toEqual(mockSummary);
    }));

    it('should update page and reload reviews when onReviewPageChange is called', fakeAsync(() => {
        component.productId = mockProductId;
        reviewServiceSpy.getReviewsByProductId.calls.reset();
        reviewServiceSpy.getSummaryByProductId.calls.reset();

        component.onReviewPageChange({ page: 1, size: 10 });
        tick();

        expect(component.page).toBe(1);
        expect(component.currentPage).toBe(1);
        expect(component.size).toBe(10);
        expect(reviewServiceSpy.getReviewsByProductId).toHaveBeenCalledWith(
            mockProductId,
            { pageNumber: 1, pageSize: 10, attribute: 'createTime', direction: 'desc' }
        );
        expect(reviewServiceSpy.getSummaryByProductId).toHaveBeenCalledWith(mockProductId);
    }));

    it('should update sort parameters and reload reviews when onReviewSortChange is called', fakeAsync(() => {
        component.productId = mockProductId;
        reviewServiceSpy.getReviewsByProductId.calls.reset();
        reviewServiceSpy.getSummaryByProductId.calls.reset();

        component.onReviewSortChange({ attribute: 'rating', direction: 'asc' });
        tick();

        expect(component.attribute).toBe('rating');
        expect(component.direction).toBe('asc');
        expect(component.page).toBe(0);
        expect(component.currentPage).toBe(0);
        expect(reviewServiceSpy.getReviewsByProductId).toHaveBeenCalledWith(
            mockProductId,
            { pageNumber: 0, pageSize: 5, attribute: 'rating', direction: 'asc' }
        );
        expect(reviewServiceSpy.getSummaryByProductId).toHaveBeenCalledWith(mockProductId);
    }));

    it('should reset page and reload reviews when onReviewCreated is called', fakeAsync(() => {
        component.productId = mockProductId;
        component.page = 1;
        component.currentPage = 1;
        reviewServiceSpy.getReviewsByProductId.calls.reset();
        reviewServiceSpy.getSummaryByProductId.calls.reset();

        component.onReviewCreated();
        tick();

        expect(component.page).toBe(0);
        expect(component.currentPage).toBe(0);
        expect(reviewServiceSpy.getReviewsByProductId).toHaveBeenCalledWith(
            mockProductId,
            { pageNumber: 0, pageSize: 5, attribute: 'createTime', direction: 'desc' }
        );
        expect(reviewServiceSpy.getSummaryByProductId).toHaveBeenCalledWith(mockProductId);
    }));

    it('should reset page and reload reviews when onReviewDeleted is called', fakeAsync(() => {
        component.productId = mockProductId;
        component.page = 1;
        component.currentPage = 1;
        reviewServiceSpy.getReviewsByProductId.calls.reset();
        reviewServiceSpy.getSummaryByProductId.calls.reset();

        component.onReviewDeleted();
        tick();

        expect(component.page).toBe(0);
        expect(component.currentPage).toBe(0);
        expect(reviewServiceSpy.getReviewsByProductId).toHaveBeenCalledWith(
            mockProductId,
            { pageNumber: 0, pageSize: 5, attribute: 'createTime', direction: 'desc' }
        );
        expect(reviewServiceSpy.getSummaryByProductId).toHaveBeenCalledWith(mockProductId);
    }));

    it('should handle case with no reviews', fakeAsync(() => {
        const emptyReviewsPage: ReviewPageResponse = {
            content: [],
            size: 5,
            totalElements: 0,
            totalPages: 0,
            number: 0
        };

        reviewServiceSpy.getReviewsByProductId.and.returnValue(of(emptyReviewsPage));

        component.setReviewsByProductId(mockProductId);
        tick();

        expect(component.reviewsPage).toEqual(emptyReviewsPage);
    }));

    it('should apply responsive layout classes correctly', () => {
        // Test for responsive dividers
        const verticalDivider = fixture.debugElement.query(By.css('p-divider[layout="vertical"]'));
        expect(verticalDivider.classes['!hidden']).toBeTrue();
        expect(verticalDivider.classes['lg:!flex']).toBeTrue();

        const horizontalDivider = fixture.debugElement.query(By.css('p-divider[layout="horizontal"]'));
        expect(horizontalDivider.classes['!flex']).toBeTrue();
        expect(horizontalDivider.classes['lg:!hidden']).toBeTrue();

        // Test for column width classes
        const summaryColumn = fixture.debugElement.query(By.css('.lg\\:w-1\\/3'));
        expect(summaryColumn).toBeTruthy();

        const reviewsColumn = fixture.debugElement.query(By.css('.lg\\:w-2\\/3'));
        expect(reviewsColumn).toBeTruthy();
    });
});
