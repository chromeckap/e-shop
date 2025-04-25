import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReviewListComponent } from './review-list.component';
import { AuthService } from '../../../../../services/services/auth.service';
import { ReviewService } from '../../../../../services/services/review.service';
import { ConfirmationService } from 'primeng/api';
import { ToastService } from '../../../../../shared/services/toast.service';
import { ReviewPageResponse } from '../../../../../services/models/review/review-page-response';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { of } from 'rxjs';
import { By } from '@angular/platform-browser';

describe('ReviewListComponent', () => {
    let component: ReviewListComponent;
    let fixture: ComponentFixture<ReviewListComponent>;
    let authServiceSpy: jasmine.SpyObj<AuthService>;
    let reviewServiceSpy: jasmine.SpyObj<ReviewService>;
    let confirmationServiceSpy: jasmine.SpyObj<ConfirmationService>;
    let toastServiceSpy: jasmine.SpyObj<ToastService>;

    // Mock data
    const mockUser = { id: 'user1', name: 'Test User' };
    const mockAdminUser = { id: 'admin1', name: 'Admin User' };

    const mockReviewsPage: ReviewPageResponse = {
        content: [
            { id: 1, rating: 5, text: 'Great product', userId: 1, productId: 123, createTime: new Date('2023-01-01T10:00:00') },
            { id: 2, rating: 4, text: 'Good product', userId: 2, productId: 123, createTime: new Date('2023-01-02T11:00:00') }
        ],
        size: 5,
        totalElements: 10,
        totalPages: 2,
        number: 0
    };

    beforeEach(async () => {
        // Create spies for services
        authServiceSpy = jasmine.createSpyObj('AuthService', [], {
            isLoggedIn: true,
            isAdmin: false,
            getCurrentUser: mockUser
        });

        reviewServiceSpy = jasmine.createSpyObj('ReviewService', ['deleteReviewById']);
        confirmationServiceSpy = jasmine.createSpyObj('ConfirmationService', ['confirm']);
        toastServiceSpy = jasmine.createSpyObj('ToastService', ['showSuccessToast', 'showErrorToast']);

        // Configure spy behavior
        reviewServiceSpy.deleteReviewById.and.returnValue(of());
        toastServiceSpy.showSuccessToast.and.resolveTo();
        toastServiceSpy.showErrorToast.and.resolveTo();

        await TestBed.configureTestingModule({
            imports: [
                ReviewListComponent,
                FormsModule
            ],
            providers: [
                { provide: AuthService, useValue: authServiceSpy },
                { provide: ReviewService, useValue: reviewServiceSpy },
                { provide: ConfirmationService, useValue: confirmationServiceSpy },
                { provide: ToastService, useValue: toastServiceSpy },
                DatePipe
            ],
            schemas: [NO_ERRORS_SCHEMA] // For PrimeNG components
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(ReviewListComponent);
        component = fixture.componentInstance;
        component.reviewsPage = mockReviewsPage;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should initialize with default values', () => {
        expect(component.currentPage).toBe(0);
        expect(component.sortOptions.length).toBe(3);
        expect(component.selectedSortOption).toBe(component.sortOptions[0]);
    });

    it('should render reviews from reviewsPage', () => {
        const reviewElements = fixture.debugElement.queryAll(By.css('.bg-gray-50'));
        expect(reviewElements.length).toBe(2);

        const reviewTexts = fixture.debugElement.queryAll(By.css('p.mb-4'));
        expect(reviewTexts[0].nativeElement.textContent.trim()).toBe('Great product');
        expect(reviewTexts[1].nativeElement.textContent.trim()).toBe('Good product');
    });

    it('should emit pageChanged event when page changes', () => {
        spyOn(component.pageChanged, 'emit');

        const pageEvent = {
            first: 5,
            rows: 5,
            page: 1
        };

        component.onPageChange(pageEvent);

        expect(component.pageChanged.emit).toHaveBeenCalledWith({
            page: 1,
            size: 5
        });
    });

    it('should emit sortChanged event when sort changes', () => {
        spyOn(component.sortChanged, 'emit');

        // Select rating desc sort option
        component.selectedSortOption = component.sortOptions[1];
        component.onSortChange();

        expect(component.sortChanged.emit).toHaveBeenCalledWith({
            attribute: 'rating',
            direction: 'desc'
        });
    });

    it('should not show edit button for other users\' reviews', () => {
        fixture.detectChanges();

        // Second review belongs to another user
        const secondReviewElement = fixture.debugElement.queryAll(By.css('.bg-gray-50'))[1];
        const editButton = secondReviewElement.query(By.css('p-button[icon="pi pi-pencil"]'));

        expect(editButton).toBeFalsy();
    });

    it('should show delete button for admin user on all reviews', () => {
        // Make user an admin
        Object.defineProperty(authServiceSpy, 'isAdmin', { get: () => true });
        fixture.detectChanges();

        // Second review belongs to another user
        const secondReviewActions = fixture.debugElement.queryAll(By.css('.bg-gray-50'))[1]
            .query(By.css('.flex.gap-2'));

        expect(secondReviewActions).toBeTruthy();
        const deleteButton = secondReviewActions.query(By.css('p-button[icon="pi pi-trash"]'));
        expect(deleteButton).toBeTruthy();
    });

    it('should not show "Tvá recenze" tag for other users\' reviews', () => {
        fixture.detectChanges();

        // Second review belongs to another user
        const secondReview = fixture.debugElement.queryAll(By.css('.bg-gray-50'))[1];
        const tagElement = secondReview.query(By.css('p-tag[value="Tvá recenze"]'));

        expect(tagElement).toBeFalsy();
    });

    it('should format date correctly', () => {
        fixture.detectChanges();

        const dateElements = fixture.debugElement.queryAll(By.css('.text-gray-500.text-sm'));

        // Test depends on how DatePipe formats dates, which can vary by locale
        // This is a flexible test that just verifies the date is shown
        expect(dateElements[0].nativeElement.textContent).toContain('01.01.2023');
    });

    it('should handle empty reviews list', () => {
        component.reviewsPage = {
            content: [],
            size: 5,
            totalElements: 0,
            totalPages: 0,
            number: 0
        };
        fixture.detectChanges();

        const reviewElements = fixture.debugElement.queryAll(By.css('.bg-gray-50'));
        expect(reviewElements.length).toBe(0);
    });
});
