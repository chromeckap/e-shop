import { Routes } from '@angular/router';

export default [
    {
        path: '',
        loadChildren: () => import('./dashboard/dashboard.routes')
    },
    {
        path: 'uzivatele',
        loadChildren: () => import('./user/user.routes')
    },
    {
        path: 'atributy',
        loadChildren: () => import('./attribute/attribute.routes')
    },
    {
        path: 'kategorie',
        loadChildren: () => import('./category/category.routes')
    },
    {
        path: 'produkty',
        loadChildren: () => import('./product/product.routes')
    },
    {
        path: 'objednavky',
        loadChildren: () => import('./order/order.routes')
    },
    {
        path: 'platebni-metody',
        loadChildren: () => import('./payment/payment-method.routes')
    },
    {
        path: 'dopravni-metody',
        loadChildren: () => import('./delivery/delivery-method.routes')
    }
] as Routes;
