import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {PageParams} from "../../models/page-params";
import {UserPageResponse} from "../../models/user/user-page-response";

export function getAllUsers(http: HttpClient, rootUrl: string, params?: PageParams, context?: HttpContext): Observable<StrictHttpResponse<UserPageResponse>> {
    const rb = new RequestBuilder(rootUrl, getAllUsers.PATH, 'get');

    if (params) {
        rb.query('pageNumber', params.pageNumber, {});
        rb.query('pageSize', params.pageSize, {});
        rb.query('direction', params.direction, {});
        rb.query('attribute', params.attribute, {});
    }

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
            return r as StrictHttpResponse<UserPageResponse>;
        })
    );
}

getAllUsers.PATH = '/api/v1/users';
