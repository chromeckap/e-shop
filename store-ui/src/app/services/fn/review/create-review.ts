import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {ReviewRequest} from "../../models/review/review-request";

export interface ReviewRequest$Params {
    requestBody: ReviewRequest;
}

export function createReview(http: HttpClient, rootUrl: string, params: ReviewRequest$Params, context?: HttpContext): Observable<StrictHttpResponse<number>> {
    const rb = new RequestBuilder(rootUrl, createReview.PATH, 'post');

    if (params) {
        rb.body(params.requestBody, 'application/json');
    }

    return http.request(
        rb.build({ responseType: 'json', accept: 'application/json', context })
    ).pipe(
        filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
        map((r: HttpResponse<any>) => {
            return r as StrictHttpResponse<number>;
        })
    );
}

createReview.PATH = '/api/v1/reviews';
