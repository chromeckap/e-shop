import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {VariantRequest} from "../../models/variant/variant-request";

export interface UpdateVariant$Params {
    requestBody: VariantRequest;
}

export function updateVariant(http: HttpClient, rootUrl: string, id: number, params: UpdateVariant$Params, context?: HttpContext): Observable<StrictHttpResponse<number>> {
    const rb = new RequestBuilder(rootUrl, updateVariant.PATH.replace('{id}', id.toString()), 'put');

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

updateVariant.PATH = '/api/v1/variants/{id}';
