import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {VariantResponse} from "../../models/variant/variant-response";

export function getVariantById(http: HttpClient, rootUrl: string, id: number, context?: HttpContext): Observable<StrictHttpResponse<VariantResponse>> {
    const rb = new RequestBuilder(rootUrl, getVariantById.PATH.replace('{id}', id.toString()), 'get');

    return http.request(
        rb.build({ responseType: 'json', accept: 'application/json', context })
    ).pipe(
        filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
        map((r: HttpResponse<any>) => {
            return r as StrictHttpResponse<VariantResponse>;
        })
    );
}

getVariantById.PATH = '/api/v1/variants/{id}';
