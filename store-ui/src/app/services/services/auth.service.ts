import { Injectable } from '@angular/core';
import { HttpClient, HttpContext } from '@angular/common/http';
import { ApiConfiguration } from '../api-configuration';
import { Observable, BehaviorSubject, tap, throwError } from 'rxjs';
import { StrictHttpResponse } from '../strict-http-response';
import { map, catchError } from 'rxjs/operators';
import { BaseService } from "../base-service";
import { UserResponse } from "../models/user/user-response";
import { LoginRequest } from "../models/auth/login-request";
import { RegisterRequest } from "../models/auth/register-request";
import { login } from "../fn/auth/login";
import { register } from "../fn/auth/register";
import { logout } from "../fn/auth/logout";
import { refreshUserInfo } from "../fn/auth/refresh-user-info";
import { refreshToken } from "../fn/auth/refresh-token";

@Injectable({ providedIn: 'root' })
export class AuthService extends BaseService {
    private currentUserSubject = new BehaviorSubject<UserResponse | null>(null);
    currentUser$ = this.currentUserSubject.asObservable();

    private tokenRefreshInProgress = false;

    constructor(config: ApiConfiguration, http: HttpClient) {
        super(config, http);
        this.loadUserData();
    }

    private loadUserData(): void {
        try {
            const userData = localStorage.getItem('user_data');
            if (userData) {
                const parsedData = JSON.parse(userData) as UserResponse;
                this.currentUserSubject.next(parsedData);
            }
        } catch (error) {
            console.error('Chyba při načítání uživatelských dat', error);
            this.clearUserData();
        }
    }

    private saveUserData(user: UserResponse): void {
        this.currentUserSubject.next(user);
        localStorage.setItem('user_data', JSON.stringify(user));
    }

    clearUserData(): void {
        this.currentUserSubject.next(null);
        localStorage.removeItem('user_data');
    }

    login(request: LoginRequest, context?: HttpContext): Observable<UserResponse> {
        return login(this.http, this.rootUrl, { requestBody: request }, context).pipe(
            map((r: StrictHttpResponse<UserResponse>) => r.body!),
            tap(user => {
                this.saveUserData(user);
            }),
            catchError(error => {
                console.error('Chyba při přihlášení', error);
                return throwError(() => error);
            })
        );
    }

    register(request: RegisterRequest, context?: HttpContext): Observable<void> {
        return register(this.http, this.rootUrl, { requestBody: request }, context).pipe(
            map((r: StrictHttpResponse<void>) => r.body!),
            catchError(error => {
                console.error('Chyba při registraci', error);
                return throwError(() => error);
            })
        );
    }

    logout(context?: HttpContext): Observable<void> {
        return logout(this.http, this.rootUrl, context).pipe(
            map((r: StrictHttpResponse<void>) => r.body!),
            tap(() => {
                this.clearUserData();
            }),
            catchError(error => {
                this.clearUserData();
                return throwError(() => error);
            })
        );
    }

    refreshToken(context?: HttpContext): Observable<UserResponse> {
        this.tokenRefreshInProgress = true;

        return refreshToken(this.http, this.rootUrl, context).pipe(
            map((r: StrictHttpResponse<UserResponse>) => r.body!),
            tap(userData => {
                this.saveUserData(userData);
                this.tokenRefreshInProgress = false;
            }),
            catchError(error => {
                this.clearUserData();
                this.tokenRefreshInProgress = false;
                return throwError(() => error);
            })
        );
    }

    refreshUserInfo(context?: HttpContext): Observable<UserResponse> {
        return refreshUserInfo(this.http, this.rootUrl, context).pipe(
            map(response => response.body),
            tap(userData => {
                if (userData) this.saveUserData(userData);
            }),
            catchError(error => {
                console.error('Chyba při obnovení informací o uživateli', error);
                return throwError(() => error);
            })
        );
    }

    get isLoggedIn(): boolean {
        return !!this.currentUserSubject.value;
    }

    hasRole(role: string): boolean {
        const user = this.currentUserSubject.value;
        if (!user || !user.role) {
            return false;
        }
        return user.role.includes(role);
    }

    get isAdmin(): boolean {
        return this.hasRole('ADMIN');
    }

    get getUserEmail(): string | null {
        return this.currentUserSubject.value?.email || null;
    }

    get getCurrentUser(): UserResponse | null {
        return this.currentUserSubject.value;
    }

    get isRefreshingToken(): boolean {
        return this.tokenRefreshInProgress;
    }
}
