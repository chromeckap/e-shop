import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {PaymentMethodResponse} from "../../models/payment-method/payment-method-response";

export function getPaymentMethodById(http: HttpClient, rootUrl: string, id: number, context?: HttpContext): Observable<StrictHttpResponse<PaymentMethodResponse>> {
    const rb = new RequestBuilder(rootUrl, getPaymentMethodById.PATH.replace('{id}', id.toString()), 'get');

    return http.request(
        rb.build({ responseType: 'json', accept: 'application/json', context })
    ).pipe(
        filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
        map((r: HttpResponse<any>) => {
            return r as StrictHttpResponse<PaymentMethodResponse>;
        })
    );
}

getPaymentMethodById.PATH = '/api/v1/payment-methods/{id}';
