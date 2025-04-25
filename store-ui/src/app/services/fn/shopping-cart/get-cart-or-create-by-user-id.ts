import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {CartResponse} from "../../models/shopping-cart/cart-response";

export function getCartOrCreateByUserId(http: HttpClient, rootUrl: string, userId: number, context?: HttpContext): Observable<StrictHttpResponse<CartResponse>> {
    const rb = new RequestBuilder(rootUrl, getCartOrCreateByUserId.PATH.replace('{userId}', userId.toString()), 'get');

    return http.request(
        rb.build({ responseType: 'json', accept: 'application/json', context })
    ).pipe(
        filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
        map((r: HttpResponse<any>) => {
            return r as StrictHttpResponse<CartResponse>;
        })
    );
}

getCartOrCreateByUserId.PATH = '/api/v1/carts/{userId}';
