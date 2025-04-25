import { Injectable } from '@angular/core';
import { HttpClient, HttpContext } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';
import {getPaymentByOrderId} from "../fn/payment/get-payment-by-order-id";
import {PaymentResponse} from "../models/payment/payment-response";

@Injectable({ providedIn: 'root' })
export class PaymentService extends BaseService {

    constructor(config: ApiConfiguration, http: HttpClient) {
        super(config, http);
    }

    getPaymentByOrderId(id: number, context?: HttpContext): Observable<PaymentResponse> {
        return getPaymentByOrderId(this.http, this.rootUrl, id, context).pipe(
            map((r: StrictHttpResponse<PaymentResponse>) => r.body!)
        );
    }

}
