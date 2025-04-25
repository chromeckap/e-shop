import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {DeliveryMethodResponse} from "../../models/delivery-method/delivery-method-response";

export function getDeliveryMethodById(http: HttpClient, rootUrl: string, id: number, context?: HttpContext): Observable<StrictHttpResponse<DeliveryMethodResponse>> {
    const rb = new RequestBuilder(rootUrl, getDeliveryMethodById.PATH.replace('{id}', id.toString()), 'get');

    return http.request(
        rb.build({ responseType: 'json', accept: 'application/json', context })
    ).pipe(
        filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
        map((r: HttpResponse<any>) => {
            return r as StrictHttpResponse<DeliveryMethodResponse>;
        })
    );
}

getDeliveryMethodById.PATH = '/api/v1/delivery-methods/{id}';
