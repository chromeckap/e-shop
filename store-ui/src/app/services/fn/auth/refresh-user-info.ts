import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {UserResponse} from "../../models/user/user-response";

export function refreshUserInfo(http: HttpClient, rootUrl: string, context?: HttpContext): Observable<StrictHttpResponse<UserResponse>> {
    const rb = new RequestBuilder(rootUrl, refreshUserInfo.PATH, 'get');

    return http.request(
        rb.build({ responseType: 'json', accept: 'application/json', context })
    ).pipe(
        filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
        map((r: HttpResponse<any>) => {
            return r as StrictHttpResponse<UserResponse>;
        })
    );
}

refreshUserInfo.PATH = '/api/v1/auth';
