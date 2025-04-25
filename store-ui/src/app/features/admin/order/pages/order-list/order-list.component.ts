import {Component, OnInit} from '@angular/core';
import {OrderPageResponse} from "../../../../../services/models/order/order-page-response";
import {OrderService} from "../../../../../services/services/order.service";
import {Router} from "@angular/router";
import {OrderOverviewResponse} from "../../../../../services/models/order/order-overview-response";
import {TableModule} from "primeng/table";
import {Tag} from "primeng/tag";
import {CustomCurrencyPipe} from "../../../../../shared/pipes/CustomCurrencyPipe";
import {Button} from "primeng/button";
import {DatePipe} from "@angular/common";
import {ConfirmDialog} from "primeng/confirmdialog";
import {ConfirmationService} from "primeng/api";
import {getOrderStatusInfo} from "../../services/get-order-status-info";
import {ToastService} from "../../../../../shared/services/toast.service";

@Component({
    selector: 'app-order-list',
    imports: [
        TableModule,
        Tag,
        CustomCurrencyPipe,
        Button,
        DatePipe,
        ConfirmDialog
    ],
    templateUrl: './order-list.component.html',
    standalone: true,
    styleUrl: './order-list.component.scss',
    providers: [ConfirmationService]
})
export class OrderListComponent implements OnInit {
    ordersPage: OrderPageResponse = {};
    page = 0;
    size = 10;
    attribute = 'id';
    direction = 'desc';

    protected readonly getOrderStatusInfo = getOrderStatusInfo;

    constructor(
        private orderService: OrderService,
        private confirmationService: ConfirmationService,
        private toastService: ToastService,
        private router: Router
    ) {}

    ngOnInit(): void {
        this.getAllOrders();
    }

    private getAllOrders() {
        this.orderService.getAllOrders({
            pageNumber: this.page,
            pageSize: this.size,
            attribute: this.attribute,
            direction: this.direction
        })
            .subscribe({
                next: (orders) => {
                    this.ordersPage = orders;
                }
            });
    }

    onPageChange(event: any) {
        this.page = event.first / event.rows;
        this.size = event.rows;
        this.getAllOrders();
    }

    viewOrder(order: OrderOverviewResponse) {
        this.router.navigate(['/admin/objednavky/zobrazit', order.id])
            .catch(error => {
                console.log("Při navigaci došlo k chybě: ", error);
            });
    }

    deleteOrder(order: OrderOverviewResponse) {
        this.confirmationService.confirm({
            header: 'Potvrzení odstranění',
            icon: 'pi pi-exclamation-triangle',
            message: 'Opravdu chceš odstranit objednávku s ID ' + order.id + "?",
            acceptLabel: 'Ano',
            acceptIcon: 'pi pi-check',
            acceptButtonStyleClass: 'p-button-danger p-button-outlined',
            rejectLabel: 'Ne',
            rejectIcon: 'pi pi-check',
            rejectButtonStyleClass: 'p-button-secondary p-button-outlined',

            accept: () => {
                this.orderService.deleteOrderById(order.id!).subscribe({
                    next: async () => {
                        try {
                            this.getAllOrders();
                            await this.toastService.showSuccessToast('Úspěch', 'Objednávka byla úspěšně odstraněna.');
                        } catch (error) {
                            console.log("Chyba při operacích po odstranění:", error);
                        }
                    },
                    error: async (error) => {
                        console.log(error);
                        try {
                            await this.toastService.showErrorToast('Chyba', error.error.detail);
                        } catch (toastError) {
                            console.log("Chyba při zobrazení toastu:", toastError);
                        }
                    }
                });
            }

        });
    }
}
