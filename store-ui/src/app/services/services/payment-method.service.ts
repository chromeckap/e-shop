import { Injectable } from '@angular/core';
import { HttpClient, HttpContext } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';
import {getPaymentMethodById} from "../fn/payment-method/get-payment-method-by-id";
import {PaymentMethodResponse} from "../models/payment-method/payment-method-response";
import {getAllPaymentMethods} from "../fn/payment-method/get-all-payment-methods";
import {getActivePaymentMethods} from "../fn/payment-method/get-active-payment-methods";
import {getPaymentGatewayTypes} from "../fn/payment-method/get-payment-gateway-types";
import {PaymentGatewayType} from "../models/payment-method/payment-gateway-type";
import {createPaymentMethod} from "../fn/payment-method/create-payment-method";
import {updatePaymentMethod} from "../fn/payment-method/update-payment-method";
import {PaymentMethodRequest} from "../models/payment-method/payment-method-request";
import {deletePaymentMethodById} from "../fn/payment-method/delete-payment-method-by-id";

@Injectable({ providedIn: 'root' })
export class PaymentMethodService extends BaseService {

    constructor(config: ApiConfiguration, http: HttpClient) {
        super(config, http);
    }

    getPaymentMethodById(id: number, context?: HttpContext): Observable<PaymentMethodResponse> {
        return getPaymentMethodById(this.http, this.rootUrl, id, context).pipe(
            map((r: StrictHttpResponse<PaymentMethodResponse>) => r.body!)
        );
    }

    getAllPaymentMethods(context?: HttpContext): Observable<Array<PaymentMethodResponse>> {
        return getAllPaymentMethods(this.http, this.rootUrl, context).pipe(
            map((r: StrictHttpResponse<Array<PaymentMethodResponse>>) => r.body!)
        );
    }

    getActivePaymentMethods(context?: HttpContext): Observable<Array<PaymentMethodResponse>> {
        return getActivePaymentMethods(this.http, this.rootUrl, context).pipe(
            map((r: StrictHttpResponse<Array<PaymentMethodResponse>>) => r.body!)
        );
    }

    getPaymentGatewayTypes(context?: HttpContext): Observable<PaymentGatewayType[]> {
        return getPaymentGatewayTypes(this.http, this.rootUrl, context).pipe(
            map((r: StrictHttpResponse<PaymentGatewayType[]>) => r.body!)
        );
    }

    createPaymentMethod(request: PaymentMethodRequest, context?: HttpContext): Observable<number> {
        return createPaymentMethod(this.http, this.rootUrl, { requestBody: request }, context).pipe(
            map((r: StrictHttpResponse<number>) => r.body!)
        );
    }

    updatePaymentMethod(id: number, request: PaymentMethodRequest, context?: HttpContext): Observable<number> {
        return updatePaymentMethod(this.http, this.rootUrl, id, { requestBody: request }, context).pipe(
            map((r: StrictHttpResponse<number>) => r.body!)
        );
    }

    deletePaymentMethodById(id: number, context?: HttpContext): Observable<void> {
        return deletePaymentMethodById(this.http, this.rootUrl, id, context).pipe(
            map((r: StrictHttpResponse<void>) => r.body!)
        );
    }
}
