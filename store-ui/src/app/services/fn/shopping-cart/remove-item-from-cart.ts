import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {ItemCartRequest} from "../../models/shopping-cart/item-cart-request";

export interface RemoveItemFromCart$Params {
    requestParam: ItemCartRequest
}

export function removeItemFromCart(http: HttpClient, rootUrl: string, userId: number, params: RemoveItemFromCart$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    const rb = new RequestBuilder(rootUrl, removeItemFromCart.PATH.replace('{userId}', userId.toString()), 'delete');

    if (params.requestParam) {
        rb.query('productId', params.requestParam.productId);
    }

    return http.request(
        rb.build({ responseType: 'json', accept: 'application/json', context })
    ).pipe(
        filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
        map((r: HttpResponse<any>) => {
            return r as StrictHttpResponse<void>;
        })
    );
}

removeItemFromCart.PATH = '/api/v1/carts/{userId}/remove';
