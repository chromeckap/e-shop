import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {CategoryRequest} from "../../models/category/category-request";

export interface UpdateCategory$Params {
    requestBody: CategoryRequest;
}

export function updateCategory(http: HttpClient, rootUrl: string, id: number, params: UpdateCategory$Params, context?: HttpContext): Observable<StrictHttpResponse<number>> {
    const rb = new RequestBuilder(rootUrl, updateCategory.PATH.replace('{id}', id.toString()), 'put');

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

updateCategory.PATH = '/api/v1/categories/{id}';
