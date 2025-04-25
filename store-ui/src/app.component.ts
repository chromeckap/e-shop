import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import {CommonModule} from "@angular/common";
import {ToastModule} from "primeng/toast";

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [RouterModule, CommonModule, ToastModule],
    template: `
        <router-outlet></router-outlet>
        <p-toast key="global" position="bottom-right"></p-toast>
    `
})
export class AppComponent {}
