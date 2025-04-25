import {Component} from '@angular/core';
import {RouterOutlet} from "@angular/router";
import {StoreFooter} from "./components/store-footer/store.footer";
import {StoreTopbar} from "./components/store-topbar/store.topbar";
import {LayoutConfigurator} from "../services/layout.configurator";

@Component({
    selector: 'store-layout',
    imports: [
        RouterOutlet,
        StoreFooter,
        StoreTopbar,
        LayoutConfigurator
    ],
    templateUrl: './store.layout.html',
    standalone: true
})
export class StoreLayout {

}
