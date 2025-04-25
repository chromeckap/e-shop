import { Injectable } from '@angular/core';
import { HttpClient, HttpContext } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';
import {getSummaryByProductId} from "../fn/review/get-summary-by-product-id";
import {ProductRatingSummary} from "../models/review/product-rating-summary";
import {getReviewsByProductId} from "../fn/review/get-reviews-by-product-id";
import {createReview} from "../fn/review/create-review";
import {updateReview} from "../fn/review/update-review";
import {deleteReviewById} from "../fn/review/delete-review-by-id";
import {ReviewRequest} from "../models/review/review-request";
import {ReviewPageResponse} from "../models/review/review-page-response";
import {PageParams} from "../models/page-params";

@Injectable({ providedIn: 'root' })
export class ReviewService extends BaseService {

    constructor(config: ApiConfiguration, http: HttpClient) {
        super(config, http);
    }

    getSummaryByProductId(id: number, context?: HttpContext): Observable<ProductRatingSummary> {
        return getSummaryByProductId(this.http, this.rootUrl, id, context).pipe(
            map((r: StrictHttpResponse<ProductRatingSummary>) => r.body!)
        );
    }

    getReviewsByProductId(id: number, pageParams?: PageParams, context?: HttpContext): Observable<ReviewPageResponse> {
        return getReviewsByProductId(this.http, this.rootUrl, id, pageParams, context).pipe(
            map((r: StrictHttpResponse<ReviewPageResponse>) => r.body!)
        );
    }

    createReview(request: ReviewRequest, context?: HttpContext): Observable<number> {
        return createReview(this.http, this.rootUrl, { requestBody: request }, context).pipe(
            map((r: StrictHttpResponse<number>) => r.body!)
        );
    }

    updateReview(id: number, request: ReviewRequest, context?: HttpContext): Observable<number> {
        return updateReview(this.http, this.rootUrl, id, { requestBody: request }, context).pipe(
            map((r: StrictHttpResponse<number>) => r.body!)
        );
    }

    deleteReviewById(id: number, context?: HttpContext): Observable<void> {
        return deleteReviewById(this.http, this.rootUrl, id, context).pipe(
            map((r: StrictHttpResponse<void>) => r.body!)
        );
    }
}
