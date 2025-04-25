import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {ProductRequest} from "../../models/product/product-request";

export interface CreateProduct$Params {
    requestBody: ProductRequest;
}

export function createProduct(http: HttpClient, rootUrl: string, params: CreateProduct$Params, context?: HttpContext): Observable<StrictHttpResponse<number>> {
    const rb = new RequestBuilder(rootUrl, createProduct.PATH, 'post');

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

createProduct.PATH = '/api/v1/products';
