import { Injectable } from '@angular/core';
import { HttpClient, HttpContext } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';
import {CategoryResponse} from "../models/category/category-response";
import {CategoryOverviewResponse} from "../models/category/category-overview-response";
import {CategoryRequest} from "../models/category/category-request";
import {getCategoryById} from "../fn/category/get-category-by-id";
import {getAllCategories} from "../fn/category/get-all-categories";
import {createCategory} from "../fn/category/create-category";
import {updateCategory} from "../fn/category/update-category";
import {deleteCategoryById} from "../fn/category/delete-category-by-id";

@Injectable({ providedIn: 'root' })
export class CategoryService extends BaseService {

    constructor(config: ApiConfiguration, http: HttpClient) {
        super(config, http);
    }

    getCategoryById(id: number, context?: HttpContext): Observable<CategoryResponse> {
        return getCategoryById(this.http, this.rootUrl, id, context).pipe(
            map((r: StrictHttpResponse<CategoryResponse>) => r.body!)
        );
    }

    getAllCategories(context?: HttpContext): Observable<Array<CategoryOverviewResponse>> {
        return getAllCategories(this.http, this.rootUrl, context).pipe(
            map((r: StrictHttpResponse<Array<CategoryOverviewResponse>>) => r.body!)
        );
    }

    createCategory(request: CategoryRequest, context?: HttpContext): Observable<number> {
        return createCategory(this.http, this.rootUrl, { requestBody: request }, context).pipe(
            map((r: StrictHttpResponse<number>) => r.body!)
        );
    }

    updateCategory(id: number, request: CategoryRequest, context?: HttpContext): Observable<number> {
        return updateCategory(this.http, this.rootUrl, id, { requestBody: request }, context).pipe(
            map((r: StrictHttpResponse<number>) => r.body!)
        );
    }

    deleteCategoryById(id: number, context?: HttpContext): Observable<void> {
        return deleteCategoryById(this.http, this.rootUrl, id, context).pipe(
            map((r: StrictHttpResponse<void>) => r.body!)
        );
    }
}
