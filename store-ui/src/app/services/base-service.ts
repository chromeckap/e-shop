import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ApiConfiguration } from './api-configuration';

@Injectable()
export class BaseService {
    constructor(
        protected config: ApiConfiguration,
        protected http: HttpClient
    ) {
    }

    private _rootUrl?: string;
    private _imageUrl?: string;

    get rootUrl(): string {
        return this._rootUrl || this.config.rootUrl;
    }

    get imageUrl(): string {
        return this._imageUrl || this.config.imageUrl;
    }
}
