import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {DeliveryMethodRequest} from "../../models/delivery-method/delivery-method-request";

export interface CreateDeliveryMethod$Params {
    requestBody: DeliveryMethodRequest;
}

export function createDeliveryMethod(http: HttpClient, rootUrl: string, params: CreateDeliveryMethod$Params, context?: HttpContext): Observable<StrictHttpResponse<number>> {
    const rb = new RequestBuilder(rootUrl, createDeliveryMethod.PATH, 'post');

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

createDeliveryMethod.PATH = '/api/v1/delivery-methods';
