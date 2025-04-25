import {Routes} from "@angular/router";
import {LoginComponent} from "./pages/login/login.component";
import {RegisterComponent} from "./pages/register/register.component";

export default [
    {
        path: 'prihlaseni',
        component: LoginComponent,
    },
    {
        path: 'registrace',
        component: RegisterComponent,
    }

] as Routes;
