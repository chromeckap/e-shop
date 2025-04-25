import {Routes} from "@angular/router";
import {OrderListComponent} from "./pages/order-list/order-list.component";
import {ViewOrderComponent} from "./pages/view-order/view-order.component";

export default [
    {
        path: '',
        component: OrderListComponent,
    },
    {
        path: 'zobrazit/:id',
        component: ViewOrderComponent
    }

] as Routes;
