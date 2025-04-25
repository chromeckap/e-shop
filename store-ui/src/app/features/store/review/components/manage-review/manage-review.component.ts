import {Component, EventEmitter, Output} from '@angular/core';
import {ReviewService} from "../../../../../services/services/review.service";
import {AuthService} from "../../../../../services/services/auth.service";
import {Button} from "primeng/button";
import {ActivatedRoute, RouterLink} from "@angular/router";
import {Dialog} from "primeng/dialog";
import {Rating} from "primeng/rating";
import {FloatLabel} from "primeng/floatlabel";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Textarea} from "primeng/textarea";
import {ToastService} from "../../../../../shared/services/toast.service";
import {ReviewRequest} from "../../../../../services/models/review/review-request";

@Component({
    selector: 'app-manage-review',
    imports: [
        Button,
        RouterLink,
        Dialog,
        Rating,
        FloatLabel,
        FormsModule,
        ReactiveFormsModule,
        Textarea,
    ],
    templateUrl: './manage-review.component.html',
    standalone: true,
    styleUrl: './manage-review.component.scss'
})
export class ManageReviewComponent {
    @Output() reviewCreated = new EventEmitter<void>();
    @Output() reviewUpdated = new EventEmitter<void>();

    review: ReviewRequest = {};
    dialogVisible: boolean = false;

    constructor(
        private reviewService: ReviewService,
        private authService: AuthService,
        private toastService: ToastService,
        private activatedRoute: ActivatedRoute
    ) {}


    get isLoggedIn() {
        return this.authService.isLoggedIn;
    }

    showDialog(review?: ReviewRequest) {
        if (review) {
            this.review = { ...review };
        } else {
            this.review = { rating: undefined, text: '' };
        }
        this.dialogVisible = true;
    }

    saveReview() {
        if (this.review.id) {
            this.reviewService.updateReview(this.review.id, this.review).subscribe({
                next: async () => {
                    this.reviewUpdated.emit();
                    await this.toastService.showSuccessToast('Úspěch', 'Recenze byla úspěšně aktualizována.');
                },
                error: async (error) => {
                    await this.toastService.showErrorToast('Chyba', error.error.detail);
                }
            });
        } else {
            this.review.userId = this.authService.getCurrentUser?.id;
            this.review.productId = this.activatedRoute.snapshot.params['id'];
            this.reviewService.createReview(this.review).subscribe({
                next: async () => {
                    this.reviewCreated.emit();
                    await this.toastService.showSuccessToast('Úspěch', 'Recenze byla úspěšně vytvořena.');
                },
                error: async (error) => {
                    await this.toastService.showErrorToast('Chyba', error.error.detail);
                }
            });
        }
        this.dialogVisible = false;
    }
}
