import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {VariantRequest} from "../../models/variant/variant-request";

export interface CreateVariant$Params {
    requestBody: VariantRequest;
}

export function createVariant(http: HttpClient, rootUrl: string, params: CreateVariant$Params, context?: HttpContext): Observable<StrictHttpResponse<number>> {
    const rb = new RequestBuilder(rootUrl, createVariant.PATH, 'post');

    if (params) {
        rb.body(params.requestBody, 'application/json');
    }

    return http.request(
        rb.build({ responseType: 'json', accept: 'application/json', context })
    ).pipe(
        filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
        map((r: HttpResponse<any>) => {
            return r as StrictHttpResponse<number>;
        })
    );
}

createVariant.PATH = '/api/v1/variants';
