import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {FilterRangesResponse} from "../../models/product/filter-ranges-response";

export function getFilterRangesByCategory(http: HttpClient, rootUrl: string, id: number, context?: HttpContext): Observable<StrictHttpResponse<FilterRangesResponse>> {
    const rb = new RequestBuilder(rootUrl, getFilterRangesByCategory.PATH.replace('{id}', id.toString()), 'get');

    return http.request(
        rb.build({ responseType: 'json', accept: 'application/json', context })
    ).pipe(
        filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
        map((r: HttpResponse<any>) => {
            return r as StrictHttpResponse<FilterRangesResponse>;
        })
    );
}

getFilterRangesByCategory.PATH = '/api/v1/products/category/{id}/ranges';
