import {Routes} from "@angular/router";
import {PaymentMethodListComponent} from "./pages/payment-method-list/payment-method-list.component";
import {ManagePaymentMethodComponent} from "./pages/manage-payment-method/manage-payment-method.component";

export default [
    {
        path: '',
        component: PaymentMethodListComponent,
    },
    {
        path: 'vytvorit',
        component: ManagePaymentMethodComponent
    },
    {
        path: 'upravit/:id',
        component: ManagePaymentMethodComponent
    },

] as Routes;
