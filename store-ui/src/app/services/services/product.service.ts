import { Injectable } from '@angular/core';
import { HttpClient, HttpContext } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';
import {ProductResponse} from "../models/product/product-response";
import {getProductById} from "../fn/product/get-product-by-id";
import {getAllProducts} from "../fn/product/get-all-products";
import {ProductRequest} from "../models/product/product-request";
import {createProduct} from "../fn/product/create-product";
import {updateProduct} from "../fn/product/update-product";
import {deleteProductById} from "../fn/product/delete-product-by-id";
import {getProductsByCategory} from "../fn/product/get-products-by-category";
import {searchProductsByQuery} from "../fn/product/search-products-by-query";
import {ProductPageResponse} from "../models/product/product-page-response";
import {uploadProductImages} from "../fn/product/upload-product-images";
import {ImageFile} from "../models/picture/ImageFile";
import {getImage} from "../fn/product/get-image";
import {PageParams} from "../models/page-params";
import {getFilterRangesByCategory} from "../fn/product/get-filter-ranges-by-category";
import {FilterRangesResponse} from "../models/product/filter-ranges-response";
import {ProductSpecificationRequest} from "../models/product/product-specification-request";

@Injectable({ providedIn: 'root' })
export class ProductService extends BaseService {

    constructor(config: ApiConfiguration, http: HttpClient) {
        super(config, http);
    }

    getProductById(id: number, context?: HttpContext): Observable<ProductResponse> {
        return getProductById(this.http, this.rootUrl, id, context).pipe(
            map((r: StrictHttpResponse<ProductResponse>) => r.body!)
        );
    }

    getAllProducts(params?: PageParams, context?: HttpContext): Observable<ProductPageResponse> {
        return getAllProducts(this.http, this.rootUrl, params, context).pipe(
            map((r: StrictHttpResponse<ProductPageResponse>) => r.body!)
        );
    }

    getProductsByCategory(
        categoryId: number,
        pageParams?: PageParams,
        filters?: ProductSpecificationRequest,
        context?: HttpContext
    ): Observable<ProductPageResponse> {
        return getProductsByCategory(this.http, this.rootUrl, categoryId, filters, pageParams, context).pipe(
            map((r: StrictHttpResponse<ProductPageResponse>) => r.body!)
        );
    }

    getFilterRangesByCategory(id: number, context?: HttpContext): Observable<FilterRangesResponse> {
        return getFilterRangesByCategory(this.http, this.rootUrl, id, context).pipe(
            map((r: StrictHttpResponse<FilterRangesResponse>) => r.body!)
        );
    }

    searchProductsByQuery(
        query: string,
        pageNumber: number = 0,
        pageSize: number = 10,
        context?: HttpContext
    ): Observable<ProductPageResponse> {
        return searchProductsByQuery(this.http, this.rootUrl, query, pageNumber, pageSize, context).pipe(
            map((r: StrictHttpResponse<ProductPageResponse>) => r.body!)
        );
    }

    createProduct(request: ProductRequest, context?: HttpContext): Observable<number> {
        return createProduct(this.http, this.rootUrl, { requestBody: request }, context).pipe(
            map((r: StrictHttpResponse<number>) => r.body!)
        );
    }

    updateProduct(id: number, request: ProductRequest, context?: HttpContext): Observable<number> {
        return updateProduct(this.http, this.rootUrl, id, { requestBody: request }, context).pipe(
            map((r: StrictHttpResponse<number>) => r.body!)
        );
    }

    deleteProductById(id: number, context?: HttpContext): Observable<void> {
        return deleteProductById(this.http, this.rootUrl, id, context).pipe(
            map((r: StrictHttpResponse<void>) => r.body!)
        );
    }

    uploadProductImages(id: number, files: ImageFile[], context?: HttpContext): Observable<void> {
        return uploadProductImages(this.http, this.rootUrl, id, files, context).pipe(
            map((r: StrictHttpResponse<void>) => r.body!)
        );
    }

    getImage(id: number, fileName: string): string {
        return getImage(this.imageUrl, id, fileName)
    }

}
