import {
    ActivatedRouteSnapshot,
    CanActivate,
    Router,
    RouterStateSnapshot,
    UrlTree
} from '@angular/router';
import {AuthService} from "../services/auth.service";
import {Observable} from "rxjs";
import {Injectable} from "@angular/core";

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
    constructor(
        private authService: AuthService,
        private router: Router
    ) {}

    canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot
    ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
        if (this.authService.isLoggedIn) {
            return true;
        }

        return this.router.createUrlTree(['/prihlaseni'], {
            queryParams: { returnUrl: state.url }
        });
    }
}
