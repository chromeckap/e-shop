import {Component, ViewChild} from '@angular/core';
import {Divider} from "primeng/divider";
import {ReviewSummaryComponent} from "../review-summary/review-summary.component";
import {ReviewPageResponse} from "../../../../../services/models/review/review-page-response";
import {ReviewService} from "../../../../../services/services/review.service";
import {ProductRatingSummary} from "../../../../../services/models/review/product-rating-summary";
import {ReviewListComponent} from "../review-list/review-list.component";
import {ManageReviewComponent} from "../manage-review/manage-review.component";
import {ReviewResponse} from "../../../../../services/models/review/review-response";

@Component({
    selector: 'app-review-container',
    imports: [
        Divider,
        ReviewSummaryComponent,
        ReviewListComponent,
        ManageReviewComponent
    ],
    templateUrl: './review-container.component.html',
    standalone: true,
    styleUrl: './review-container.component.scss'
})
export class ReviewContainerComponent {
    @ViewChild('manageReview') manageReviewComponent!: ManageReviewComponent;

    productId: number | undefined;
    reviewsPage: ReviewPageResponse = {};
    summary: ProductRatingSummary = {};

    page = 0;
    size = 5;
    attribute = 'createTime';
    direction = 'desc';
    currentPage = 0;

    constructor(
        private reviewService: ReviewService,
    ) {
    }

    setReviewsByProductId(id: number) {
        this.productId = id;
        this.loadReviews();
    }

    loadReviews() {
        this.reviewService.getReviewsByProductId(
            this.productId!,
            {
                pageNumber: this.page,
                pageSize: this.size,
                attribute: this.attribute,
                direction: this.direction
            })
            .subscribe(reviews => {
                this.reviewsPage = reviews;
            });

        this.reviewService.getSummaryByProductId(this.productId!)
            .subscribe(summary => {
                this.summary = summary;
            });
    }

    onReviewPageChange(event: { page: number, size: number }): void {
        this.page = event.page;
        this.currentPage = event.page;
        this.size = event.size;
        this.loadReviews();
    }

    onReviewSortChange(sortOption: { attribute: string, direction: string }): void {
        this.attribute = sortOption.attribute;
        this.direction = sortOption.direction;
        this.page = 0;
        this.currentPage = 0;
        this.loadReviews();
    }

    onReviewCreated() {
        this.page = 0;
        this.currentPage = 0;
        this.loadReviews();
    }

    onReviewDeleted() {
        this.page = 0;
        this.currentPage = 0;
        this.loadReviews();
    }

    onEditReview(review: ReviewResponse) {
        this.manageReviewComponent.showDialog(review);
    }
}
