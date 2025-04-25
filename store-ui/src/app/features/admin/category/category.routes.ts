import {Routes} from "@angular/router";
import {CategoryListComponent} from "./pages/category-list/category-list.component";
import {ManageCategoryComponent} from "./pages/manage-category/manage-category.component";

export default [
    {
        path: '',
        component: CategoryListComponent,
    },
    {
        path: 'vytvorit',
        component: ManageCategoryComponent,
    },
    {
        path: 'upravit/:id',
        component: ManageCategoryComponent
    },

] as Routes;
