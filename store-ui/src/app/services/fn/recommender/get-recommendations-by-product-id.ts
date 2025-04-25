import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {ProductOverviewResponse} from "../../models/product/product-overview-response";
import {ProductResponse} from "../../models/product/product-response";

export function getRecommendationsByProductId(http: HttpClient, rootUrl: string, productId: number, limit?: number, context?: HttpContext): Observable<StrictHttpResponse<Array<ProductResponse>>> {
    const rb = new RequestBuilder(rootUrl, getRecommendationsByProductId.PATH.replace('{productId}', productId.toString()), 'get');

    if (limit) {
        rb.query('limit', limit, {});
    }

    return http.request(
        rb.build({ responseType: 'json', accept: 'application/json', context })
    ).pipe(
        filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
        map((r: HttpResponse<any>) => {
            return r as StrictHttpResponse<Array<ProductOverviewResponse>>;
        })
    );
}

getRecommendationsByProductId.PATH = '/api/v1/recommendations/{productId}';
