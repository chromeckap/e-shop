import { Component } from '@angular/core';
import {ViewOrderComponent} from "../../../../admin/order/pages/view-order/view-order.component";

@Component({
    selector: 'app-user-single-order',
    imports: [
        ViewOrderComponent
    ],
    templateUrl: './user-single-order.component.html',
    standalone: true,
    styleUrl: './user-single-order.component.scss'
})
export class UserSingleOrderComponent {

}
