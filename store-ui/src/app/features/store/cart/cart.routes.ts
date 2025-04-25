import {Routes} from "@angular/router";
import {CartInfoComponent} from "./pages/cart-info/cart-info.component";
import {AuthGuard} from "../../../services/guards/auth.guard";

export default [
    {
        path: 'kosik',
        component: CartInfoComponent,
        canActivate: [AuthGuard],
    }

] as Routes;
