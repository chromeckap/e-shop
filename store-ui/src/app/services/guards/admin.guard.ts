import {Injectable} from "@angular/core";
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree} from "@angular/router";
import {AuthService} from "../services/auth.service";
import {Observable} from "rxjs";

@Injectable({ providedIn: 'root' })
export class AdminGuard implements CanActivate {
    constructor(
        private authService: AuthService,
        private router: Router
    ) {}

    canActivate(
        route: ActivatedRouteSnapshot,
        state: RouterStateSnapshot
    ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
        if (this.authService.isLoggedIn && this.authService.isAdmin) {
            return true;
        }

        if (this.authService.isLoggedIn) {
            return this.router.createUrlTree(['/403']);
        }

        return this.router.createUrlTree(['/prihlaseni'], {
            queryParams: { returnUrl: state.url }
        });
    }
}
