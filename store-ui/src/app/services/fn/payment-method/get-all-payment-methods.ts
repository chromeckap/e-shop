import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {PaymentMethodResponse} from "../../models/payment-method/payment-method-response";

export function getAllPaymentMethods(http: HttpClient, rootUrl: string, context?: HttpContext): Observable<StrictHttpResponse<Array<PaymentMethodResponse>>> {
    const rb = new RequestBuilder(rootUrl, getAllPaymentMethods.PATH, 'get');

    return http.request(
        rb.build({
            responseType: 'json',
            accept: 'application/json',
            context,
            withCredentials: true
        })
    ).pipe(
        filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
        map((r: HttpResponse<any>) => {
            return r as StrictHttpResponse<Array<PaymentMethodResponse>>;
        })
    );
}

getAllPaymentMethods.PATH = '/api/v1/payment-methods';
