import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {PaymentMethodRequest} from "../../models/payment-method/payment-method-request";

export interface UpdatePaymentMethod$Params {
    requestBody: PaymentMethodRequest;
}

export function updatePaymentMethod(http: HttpClient, rootUrl: string, id: number, params: UpdatePaymentMethod$Params, context?: HttpContext): Observable<StrictHttpResponse<number>> {
    const rb = new RequestBuilder(rootUrl, updatePaymentMethod.PATH.replace('{id}', id.toString()), 'put');

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

updatePaymentMethod.PATH = '/api/v1/payment-methods/{id}';
