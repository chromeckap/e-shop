import {HttpClient, HttpContext, HttpResponse} from "@angular/common/http";
import {Observable} from "rxjs";
import {StrictHttpResponse} from "../../strict-http-response";
import {RequestBuilder} from "../../request-builder";
import {filter, map} from "rxjs/operators";
import {ProductPageResponse} from "../../models/product/product-page-response";

export function searchProductsByQuery(
    http: HttpClient,
    rootUrl: string,
    query: string,
    pageNumber: number = 0,
    pageSize: number = 10,
    context?: HttpContext
): Observable<StrictHttpResponse<ProductPageResponse>> {
    const rb = new RequestBuilder(rootUrl, searchProductsByQuery.PATH, 'get');

    rb.query('pageNumber', pageNumber);
    rb.query('pageSize', pageSize);
    rb.query('query', query);

    return http.request(rb.build({ responseType: 'json', accept: 'application/json', context }))
        .pipe(
            filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
            map((r: HttpResponse<any>) => r as StrictHttpResponse<ProductPageResponse>)
        );
}

searchProductsByQuery.PATH = '/api/v1/products/search';

