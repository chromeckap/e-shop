import { Routes } from '@angular/router';
import {StoreLayout} from "./app/core/layouts/store-layout/store.layout";
import {AdminLayout} from "./app/core/layouts/admin-layout/admin.layout";
import {AdminGuard} from "./app/services/guards/admin.guard";
import {HomepageComponent} from "./app/features/store/homepage/homepage.component";

export const appRoutes: Routes = [
    {
        path: 'admin',
        component: AdminLayout,
        canActivate: [AdminGuard],
        loadChildren: () => import('./app/features/admin/admin.routes')
    },
    {
        path: '',
        component: StoreLayout,
        children: [
            {
                path: '',
                component: HomepageComponent,
            },
            {
                path: '',
                loadChildren: () => import('./app/core/auth/auth.routes'),
            },
            {
                path: '',
                loadChildren: () => import('./app/features/store/store.routes'),
            }
        ]
    },
    {
        path: '**', redirectTo: '/404'
    }
];
