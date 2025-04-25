import {Routes} from "@angular/router";
import {AuthGuard} from "../../../services/guards/auth.guard";
import {CheckoutComponent} from "./pages/checkout/checkout.component";
import {UserSingleOrderComponent} from "./pages/user-single-order/user-single-order.component";
import {UserOrderListComponent} from "./pages/user-order-list/user-order-list.component";

export default [
    {
        path: 'pokladna',
        component: CheckoutComponent,
        canActivate: [AuthGuard],
    },
    {
        path: 'moje-objednavky',
        component: UserOrderListComponent,
        canActivate: [AuthGuard],
    },
    {
        path: 'moje-objednavky/:id',
        component: UserSingleOrderComponent,
        canActivate: [AuthGuard],
    }

] as Routes;
