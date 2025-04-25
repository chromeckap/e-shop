import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {ReviewPageResponse} from "../../models/review/review-page-response";
import {PageParams} from "../../models/page-params";

export function getReviewsByProductId(http: HttpClient, rootUrl: string, productId: number, pageParams?: PageParams, context?: HttpContext): Observable<StrictHttpResponse<ReviewPageResponse>> {
    const rb = new RequestBuilder(rootUrl, getReviewsByProductId.PATH.replace('{productId}', productId.toString()), 'get');

    if (pageParams) {
        rb.query('pageNumber', pageParams.pageNumber, {});
        rb.query('pageSize', pageParams.pageSize, {});
        rb.query('direction', pageParams.direction, {});
        rb.query('attribute', pageParams.attribute, {});
    }

    return http.request(
        rb.build({ responseType: 'json', accept: 'application/json', context })
    ).pipe(
        filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
        map((r: HttpResponse<any>) => {
            return r as StrictHttpResponse<ReviewPageResponse>;
        })
    );
}

getReviewsByProductId.PATH = '/api/v1/reviews/product/{productId}';
