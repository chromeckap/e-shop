import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';

export function logout(http: HttpClient, rootUrl: string, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    const rb = new RequestBuilder(rootUrl, logout.PATH, 'post');

    return http.request(
        rb.build({ responseType: 'json', accept: 'application/json', context })
    ).pipe(
        filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
        map((r: HttpResponse<any>) => {
            return r as StrictHttpResponse<void>;
        })
    );
}

logout.PATH = '/api/v1/auth/logout';
