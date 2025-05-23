import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {VariantResponse} from "../../models/variant/variant-response";

export function getVariantsByProductId(http: HttpClient, rootUrl: string, productId: number, context?: HttpContext): Observable<StrictHttpResponse<Array<VariantResponse>>> {
    const rb = new RequestBuilder(rootUrl, getVariantsByProductId.PATH.replace('{id}', productId.toString()), 'get');

    return http.request(
        rb.build({ responseType: 'json', accept: 'application/json', context })
    ).pipe(
        filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
        map((r: HttpResponse<any>) => {
            return r as StrictHttpResponse<Array<VariantResponse>>;
        })
    );
}

getVariantsByProductId.PATH = '/api/v1/variants/product/{id}';
