import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {OrderService} from "../../../../../services/services/order.service";
import {ConfirmationService} from "primeng/api";
import {ToastService} from "../../../../../shared/services/toast.service";
import {ActivatedRoute, Router} from "@angular/router";
import {OrderResponse} from "../../../../../services/models/order/order-response";
import {Button} from "primeng/button";
import {Toolbar} from "primeng/toolbar";
import {getOrderStatusInfo, getOrderStatusOptions, ORDER_STATUS_MAP} from "../../services/get-order-status-info";
import {Tag} from "primeng/tag";
import {ConfirmDialog} from "primeng/confirmdialog";
import {ItemListComponent} from "../../components/item-list/item-list.component";
import {SidebarInfoComponent} from "../../components/sidebar-info/sidebar-info.component";
import {Select, SelectChangeEvent} from "primeng/select";
import {FormsModule} from "@angular/forms";

@Component({
    selector: 'app-view-order',
    imports: [
        Button,
        Toolbar,
        Tag,
        ConfirmDialog,
        ItemListComponent,
        SidebarInfoComponent,
        Select,
        FormsModule
    ],
    templateUrl: './view-order.component.html',
    standalone: true,
    styleUrl: './view-order.component.scss',
    providers: [ConfirmationService]
})
export class ViewOrderComponent implements OnInit {
    order: OrderResponse = {};
    selectedStatus: string = '';
    @Input() isAdminView: boolean = true;

    @ViewChild(SidebarInfoComponent) sidebarInfo!: SidebarInfoComponent;

    readonly getOrderStatusInfo = getOrderStatusInfo;
    readonly getOrderStatusOptions = getOrderStatusOptions;

    constructor(
        private orderService: OrderService,
        private confirmationService: ConfirmationService,
        private toastService: ToastService,
        private router: Router,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit(): void {
        this.getOrderById();
    }

    private getOrderById() {
        const id = this.activatedRoute.snapshot.params['id'];

        if (!id)
            this.orderNotFound();

        this.orderService.getOrderById(id).subscribe({
            next: (order) => {
                this.order = order;
                this.selectedStatus = order.status!;
                this.sidebarInfo.getPaymentByOrderId(order.id!);
            },
            error: (error) => {
                console.error('Chyba při načítání objednávky:', error);
                this.orderNotFound();
            }
        });
    }

    private orderNotFound() {
        this.router.navigate(['404'])
            .catch((error) => {
                console.log("Chyba při navigaci:", error);
            });
    }

    onStatusChange(event: SelectChangeEvent) {
        const newStatus = event.value;
        const statusLabel = ORDER_STATUS_MAP[newStatus]?.value || newStatus;

        this.confirmationService.confirm({
            header: 'Potvrzení změny statusu',
            icon: 'pi pi-cog',
            message: "Opravdu chceš změnit status objednávky na '" + statusLabel + "'?",
            acceptLabel: 'Ano',
            acceptIcon: 'pi pi-check',
            acceptButtonStyleClass: 'p-button-primary p-button-outlined',
            rejectLabel: 'Ne',
            rejectIcon: 'pi pi-check',
            rejectButtonStyleClass: 'p-button-secondary p-button-outlined',

            accept: () => {
                this.orderService.updateOrderStatus(this.order.id!, newStatus).subscribe({
                    next: async () => {
                        try {
                            this.order.status = newStatus;
                            await this.toastService.showSuccessToast('Úspěch', 'Status objednávky byl úspěšně změněn.');
                        } catch (error) {
                            console.log("Chyba při zobrazení toastu nebo navigaci:", error);
                        }
                    },
                    error: async (error) => {
                        console.log(error);
                        try {
                            this.selectedStatus = this.order.status!;
                            await this.toastService.showErrorToast('Chyba', error.error.detail);
                        } catch (toastError) {
                            console.log("Chyba při zobrazení chybového toastu:", toastError);
                        }
                    }
                });
            },
            reject: () => {
                this.selectedStatus = this.order.status!;
            }
        });
    }

    deleteOrder() {
        this.confirmationService.confirm({
            header: 'Potvrzení odstranění',
            icon: 'pi pi-exclamation-triangle',
            message: 'Opravdu chceš odstranit objednávku s ID ' + this.order.id + "?",
            acceptLabel: 'Ano',
            acceptIcon: 'pi pi-check',
            acceptButtonStyleClass: 'p-button-danger p-button-outlined',
            rejectLabel: 'Ne',
            rejectIcon: 'pi pi-check',
            rejectButtonStyleClass: 'p-button-secondary p-button-outlined',

            accept: () => {
                this.orderService.deleteOrderById(this.order.id!).subscribe({
                    next: async () => {
                        try {
                            await this.toastService.showSuccessToast('Úspěch', 'Objednávka byla úspěšně odstraněna.');
                            await this.router.navigate(['/admin/objednavky']);
                        } catch (error) {
                            console.log("Chyba při zobrazení toastu nebo navigaci:", error);
                        }
                    },
                    error: async (error) => {
                        console.log(error);
                        try {
                            await this.toastService.showErrorToast('Chyba', error.error.detail);
                        } catch (toastError) {
                            console.log("Chyba při zobrazení chybového toastu:", toastError);
                        }
                    }
                });
            }
        });
    }

}
