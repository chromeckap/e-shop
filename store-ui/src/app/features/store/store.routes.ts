import { Routes } from '@angular/router';

export default [
    {
        path: '',
        loadChildren: () => import('./product/product.routes')
    },
    {
        path: '',
        loadChildren: () => import('./cart/cart.routes')
    },
    {
        path: '',
        loadChildren: () => import('./order/order.routes')
    },
    {
        path: '',
        loadChildren: () => import('./error/error.routes')
    }
] as Routes;
