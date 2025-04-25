import { Injectable } from '@angular/core';
import { HttpClient, HttpContext } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';
import {getDeliveryMethodById} from "../fn/delivery-method/get-delivery-method-by-id";
import {DeliveryMethodResponse} from "../models/delivery-method/delivery-method-response";
import {getAllDeliveryMethods} from "../fn/delivery-method/get-all-delivery-methods";
import {getActiveDeliveryMethods} from "../fn/delivery-method/get-active-delivery-methods";
import {getCourierTypes} from "../fn/delivery-method/get-courier-types";
import {CourierType} from "../models/delivery-method/courier-type";
import {createDeliveryMethod} from "../fn/delivery-method/create-delivery-method";
import {DeliveryMethodRequest} from "../models/delivery-method/delivery-method-request";
import {updateDeliveryMethod} from "../fn/delivery-method/update-delivery-method";
import {deleteDeliveryMethodById} from "../fn/delivery-method/delete-delivery-method-by-id";
import {getCourierWidgetUrl} from "../fn/delivery-method/get-courier-widget-url";

@Injectable({ providedIn: 'root' })
export class DeliveryMethodService extends BaseService {

    constructor(config: ApiConfiguration, http: HttpClient) {
        super(config, http);
    }

    getDeliveryMethodById(id: number, context?: HttpContext): Observable<DeliveryMethodResponse> {
        return getDeliveryMethodById(this.http, this.rootUrl, id, context).pipe(
            map((r: StrictHttpResponse<DeliveryMethodResponse>) => r.body!)
        );
    }

    getAllDeliveryMethods(context?: HttpContext): Observable<Array<DeliveryMethodResponse>> {
        return getAllDeliveryMethods(this.http, this.rootUrl, context).pipe(
            map((r: StrictHttpResponse<Array<DeliveryMethodResponse>>) => r.body!)
        );
    }

    getActiveDeliveryMethods(context?: HttpContext): Observable<Array<DeliveryMethodResponse>> {
        return getActiveDeliveryMethods(this.http, this.rootUrl, context).pipe(
            map((r: StrictHttpResponse<Array<DeliveryMethodResponse>>) => r.body!)
        );
    }

    getCourierWidgetUrl(courierType: string, context?: HttpContext): Observable<string> {
        return getCourierWidgetUrl(this.http, this.rootUrl, courierType, context).pipe(
            map((r: StrictHttpResponse<string>) => r.body!)
        );
    }

    getCourierTypes(context?: HttpContext): Observable<CourierType[]> {
        return getCourierTypes(this.http, this.rootUrl, context).pipe(
            map((r: StrictHttpResponse<CourierType[]>) => r.body!)
        );
    }

    createDeliveryMethod(request: DeliveryMethodRequest, context?: HttpContext): Observable<number> {
        return createDeliveryMethod(this.http, this.rootUrl, { requestBody: request }, context).pipe(
            map((r: StrictHttpResponse<number>) => r.body!)
        );
    }

    updateDeliveryMethod(id: number, request: DeliveryMethodRequest, context?: HttpContext): Observable<number> {
        return updateDeliveryMethod(this.http, this.rootUrl, id, { requestBody: request }, context).pipe(
            map((r: StrictHttpResponse<number>) => r.body!)
        );
    }

    deleteDeliveryMethodById(id: number, context?: HttpContext): Observable<void> {
        return deleteDeliveryMethodById(this.http, this.rootUrl, id, context).pipe(
            map((r: StrictHttpResponse<void>) => r.body!)
        );
    }
}
