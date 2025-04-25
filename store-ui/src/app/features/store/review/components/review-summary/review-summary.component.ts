import {Component, Input} from '@angular/core';
import {ProductRatingSummary} from "../../../../../services/models/review/product-rating-summary";
import {Rating} from "primeng/rating";
import {FormsModule} from "@angular/forms";
import {Divider} from "primeng/divider";

@Component({
    selector: 'app-review-summary',
    imports: [
        Rating,
        FormsModule,
        Divider
    ],
    templateUrl: './review-summary.component.html',
    standalone: true,
    styleUrl: './review-summary.component.scss'
})
export class ReviewSummaryComponent {
    @Input() summary!: ProductRatingSummary;
    protected readonly Number = Number;

    get ratingCountsList() {
        if (this.summary.ratingCounts) {
            return Object.entries(this.summary.ratingCounts)
                .sort(([keyA], [keyB]) => +keyB - +keyA);
        }
        return null;
    }
}
