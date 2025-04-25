import {Routes} from "@angular/router";
import {ProductCatalogComponent} from "./pages/product-catalog/product-catalog.component";
import {ProductDetailComponent} from "./pages/product-detail/product-detail.component";

export default [
    {
        path: 'kategorie/:id/produkty',
        component: ProductCatalogComponent,
    },
    {
        path: 'produkt/:id',
        component: ProductDetailComponent,
    }

] as Routes;
