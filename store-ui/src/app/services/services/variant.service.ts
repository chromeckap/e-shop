import { Injectable } from '@angular/core';
import { HttpClient, HttpContext } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';
import {VariantResponse} from "../models/variant/variant-response";
import {VariantRequest} from "../models/variant/variant-request";
import {getVariantById} from "../fn/variant/get-variant-by-id";
import {createVariant} from "../fn/variant/create-variant";
import {updateVariant} from "../fn/variant/update-variant";
import {deleteVariantById} from "../fn/variant/delete-variant-by-id";
import {getVariantsByProductId} from "../fn/variant/get-variants-by-product-id";

@Injectable({ providedIn: 'root' })
export class VariantService extends BaseService {

    constructor(config: ApiConfiguration, http: HttpClient) {
        super(config, http);
    }

    getVariantById(id: number, context?: HttpContext): Observable<VariantResponse> {
        return getVariantById(this.http, this.rootUrl, id, context).pipe(
            map((r: StrictHttpResponse<VariantResponse>) => r.body!)
        );
    }

    getVariantsByProductId(productId: number, context?: HttpContext): Observable<Array<VariantResponse>> {
        return getVariantsByProductId(this.http, this.rootUrl, productId, context).pipe(
            map((r: StrictHttpResponse<Array<VariantResponse>>) => r.body!)
        );
    }

    createVariant(request: VariantRequest, context?: HttpContext): Observable<number> {
        return createVariant(this.http, this.rootUrl, { requestBody: request }, context).pipe(
            map((r: StrictHttpResponse<number>) => r.body!)
        );
    }

    updateVariant(id: number, request: VariantRequest, context?: HttpContext): Observable<number> {
        return updateVariant(this.http, this.rootUrl, id, { requestBody: request }, context).pipe(
            map((r: StrictHttpResponse<number>) => r.body!)
        );
    }

    deleteVariantById(id: number, context?: HttpContext): Observable<void> {
        return deleteVariantById(this.http, this.rootUrl, id, context).pipe(
            map((r: StrictHttpResponse<void>) => r.body!)
        );
    }
}
