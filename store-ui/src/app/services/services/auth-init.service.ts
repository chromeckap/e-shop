import {inject, Injectable} from '@angular/core';
import { AuthService } from './auth.service';
import { catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class AuthInitService {
    initAuth(): Promise<boolean> {
        const authService = inject(AuthService);

        return new Promise((resolve) => {
            if (authService.isLoggedIn) {
                authService.refreshUserInfo().pipe(
                    catchError(() => {
                        return of(null);
                    })
                ).subscribe(() => {
                    resolve(true);
                });
            } else {
                resolve(true);
            }
        });
    }
}
