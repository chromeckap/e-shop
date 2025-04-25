import {Component, OnInit} from '@angular/core';
import {OrderOverviewResponse} from "../../../../../services/models/order/order-overview-response";
import {OrderService} from "../../../../../services/services/order.service";
import {AuthService} from "../../../../../services/services/auth.service";
import {getOrderStatusInfo} from "../../../../admin/order/services/get-order-status-info";
import {Router} from "@angular/router";
import {Button} from "primeng/button";
import {CustomCurrencyPipe} from "../../../../../shared/pipes/CustomCurrencyPipe";
import {DatePipe} from "@angular/common";
import {TableModule} from "primeng/table";
import {Tag} from "primeng/tag";

@Component({
    selector: 'app-user-order-list',
    imports: [
        Button,
        CustomCurrencyPipe,
        DatePipe,
        TableModule,
        Tag
    ],
    templateUrl: './user-order-list.component.html',
    standalone: true,
    styleUrl: './user-order-list.component.scss'
})
export class UserOrderListComponent implements OnInit {
    orders: OrderOverviewResponse[] = [];

    readonly getOrderStatusInfo = getOrderStatusInfo;

    constructor(
        private orderService: OrderService,
        private authService: AuthService,
        private router: Router
    ) {}

    ngOnInit(): void {
        this.loadData();
    }

    private loadData() {
        const userId = this.authService.getCurrentUser?.id;

        if (!userId) return;

        this.orderService.getOrdersByUserId(userId)
            .subscribe(orders => {
                this.orders = orders;
            });
    }

    viewOrder(order: OrderOverviewResponse) {
        this.router.navigate(['/moje-objednavky/', order.id])
            .catch(error => {
                console.log("Při navigaci došlo k chybě: ", error);
            });
    }
}
