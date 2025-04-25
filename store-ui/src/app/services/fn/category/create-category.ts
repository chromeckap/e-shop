import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {CategoryRequest} from "../../models/category/category-request";

export interface CreateCategory$Params {
    requestBody: CategoryRequest;
}

export function createCategory(http: HttpClient, rootUrl: string, params: CreateCategory$Params, context?: HttpContext): Observable<StrictHttpResponse<number>> {
    const rb = new RequestBuilder(rootUrl, createCategory.PATH, 'post');

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

createCategory.PATH = '/api/v1/categories';
