import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {DeliveryMethodResponse} from "../../models/delivery-method/delivery-method-response";

export function getActiveDeliveryMethods(http: HttpClient, rootUrl: string, context?: HttpContext): Observable<StrictHttpResponse<Array<DeliveryMethodResponse>>> {
    const rb = new RequestBuilder(rootUrl, getActiveDeliveryMethods.PATH, 'get');

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
            return r as StrictHttpResponse<Array<DeliveryMethodResponse>>;
        })
    );
}

getActiveDeliveryMethods.PATH = '/api/v1/delivery-methods/active';
