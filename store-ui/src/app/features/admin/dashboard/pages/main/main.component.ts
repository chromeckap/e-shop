import {Component, OnInit} from '@angular/core';
import {StatsCardComponent} from "../../components/stats-card/stats-card.component";
import {OrderPageResponse} from "../../../../../services/models/order/order-page-response";
import {UserPageResponse} from "../../../../../services/models/user/user-page-response";
import {OrderService} from "../../../../../services/services/order.service";
import {UserService} from "../../../../../services/services/user.service";
import {CustomCurrencyPipe} from "../../../../../shared/pipes/CustomCurrencyPipe";

@Component({
    selector: 'app-main',
    imports: [
        StatsCardComponent,
        CustomCurrencyPipe
    ],
    templateUrl: './main.component.html',
    standalone: true,
    styleUrl: './main.component.scss'
})
export class MainComponent implements OnInit {
    ordersPage: OrderPageResponse = {};
    newOrdersLastWeek = 0;
    totalRevenue = 0;
    weeklyRevenue = 0;
    usersPage: UserPageResponse = {};
    newUsersLastWeek = 0;
    size = 99999;

    constructor(
        private orderService: OrderService,
        private userService: UserService
    ) {}

    ngOnInit(): void {
        this.getAllOrders();
        this.getAllUsers();
    }

    private getAllOrders() {
        this.orderService.getAllOrders({
            pageSize: this.size
        }).subscribe({
            next: (orders) => {
                this.ordersPage = orders;

                const oneWeekAgo = new Date();
                oneWeekAgo.setDate(oneWeekAgo.getDate() - 7);

                if (this.ordersPage.content) {
                    this.newOrdersLastWeek = this.ordersPage.content.filter(order => {
                        const orderDate = new Date(order.createTime!);
                        return orderDate >= oneWeekAgo;
                    }).length;

                    this.totalRevenue = this.ordersPage.content.reduce((sum, order) => sum + order.totalPrice!, 0);

                    this.weeklyRevenue = this.ordersPage.content
                        .filter(order => {
                            const orderDate = new Date(order.createTime!);
                            return orderDate >= oneWeekAgo;
                        })
                        .reduce((sum, order) => sum + order.totalPrice!, 0);
                }
            }
        });
    }


    private getAllUsers() {
        this.userService.getAllUsers({
            pageSize: this.size
        })
            .subscribe({
                next: (users) => {
                    this.usersPage = users;

                    const oneWeekAgo = new Date();
                    oneWeekAgo.setDate(oneWeekAgo.getDate() - 7);

                    if (this.usersPage.content) {
                        this.newUsersLastWeek = this.usersPage.content.filter(user => {
                            const userDate = new Date(user.createTime!);
                            return userDate >= oneWeekAgo;
                        }).length;
                    }
                }
            });
    }

}
