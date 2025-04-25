import {Routes} from "@angular/router";
import {AttributeListComponent} from "./pages/attribute-list/attribute-list.component";
import {ManageAttributeComponent} from "./pages/manage-attribute/manage-attribute.component";

export default [
    {
        path: '',
        component: AttributeListComponent,
    },
    {
        path: 'vytvorit',
        component: ManageAttributeComponent,
    },
    {
        path: 'upravit/:id',
        component: ManageAttributeComponent
    },

] as Routes;
