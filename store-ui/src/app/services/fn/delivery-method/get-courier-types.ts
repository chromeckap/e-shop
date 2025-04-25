import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {CourierType} from "../../models/delivery-method/courier-type";

export function getCourierTypes(http: HttpClient, rootUrl: string, context?: HttpContext): Observable<StrictHttpResponse<CourierType[]>> {
    const rb = new RequestBuilder(rootUrl, getCourierTypes.PATH, 'get');

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
            return r as StrictHttpResponse<CourierType[]>;
        })
    );
}

getCourierTypes.PATH = '/api/v1/delivery-methods/couriers';
