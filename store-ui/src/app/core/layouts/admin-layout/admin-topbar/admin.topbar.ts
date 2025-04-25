import { Component } from '@angular/core';
import {RouterLink} from "@angular/router";
import {LayoutService} from "../../services/layout.service";
import {UserPopoverComponent} from "../../../auth/components/user-popover/user-popover.component";

@Component({
    selector: 'admin-topbar',
    imports: [
        RouterLink,
        UserPopoverComponent
    ],
    templateUrl: './admin.topbar.html',
    standalone: true
})
export class AdminTopbar {

    constructor(
        protected layoutService: LayoutService
    ) {}

}
