import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

export function getCourierWidgetUrl(http: HttpClient, rootUrl: string, courierType: string, context?: HttpContext): Observable<StrictHttpResponse<string>> {
    const rb = new RequestBuilder(rootUrl, getCourierWidgetUrl.PATH.replace('{courierType}', courierType.toString()), 'get');

    return http.request(
        rb.build({ responseType: 'text', accept: 'text/plain', context })
    ).pipe(
        filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
        map((r: HttpResponse<any>) => {
            return r as StrictHttpResponse<string>;
        })
    );
}

getCourierWidgetUrl.PATH = '/api/v1/delivery-methods/widget/{courierType}';
