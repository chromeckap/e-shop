import { Injectable } from '@angular/core';
import { HttpClient, HttpContext } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';
import { AttributeRequest } from '../models/attribute/attribute-request';
import { AttributeResponse } from '../models/attribute/attribute-response';
import { getAttributeById } from "../fn/attribute/get-attribute-by-id";
import { getAllAttributes } from "../fn/attribute/get-all-attributes";
import { createAttribute } from "../fn/attribute/create-attribute";
import { updateAttribute } from "../fn/attribute/update-attribute";
import { deleteAttributeById } from "../fn/attribute/delete-attribute-by-id";

@Injectable({ providedIn: 'root' })
export class AttributeService extends BaseService {

    constructor(config: ApiConfiguration, http: HttpClient) {
        super(config, http);
    }

    getAttributeById(id: number, context?: HttpContext): Observable<AttributeResponse> {
        return getAttributeById(this.http, this.rootUrl, id, context).pipe(
            map((r: StrictHttpResponse<AttributeResponse>) => r.body!)
        );
    }

    getAllAttributes(context?: HttpContext): Observable<Array<AttributeResponse>> {
        return getAllAttributes(this.http, this.rootUrl, context).pipe(
            map((r: StrictHttpResponse<Array<AttributeResponse>>) => r.body!)
        );
    }

    createAttribute(request: AttributeRequest, context?: HttpContext): Observable<number> {
        return createAttribute(this.http, this.rootUrl, { requestBody: request }, context).pipe(
            map((r: StrictHttpResponse<number>) => r.body!)
        );
    }

    updateAttribute(id: number, request: AttributeRequest, context?: HttpContext): Observable<number> {
        return updateAttribute(this.http, this.rootUrl, id, { requestBody: request }, context).pipe(
            map((r: StrictHttpResponse<number>) => r.body!)
        );
    }

    deleteAttributeById(id: number, context?: HttpContext): Observable<void> {
        return deleteAttributeById(this.http, this.rootUrl, id, context).pipe(
            map((r: StrictHttpResponse<void>) => r.body!)
        );
    }
}
