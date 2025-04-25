import {Routes} from "@angular/router";
import {DeliveryMethodListComponent} from "./pages/delivery-method-list/delivery-method-list.component";
import {ManageDeliveryMethodComponent} from "./pages/manage-delivery-method/manage-delivery-method.component";

export default [
    {
        path: '',
        component: DeliveryMethodListComponent,
    },
    {
        path: 'vytvorit',
        component: ManageDeliveryMethodComponent
    },
    {
        path: 'upravit/:id',
        component: ManageDeliveryMethodComponent
    },

] as Routes;
