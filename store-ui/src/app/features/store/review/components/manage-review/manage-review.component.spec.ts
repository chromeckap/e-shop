import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ManageReviewComponent } from './manage-review.component';
import { ReviewService } from '../../../../../services/services/review.service';
import { AuthService } from '../../../../../services/services/auth.service';
import { ToastService } from '../../../../../shared/services/toast.service';
import { ActivatedRoute } from '@angular/router';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ReviewRequest } from '../../../../../services/models/review/review-request';
import { of, throwError } from 'rxjs';
import { By } from '@angular/platform-browser';

describe('ManageReviewComponent', () => {
    let component: ManageReviewComponent;
    let fixture: ComponentFixture<ManageReviewComponent>;
    let reviewServiceSpy: jasmine.SpyObj<ReviewService>;
    let authServiceSpy: jasmine.SpyObj<AuthService>;
    let toastServiceSpy: jasmine.SpyObj<ToastService>;

    const mockUser = { id: 123, name: 'Test User' };
    const mockProductId = 456;

    beforeEach(async () => {
        // Create spies for services
        reviewServiceSpy = jasmine.createSpyObj('ReviewService', ['createReview', 'updateReview']);
        authServiceSpy = jasmine.createSpyObj('AuthService', [], {
            isLoggedIn: true,
            getCurrentUser: mockUser
        });
        toastServiceSpy = jasmine.createSpyObj('ToastService', ['showSuccessToast', 'showErrorToast']);

        // Configure spy behavior
        reviewServiceSpy.createReview.and.returnValue(of());
        reviewServiceSpy.updateReview.and.returnValue(of());
        toastServiceSpy.showSuccessToast.and.resolveTo();
        toastServiceSpy.showErrorToast.and.resolveTo();

        await TestBed.configureTestingModule({
            imports: [
                ManageReviewComponent,
                FormsModule,
                ReactiveFormsModule
            ],
            providers: [
                { provide: ReviewService, useValue: reviewServiceSpy },
                { provide: AuthService, useValue: authServiceSpy },
                { provide: ToastService, useValue: toastServiceSpy },
                {
                    provide: ActivatedRoute,
                    useValue: {
                        snapshot: {
                            params: { id: mockProductId }
                        }
                    }
                }
            ],
            schemas: [NO_ERRORS_SCHEMA] // For PrimeNG components
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(ManageReviewComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should initialize with default values', () => {
        expect(component.review).toEqual({});
        expect(component.dialogVisible).toBeFalse();
    });

    it('should check if user is logged in', () => {
        expect(component.isLoggedIn).toBeTrue();

        // Test when user is not logged in
        Object.defineProperty(authServiceSpy, 'isLoggedIn', { get: () => false });
        expect(component.isLoggedIn).toBeFalse();
    });

    it('should show review creation dialog with empty review', () => {
        component.showDialog();

        expect(component.dialogVisible).toBeTrue();
        expect(component.review).toEqual({ rating: undefined, text: '' });
    });

    it('should show review update dialog with existing review data', () => {
        const existingReview: ReviewRequest = {
            id: 1,
            rating: 4,
            text: 'Existing review text',
            userId: 123,
            productId: mockProductId
        };

        component.showDialog(existingReview);

        expect(component.dialogVisible).toBeTrue();
        expect(component.review).toEqual(existingReview);
        expect(component.review).not.toBe(existingReview); // Should be a copy, not a reference
    });

    it('should handle error when creating a review fails', fakeAsync(() => {
        spyOn(component.reviewCreated, 'emit');
        reviewServiceSpy.createReview.and.returnValue(
            throwError(() => ({ error: { detail: 'Failed to create review' } }))
        );

        component.showDialog();
        component.review.rating = 5;
        component.review.text = 'This is a great product!';

        component.saveReview();
        tick();

        expect(component.reviewCreated.emit).not.toHaveBeenCalled();
        expect(toastServiceSpy.showErrorToast).toHaveBeenCalledWith('Chyba', 'Failed to create review');
    }));

    it('should handle error when updating a review fails', fakeAsync(() => {
        spyOn(component.reviewUpdated, 'emit');
        reviewServiceSpy.updateReview.and.returnValue(
            throwError(() => ({ error: { detail: 'Failed to update review' } }))
        );

        const existingReview: ReviewRequest = {
            id: 123,
            rating: 3,
            text: 'Original review text'
        };

        component.showDialog(existingReview);
        component.saveReview();
        tick();

        expect(component.reviewUpdated.emit).not.toHaveBeenCalled();
        expect(toastServiceSpy.showErrorToast).toHaveBeenCalledWith('Chyba', 'Failed to update review');
    }));

    it('should display login button when user is not logged in', () => {
        // Set isLoggedIn to false
        Object.defineProperty(authServiceSpy, 'isLoggedIn', { get: () => false });
        fixture.detectChanges();

        const loginButton = fixture.debugElement.query(By.css('p-button[routerLink="/prihlaseni"]'));
        expect(loginButton).toBeTruthy();

        const addReviewButton = fixture.debugElement.query(By.css('p-button[icon="pi pi-pencil"]'));
        expect(addReviewButton).toBeFalsy();
    });

    it('should display add review button when user is logged in', () => {
        // Ensure isLoggedIn is true
        Object.defineProperty(authServiceSpy, 'isLoggedIn', { get: () => true });
        fixture.detectChanges();

        const addReviewButton = fixture.debugElement.query(By.css('p-button[icon="pi pi-pencil"]'));
        expect(addReviewButton).toBeTruthy();

        const loginButton = fixture.debugElement.query(By.css('p-button[routerLink="/prihlaseni"]'));
        expect(loginButton).toBeFalsy();
    });

    it('should open dialog when add review button is clicked', () => {
        spyOn(component, 'showDialog');

        const addReviewButton = fixture.debugElement.query(By.css('p-button[icon="pi pi-pencil"]'));
        addReviewButton.triggerEventHandler('onClick', null);

        expect(component.showDialog).toHaveBeenCalled();
    });
});
