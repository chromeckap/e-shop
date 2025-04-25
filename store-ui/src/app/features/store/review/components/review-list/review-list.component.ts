import {Component, EventEmitter, Input, Output} from '@angular/core';
import {ReviewResponse} from "../../../../../services/models/review/review-response";
import {Rating} from "primeng/rating";
import {FormsModule} from "@angular/forms";
import {DatePipe} from "@angular/common";
import {Paginator} from "primeng/paginator";
import {ReviewPageResponse} from "../../../../../services/models/review/review-page-response";
import {ReviewService} from "../../../../../services/services/review.service";
import {FloatLabel} from "primeng/floatlabel";
import {Select} from "primeng/select";
import {AuthService} from "../../../../../services/services/auth.service";
import {Button} from "primeng/button";
import {ConfirmationService} from "primeng/api";
import {ConfirmDialog} from "primeng/confirmdialog";
import {ToastService} from "../../../../../shared/services/toast.service";
import {Tag} from "primeng/tag";

@Component({
    selector: 'app-review-list',
    imports: [
        Rating,
        FormsModule,
        DatePipe,
        Paginator,
        FloatLabel,
        Select,
        Button,
        ConfirmDialog,
        Tag
    ],
    templateUrl: './review-list.component.html',
    standalone: true,
    styleUrl: './review-list.component.scss',
    providers: [ConfirmationService]
})
export class ReviewListComponent {
    @Input() reviewsPage!: ReviewPageResponse;
    @Input() currentPage = 0;
    @Output() pageChanged = new EventEmitter<{ page: number, size: number }>();
    @Output() sortChanged = new EventEmitter<{ attribute: string, direction: string }>();
    @Output() reviewDeleted = new EventEmitter<void>();
    @Output() editReviewEvent = new EventEmitter<ReviewResponse>();

    sortOptions = [
        { name: 'Od nejnovějších', attribute: 'createTime', direction: 'desc' },
        { name: 'Od nejlepších', attribute: 'rating', direction: 'desc' },
        { name: 'Od nejhorších', attribute: 'rating', direction: 'asc' },
    ];

    selectedSortOption = this.sortOptions[0];

    constructor(
        protected authService: AuthService,
        private reviewService: ReviewService,
        private confirmationService: ConfirmationService,
        private toastService: ToastService
    ) {}

    isUserReview(review: ReviewResponse) {
        return this.authService.getCurrentUser?.id === review.userId;
    }

    onPageChange(event: any) {
        this.pageChanged.emit({
            page: event.first / event.rows,
            size: event.rows
        });
    }

    onSortChange(): void {
        this.sortChanged.emit({
            attribute: this.selectedSortOption.attribute,
            direction: this.selectedSortOption.direction
        });
    }

    deleteReview(review: ReviewResponse) {
        this.confirmationService.confirm({
            header: 'Potvrzení odstranění',
            icon: 'pi pi-exclamation-triangle',
            message: 'Opravdu chceš odstranit recenzi s ID ' + review.id + "?",
            acceptLabel: 'Ano',
            acceptIcon: 'pi pi-check',
            acceptButtonStyleClass: 'p-button-danger p-button-outlined',
            rejectLabel: 'Ne',
            rejectIcon: 'pi pi-check',
            rejectButtonStyleClass: 'p-button-secondary p-button-outlined',

            accept: () => {
                this.reviewService.deleteReviewById(review.id!).subscribe({
                    next: async () => {
                        try {
                            this.reviewDeleted.emit();
                            await this.toastService.showSuccessToast('Úspěch', 'Recenze byla úspěšně odstraněna.');
                        } catch (error) {
                            console.log("Chyba při operacích po odstranění:", error);
                        }
                    },
                    error: async (error) => {
                        console.log(error);
                        try {
                            await this.toastService.showErrorToast('Chyba', error.error.detail);
                        } catch (toastError) {
                            console.log("Chyba při zobrazení toastu:", toastError);
                        }
                    }
                });
            }
        });
    }

    editReview(review: ReviewResponse) {
        this.editReviewEvent.emit(review);
        console.log('jsem v review list')
    }
}
