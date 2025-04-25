import {provideHttpClient, withFetch, withInterceptors} from '@angular/common/http';
import {APP_INITIALIZER, ApplicationConfig} from '@angular/core';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideRouter, withEnabledBlockingInitialNavigation, withInMemoryScrolling } from '@angular/router';
import { providePrimeNG } from 'primeng/config';
import { appRoutes } from './app.routes';
import Lara from "@primeng/themes/lara";
import {MessageService} from "primeng/api";
import {AuthInitService} from "./app/services/services/auth-init.service";
import {authTokenInterceptor} from "./app/services/interceptors/auth-token.interceptor";

export const appConfig: ApplicationConfig = {
    providers: [
        MessageService,
        provideRouter(appRoutes, withInMemoryScrolling({ anchorScrolling: 'enabled', scrollPositionRestoration: 'enabled' }), withEnabledBlockingInitialNavigation()),
        provideHttpClient(
            withFetch(),
            withInterceptors([authTokenInterceptor])),
        provideAnimationsAsync(),
        providePrimeNG({ theme: { preset: Lara, options: { darkModeSelector: '.app-light' } } }),
        {
            provide: APP_INITIALIZER,
            useFactory: initializeAuth,
            deps: [AuthInitService],
            multi: true
        }
    ]
};

export function initializeAuth(authInitService: AuthInitService) {
    return () => authInitService.initAuth();
}
