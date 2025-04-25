import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {ProductRequest} from "../../models/product/product-request";

export interface UpdateProduct$Params {
    requestBody: ProductRequest;
}

export function updateProduct(http: HttpClient, rootUrl: string, id: number, params: UpdateProduct$Params, context?: HttpContext): Observable<StrictHttpResponse<number>> {
    const rb = new RequestBuilder(rootUrl, updateProduct.PATH.replace('{id}', id.toString()), 'put');

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

updateProduct.PATH = '/api/v1/products/{id}';
