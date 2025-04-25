import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root',
})
export class ApiConfiguration {
    rootUrl: string = 'http://localhost:8080';
    imageUrl: string = 'http://localhost:8030';
}

export interface ApiConfigurationParams {
    rootUrl?: string;
    imageUrl?: string;
}
