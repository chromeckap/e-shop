import {Routes} from "@angular/router";
import {ProductListComponent} from "./pages/product-list/product-list.component";
import {ManageProductComponent} from "./pages/manage-product/manage-product.component";

export default [
    {
        path: '',
        component: ProductListComponent,
    },
    {
        path: 'vytvorit',
        component: ManageProductComponent,
    },
    {
        path: 'upravit/:id',
        component: ManageProductComponent,
    }

] as Routes;
