import { Injectable } from '@angular/core';
import { HttpClient, HttpContext } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';
import {ProductOverviewResponse} from "../models/product/product-overview-response";
import {getRecommendationsByProductId} from "../fn/recommender/get-recommendations-by-product-id";

@Injectable({ providedIn: 'root' })
export class RecommendationService extends BaseService {

    constructor(config: ApiConfiguration, http: HttpClient) {
        super(config, http);
    }

    getRecommendationsByProductId(id: number, limit?: number, context?: HttpContext): Observable<Array<ProductOverviewResponse>> {
        return getRecommendationsByProductId(this.http, this.rootUrl, id, limit, context).pipe(
            map((r: StrictHttpResponse<Array<ProductOverviewResponse>>) => r.body!)
        );
    }

}
