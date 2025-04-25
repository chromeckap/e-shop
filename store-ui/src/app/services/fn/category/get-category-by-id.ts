import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {CategoryResponse} from "../../models/category/category-response";

export function getCategoryById(http: HttpClient, rootUrl: string, id: number, context?: HttpContext): Observable<StrictHttpResponse<CategoryResponse>> {
    const rb = new RequestBuilder(rootUrl, getCategoryById.PATH.replace('{id}', id.toString()), 'get');

    return http.request(
        rb.build({ responseType: 'json', accept: 'application/json', context })
    ).pipe(
        filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
        map((r: HttpResponse<any>) => {
            return r as StrictHttpResponse<CategoryResponse>;
        })
    );
}

getCategoryById.PATH = '/api/v1/categories/{id}';
