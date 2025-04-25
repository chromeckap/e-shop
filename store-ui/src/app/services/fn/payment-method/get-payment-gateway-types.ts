import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {PaymentGatewayType} from "../../models/payment-method/payment-gateway-type";

export function getPaymentGatewayTypes(http: HttpClient, rootUrl: string, context?: HttpContext): Observable<StrictHttpResponse<PaymentGatewayType[]>> {
    const rb = new RequestBuilder(rootUrl, getPaymentGatewayTypes.PATH, 'get');

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
            return r as StrictHttpResponse<PaymentGatewayType[]>;
        })
    );
}

getPaymentGatewayTypes.PATH = '/api/v1/payment-methods/types';
