import {Component, OnInit} from '@angular/core';
import {ConfirmationService} from "primeng/api";
import {Router} from "@angular/router";
import {Table, TableModule} from "primeng/table";
import {DeliveryMethodResponse} from "../../../../../services/models/delivery-method/delivery-method-response";
import {Button} from "primeng/button";
import {ConfirmDialog} from "primeng/confirmdialog";
import {CustomCurrencyPipe} from "../../../../../shared/pipes/CustomCurrencyPipe";
import {IconField} from "primeng/iconfield";
import {InputText} from "primeng/inputtext";
import {Tag} from "primeng/tag";
import {Toolbar} from "primeng/toolbar";
import {forkJoin, Observable} from "rxjs";
import {ToastService} from "../../../../../shared/services/toast.service";
import {DeliveryMethodService} from "../../../../../services/services/delivery-method.service";

@Component({
    selector: 'app-delivery-method-list',
    imports: [
        Button,
        ConfirmDialog,
        CustomCurrencyPipe,
        IconField,
        InputText,
        TableModule,
        Tag,
        Toolbar
    ],
    templateUrl: './delivery-method-list.component.html',
    styleUrl: './delivery-method-list.component.scss',
    standalone: true,
    providers: [ConfirmationService]
})
export class DeliveryMethodListComponent implements OnInit {
    deliveryMethods: Array<DeliveryMethodResponse> = [];
    selectedDeliveryMethods: Array<DeliveryMethodResponse> = [];

    constructor(
        private deliveryMethodService: DeliveryMethodService,
        private confirmationService: ConfirmationService,
        private toastService: ToastService,
        private router: Router
    ) {}

    ngOnInit(): void {
        this.getAllDeliveryMethods();
    }

    private getAllDeliveryMethods() {
        this.deliveryMethodService.getAllDeliveryMethods()
            .subscribe({
                next: (deliveryMethods) => {
                    this.deliveryMethods = deliveryMethods;
                }
            });
    }

    createDeliveryMethod() {
        this.router.navigate(['admin/dopravni-metody/vytvorit'])
            .catch(error => {
                console.log("Při navigaci došlo k chybě: ", error);
            });
    }

    editDeliveryMethod(deliveryMethod: DeliveryMethodResponse) {
        this.router.navigate(['admin/dopravni-metody/upravit', deliveryMethod.id])
            .catch(error => {
                console.log("Při navigaci došlo k chybě: ", error);
            });
    }

    deleteDeliveryMethod(deliveryMethod: DeliveryMethodResponse) {
        this.confirmationService.confirm({
            header: 'Potvrzení odstranění',
            icon: 'pi pi-exclamation-triangle',
            message: 'Opravdu chceš odstranit dopravní metodu ' + deliveryMethod.name + "?",
            acceptLabel: 'Ano',
            acceptIcon: 'pi pi-check',
            acceptButtonStyleClass: 'p-button-danger p-button-outlined',
            rejectLabel: 'Ne',
            rejectIcon: 'pi pi-check',
            rejectButtonStyleClass: 'p-button-secondary p-button-outlined',

            accept: () => {
                this.deliveryMethodService.deleteDeliveryMethodById(deliveryMethod.id!).subscribe({
                    next: async () => {
                        try {
                            this.getAllDeliveryMethods();
                            await this.toastService.showSuccessToast('Úspěch', 'Dopravní metoda byla úspěšně odstraněna.');
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

    deleteSelectedDeliveryMethods() {
        this.confirmationService.confirm({
            header: 'Potvrzení odstranění',
            icon: 'pi pi-exclamation-triangle',
            message: 'Opravdu chceš odstranit vybrané dopravní metody?',
            acceptLabel: 'Ano',
            acceptIcon: 'pi pi-check',
            acceptButtonStyleClass: 'p-button-danger p-button-outlined',
            rejectLabel: 'Ne',
            rejectIcon: 'pi pi-check',
            rejectButtonStyleClass: 'p-button-secondary p-button-outlined',

            accept: () => {
                const deleteOperations: { [key: string]: Observable<any> } = {};
                this.selectedDeliveryMethods.forEach(deliveryMethod => {
                    if (deliveryMethod.id) {
                        deleteOperations[`deliveryMethod-${deliveryMethod.id}`] = this.deliveryMethodService.deleteDeliveryMethodById(deliveryMethod.id);
                    }
                });
                forkJoin(deleteOperations).subscribe({
                    next: async () => {
                        try {
                            this.getAllDeliveryMethods();
                            this.selectedDeliveryMethods = [];
                            await this.toastService.showSuccessToast('Úspěch', 'Dopravní metody byly úspěšně odstraněny.');
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

    onGlobalFilter(table: Table, event: Event) {
        table.filterGlobal((event.target as HTMLInputElement).value, 'contains');
    }
}
