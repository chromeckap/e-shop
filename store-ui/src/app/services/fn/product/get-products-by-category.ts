import {HttpClient, HttpContext, HttpResponse} from '@angular/common/http';
import { Observable } from 'rxjs';
import {filter, map} from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import {ProductPageResponse} from "../../models/product/product-page-response";
import {PageParams} from "../../models/page-params";
import {ProductSpecificationRequest} from "../../models/product/product-specification-request";

export function getProductsByCategory(http: HttpClient, rootUrl: string,
    categoryId: number,
    specifications?: ProductSpecificationRequest,
    pageParams?: PageParams,
    context?: HttpContext): Observable<StrictHttpResponse<ProductPageResponse>> {

    const rb = new RequestBuilder(rootUrl, getProductsByCategory.PATH.replace('{id}', categoryId.toString()), 'get');

    if (pageParams) {
        rb.query('pageNumber', pageParams.pageNumber, {});
        rb.query('pageSize', pageParams.pageSize, {});
        rb.query('direction', pageParams.direction, {});
        rb.query('attribute', pageParams.attribute, {});
    }

    if (specifications) {
        rb.query('lowPrice', specifications.lowPrice, {});
        rb.query('maxPrice', specifications.maxPrice, {});
        rb.query('attributeValueIds', specifications.attributeValueIds, {});
    }

    return http.request(rb.build({ responseType: 'json', accept: 'application/json', context }))
        .pipe(
            filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
            map((r: HttpResponse<any>) => r as StrictHttpResponse<ProductPageResponse>)
        );
}

getProductsByCategory.PATH = '/api/v1/products/category/{id}';
