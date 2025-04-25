import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import { AttributeResponse } from '../../models/attribute/attribute-response';

export function getAttributeById(http: HttpClient, rootUrl: string, id: number, context?: HttpContext): Observable<StrictHttpResponse<AttributeResponse>> {
    const rb = new RequestBuilder(rootUrl, getAttributeById.PATH.replace('{id}', id.toString()), 'get');

    return http.request(
        rb.build({ responseType: 'json', accept: 'application/json', context })
    ).pipe(
        filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
        map((r: HttpResponse<any>) => {
            return r as StrictHttpResponse<AttributeResponse>;
        })
    );
}

getAttributeById.PATH = '/api/v1/attributes/{id}';
