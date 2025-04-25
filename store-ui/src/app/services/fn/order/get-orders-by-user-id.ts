import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {OrderOverviewResponse} from "../../models/order/order-overview-response";

export function getOrdersByUserId(http: HttpClient, rootUrl: string, userId: number, context?: HttpContext): Observable<StrictHttpResponse<Array<OrderOverviewResponse>>> {
    const rb = new RequestBuilder(rootUrl, getOrdersByUserId.PATH.replace('{userId}', userId.toString()), 'get');

    return http.request(
        rb.build({ responseType: 'json', accept: 'application/json', context })
    ).pipe(
        filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
        map((r: HttpResponse<any>) => {
            return r as StrictHttpResponse<Array<OrderOverviewResponse>>;
        })
    );
}

getOrdersByUserId.PATH = '/api/v1/orders/user/{userId}';
