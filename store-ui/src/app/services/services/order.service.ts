import { Injectable } from '@angular/core';
import { HttpClient, HttpContext } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';
import {OrderResponse} from "../models/order/order-response";
import {OrderRequest} from "../models/order/order-request";
import {getOrderById} from "../fn/order/get-order-by-id";
import {getAllOrders} from "../fn/order/get-all-orders";
import {createOrder} from "../fn/order/create-order";
import {deleteOrderById} from "../fn/order/delete-order-by-id";
import {OrderPageResponse} from "../models/order/order-page-response";
import {PageParams} from "../models/page-params";
import {updateOrderStatus} from "../fn/order/update-order-status";
import {getOrdersByUserId} from "../fn/order/get-orders-by-user-id";
import {OrderOverviewResponse} from "../models/order/order-overview-response";

@Injectable({ providedIn: 'root' })
export class OrderService extends BaseService {

    constructor(config: ApiConfiguration, http: HttpClient) {
        super(config, http);
    }

    getOrderById(id: number, context?: HttpContext): Observable<OrderResponse> {
        return getOrderById(this.http, this.rootUrl, id, context).pipe(
            map((r: StrictHttpResponse<OrderResponse>) => r.body!)
        );
    }

    getAllOrders(params?: PageParams, context?: HttpContext): Observable<OrderPageResponse> {
        return getAllOrders(this.http, this.rootUrl, params, context).pipe(
            map((r: StrictHttpResponse<OrderPageResponse>) => r.body!)
        );
    }

    getOrdersByUserId(userId: number, context?: HttpContext): Observable<Array<OrderOverviewResponse>> {
        return getOrdersByUserId(this.http, this.rootUrl, userId, context).pipe(
            map((r: StrictHttpResponse<Array<OrderOverviewResponse>>) => r.body!)
        );
    }

    createOrder(request: OrderRequest, context?: HttpContext): Observable<number> {
        return createOrder(this.http, this.rootUrl, { requestBody: request }, context).pipe(
            map((r: StrictHttpResponse<number>) => r.body!)
        );
    }

    deleteOrderById(id: number, context?: HttpContext): Observable<void> {
        return deleteOrderById(this.http, this.rootUrl, id, context).pipe(
            map((r: StrictHttpResponse<void>) => r.body!)
        );
    }

    updateOrderStatus(id: number, request: string, context?: HttpContext): Observable<void> {
        return updateOrderStatus(this.http, this.rootUrl, id, request, context).pipe(
            map((r: StrictHttpResponse<void>) => r.body!)
        );
    }
}
