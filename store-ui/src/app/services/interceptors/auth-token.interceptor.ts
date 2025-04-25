import {HttpErrorResponse, HttpHandlerFn, HttpInterceptorFn, HttpRequest} from '@angular/common/http';
import {inject} from "@angular/core";
import {AuthService} from "../services";
import {Router} from "@angular/router";
import {BehaviorSubject, Observable, throwError} from "rxjs";
import {catchError, filter, finalize, switchMap, take} from "rxjs/operators";
import {ToastService} from "../../shared/services/toast.service";

export const authTokenInterceptor: HttpInterceptorFn = (
    request: HttpRequest<unknown>,
    next: HttpHandlerFn
) => {
    const authService = inject(AuthService);
    const router = inject(Router);
    const toastService = inject(ToastService);

    const refreshState = (() => {
        let isRefreshing = false;
        const refreshTokenSubject = new BehaviorSubject<any>(null);

        return {
            startRefresh: () => {
                isRefreshing = true;
                refreshTokenSubject.next(null);
            },
            endRefresh: () => {
                isRefreshing = false;
            },
            setRefreshToken: (userData: any) => {
                refreshTokenSubject.next(userData);
            },
            isRefreshing: () => isRefreshing,
            refreshToken$: refreshTokenSubject.asObservable()
        };
    })();

    const handleError = (req: HttpRequest<any>): Observable<any> => {
        if (!refreshState.isRefreshing()) {
            refreshState.startRefresh();

            return authService.refreshToken().pipe(
                switchMap(userData => {
                    refreshState.endRefresh();
                    refreshState.setRefreshToken(userData);
                    return next(req);
                }),
                catchError(async error => {
                    console.error('Token refresh failed:', error);
                    refreshState.endRefresh();

                    authService.clearUserData();
                    router.navigate(['/prihlaseni'])
                        .catch(error => {
                            console.log("Při navigaci došlo k chybě:", error);
                        });

                    await toastService.showErrorToast('Chyba', 'Platnost přihlášení vypršela. Přihlaste se znovu.');
                    return throwError(() => new Error('Platnost přihlášení vypršela. Přihlaste se znovu.'));
                }),
                finalize(() => {
                    refreshState.endRefresh();
                })
            );
        } else {
            return refreshState.refreshToken$.pipe(
                filter(token => token !== null),
                take(1),
                switchMap(() => {
                    return next(req);
                })
            );
        }
    };

    return next(request).pipe(
        catchError(error => {
            if (error instanceof HttpErrorResponse && (error.status === 401 || error.status === 403)) {
                if (request.url.includes('api/v1/auth/login')) {
                    return throwError(() => error);
                }

                if (request.url.includes('/api/v1/auth/refresh')) {
                    authService.clearUserData();
                    return throwError(() => new Error('Platnost přihlášení vypršela. Přihlaste se znovu.'));
                }

                return handleError(request);
            }

            return throwError(() => error);
        })
    );
};
