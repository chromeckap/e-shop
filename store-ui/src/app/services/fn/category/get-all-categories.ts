import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {CategoryOverviewResponse} from "../../models/category/category-overview-response";

export function getAllCategories(http: HttpClient, rootUrl: string, context?: HttpContext): Observable<StrictHttpResponse<Array<CategoryOverviewResponse>>> {
    const rb = new RequestBuilder(rootUrl, getAllCategories.PATH, 'get');

    return http.request(
        rb.build({
            responseType: 'json',
            accept: 'application/json',
            context,
            withCredentials: true
        })
    ).pipe(
        filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
        map((r: HttpResponse<any>) => {
            return r as StrictHttpResponse<Array<CategoryOverviewResponse>>;
        })
    );
}

getAllCategories.PATH = '/api/v1/categories';
