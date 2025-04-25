import {ComponentFixture, TestBed} from '@angular/core/testing';
import {ReviewSummaryComponent} from './review-summary.component';
import {ProductRatingSummary} from '../../../../../services/models/review/product-rating-summary';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {By} from '@angular/platform-browser';

describe('ReviewSummaryComponent', () => {
    let component: ReviewSummaryComponent;
    let fixture: ComponentFixture<ReviewSummaryComponent>;

    // Mock data
    const mockSummary: ProductRatingSummary = {
        averageRating: 4.25,
        totalRatingsCount: 20,
        ratingCounts: new Map<number, number>([
            [1, 0],
            [2, 1],
            [3, 1],
            [4, 2],
            [5, 6]
        ])
    };

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [
                ReviewSummaryComponent,
                FormsModule
            ],
            schemas: [NO_ERRORS_SCHEMA] // For PrimeNG components
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(ReviewSummaryComponent);
        component = fixture.componentInstance;
        component.summary = mockSummary;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should display the average rating formatted to 2 decimal places', () => {
        const averageRatingElement = fixture.debugElement.query(By.css('.text-2xl.font-semibold'));
        expect(averageRatingElement.nativeElement.textContent.trim()).toBe('4.25');
    });

    it('should display the total ratings count', () => {
        const totalRatingsElement = fixture.debugElement.query(By.css('.text-sm.text-gray-600'));
        expect(totalRatingsElement.nativeElement.textContent.trim()).toBe('20 celkových hodnocení');
    });

    it('should handle undefined rating counts case', () => {
        // Set up a summary with undefined rating counts
        component.summary = {
            averageRating: 4.5,
            totalRatingsCount: 10
            // ratingCounts is undefined
        };
        fixture.detectChanges();

        // There should be no rating breakdown elements
        const ratingElements = fixture.debugElement.queryAll(By.css('.flex.items-center.mb-2'));
        expect(ratingElements.length).toBe(0);
    });

    it('should return null from ratingCountsList when summary.ratingCounts is undefined', () => {
        component.summary = {
            averageRating: 4.5,
            totalRatingsCount: 10
            // ratingCounts is undefined
        };

        expect(component.ratingCountsList).toBeNull();
    });

    it('should have correct text for each rating value', () => {
        const ratingElements = fixture.debugElement.queryAll(By.css('.flex.items-center.mb-2'));

        // Check all rating texts
        const ratings = [5, 4, 3, 2, 1];
        const counts = [8, 6, 3, 2, 1];

        ratingElements.forEach((element, index) => {
            const spans = element.queryAll(By.css('span'));
            expect(spans[0].nativeElement.textContent.trim()).toBe(ratings[index].toString());
            expect(spans[1].nativeElement.textContent.trim()).toBe(`${counts[index]}x`);
        });
    });

    it('should include the p-rating component for each rating row', () => {
        const ratingElements = fixture.debugElement.queryAll(By.css('.flex.items-center.mb-2'));

        ratingElements.forEach(element => {
            const ratingComponent = element.query(By.css('p-rating'));
            expect(ratingComponent).toBeTruthy();
        });
    });
});
