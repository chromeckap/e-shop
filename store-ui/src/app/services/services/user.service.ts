import { Injectable } from '@angular/core';
import { HttpClient, HttpContext } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { BaseService } from '../base-service';
import { ApiConfiguration } from '../api-configuration';
import { StrictHttpResponse } from '../strict-http-response';
import {getAllUsers} from "../fn/user/get-all-users";
import {deleteUserById} from "../fn/user/delete-user-by-id";
import {PageParams} from "../models/page-params";
import {UserPageResponse} from "../models/user/user-page-response";
import {ProductRequest} from "../models/product/product-request";
import {updateUserRole} from "../fn/user/update-user-role";

@Injectable({ providedIn: 'root' })
export class UserService extends BaseService {

    constructor(config: ApiConfiguration, http: HttpClient) {
        super(config, http);
    }

    getAllUsers(params?: PageParams, context?: HttpContext): Observable<UserPageResponse> {
        return getAllUsers(this.http, this.rootUrl, params, context).pipe(
            map((r: StrictHttpResponse<UserPageResponse>) => r.body!)
        );
    }

    deleteUserById(id: number, context?: HttpContext): Observable<void> {
        return deleteUserById(this.http, this.rootUrl, id, context).pipe(
            map((r: StrictHttpResponse<void>) => r.body!)
        );
    }

    updateUserRole(id: number, request: string, context?: HttpContext): Observable<void> {
        return updateUserRole(this.http, this.rootUrl, id, request, context).pipe(
            map((r: StrictHttpResponse<void>) => r.body!)
        );
    }

}
