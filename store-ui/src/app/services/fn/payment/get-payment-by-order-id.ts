import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {PaymentResponse} from "../../models/payment/payment-response";

export function getPaymentByOrderId(http: HttpClient, rootUrl: string, id: number, context?: HttpContext): Observable<StrictHttpResponse<PaymentResponse>> {
    const rb = new RequestBuilder(rootUrl, getPaymentByOrderId.PATH.replace('{id}', id.toString()), 'get');

    return http.request(
        rb.build({ responseType: 'json', accept: 'application/json', context })
    ).pipe(
        filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
        map((r: HttpResponse<any>) => {
            return r as StrictHttpResponse<PaymentResponse>;
        })
    );
}

getPaymentByOrderId.PATH = '/api/v1/payments/order/{id}';
