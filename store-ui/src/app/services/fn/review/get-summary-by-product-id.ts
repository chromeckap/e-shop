import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {ProductRatingSummary} from "../../models/review/product-rating-summary";

export function getSummaryByProductId(http: HttpClient, rootUrl: string, productId: number, context?: HttpContext): Observable<StrictHttpResponse<ProductRatingSummary>> {
    const rb = new RequestBuilder(rootUrl, getSummaryByProductId.PATH.replace('{productId}', productId.toString()), 'get');

    return http.request(
        rb.build({ responseType: 'json', accept: 'application/json', context })
    ).pipe(
        filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
        map((r: HttpResponse<any>) => {
            return r as StrictHttpResponse<ProductRatingSummary>;
        })
    );
}

getSummaryByProductId.PATH = '/api/v1/reviews/summary/{productId}';
