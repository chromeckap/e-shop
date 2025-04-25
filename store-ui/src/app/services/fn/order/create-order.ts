import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {OrderRequest} from "../../models/order/order-request";

export interface CreateOrder$Params {
    requestBody: OrderRequest;
}

export function createOrder(http: HttpClient, rootUrl: string, params: CreateOrder$Params, context?: HttpContext): Observable<StrictHttpResponse<number>> {
    const rb = new RequestBuilder(rootUrl, createOrder.PATH, 'post');

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

createOrder.PATH = '/api/v1/orders';
