import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {ItemCartRequest} from "../../models/shopping-cart/item-cart-request";

export interface AddItemToCart$Params {
    requestParam: ItemCartRequest;
}

export function addItemToCart(http: HttpClient, rootUrl: string, userId: number, params: AddItemToCart$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    const rb = new RequestBuilder(rootUrl, addItemToCart.PATH.replace('{userId}', userId.toString()), 'post');

    if (params.requestParam) {
        rb.query('productId', params.requestParam.productId);
        rb.query('quantity', params.requestParam.quantity);
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

addItemToCart.PATH = '/api/v1/carts/{userId}/add';
