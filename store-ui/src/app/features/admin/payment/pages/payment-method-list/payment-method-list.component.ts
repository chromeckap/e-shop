import {Component, OnInit} from '@angular/core';
import {ConfirmationService} from "primeng/api";
import {PaymentMethodResponse} from "../../../../../services/models/payment-method/payment-method-response";
import {PaymentMethodService} from "../../../../../services/services/payment-method.service";
import {Router} from "@angular/router";
import {Table, TableModule} from "primeng/table";
import {Toolbar} from "primeng/toolbar";
import {Button} from "primeng/button";
import {IconField} from "primeng/iconfield";
import {InputText} from "primeng/inputtext";
import {ConfirmDialog} from "primeng/confirmdialog";
import {CustomCurrencyPipe} from "../../../../../shared/pipes/CustomCurrencyPipe";
import {Tag} from "primeng/tag";
import {forkJoin, Observable} from "rxjs";
import {ToastService} from "../../../../../shared/services/toast.service";

@Component({
    selector: 'app-payment-method-list',
    imports: [
        Toolbar,
        Button,
        TableModule,
        IconField,
        InputText,
        ConfirmDialog,
        CustomCurrencyPipe,
        Tag
    ],
    templateUrl: './payment-method-list.component.html',
    standalone: true,
    styleUrl: './payment-method-list.component.scss',
    providers: [ConfirmationService]
})
export class PaymentMethodListComponent implements OnInit {
    paymentMethods: Array<PaymentMethodResponse> = [];
    selectedPaymentMethods: Array<PaymentMethodResponse> = [];

    constructor(
        private paymentMethodService: PaymentMethodService,
        private confirmationService: ConfirmationService,
        private toastService: ToastService,
        private router: Router
    ) {}

    ngOnInit(): void {
        this.getAllPaymentMethods();
    }

    private getAllPaymentMethods() {
        this.paymentMethodService.getAllPaymentMethods()
            .subscribe({
                next: (paymentMethods) => {
                    this.paymentMethods = paymentMethods;
                }
            });
    }

    createPaymentMethod() {
        this.router.navigate(['admin/platebni-metody/vytvorit'])
            .catch(error => {
                console.log("Při navigaci došlo k chybě: ", error);
            });
    }

    editPaymentMethod(paymentMethod: PaymentMethodResponse) {
        this.router.navigate(['admin/platebni-metody/upravit', paymentMethod.id])
            .catch(error => {
                console.log("Při navigaci došlo k chybě: ", error);
            });
    }

    deletePaymentMethod(paymentMethod: PaymentMethodResponse) {
        this.confirmationService.confirm({
            header: 'Potvrzení odstranění',
            icon: 'pi pi-exclamation-triangle',
            message: 'Opravdu chceš odstranit platební metodu ' + paymentMethod.name + "?",
            acceptLabel: 'Ano',
            acceptIcon: 'pi pi-check',
            acceptButtonStyleClass: 'p-button-danger p-button-outlined',
            rejectLabel: 'Ne',
            rejectIcon: 'pi pi-check',
            rejectButtonStyleClass: 'p-button-secondary p-button-outlined',

            accept: () => {
                this.paymentMethodService.deletePaymentMethodById(paymentMethod.id!).subscribe({
                    next: async () => {
                        try {
                            this.getAllPaymentMethods();
                            await this.toastService.showSuccessToast('Úspěch', 'Platební metoda byla úspěšně odstraněna.');
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

    deleteSelectedPaymentMethods() {
        this.confirmationService.confirm({
            header: 'Potvrzení odstranění',
            icon: 'pi pi-exclamation-triangle',
            message: 'Opravdu chceš odstranit vybrané platební metody?',
            acceptLabel: 'Ano',
            acceptIcon: 'pi pi-check',
            acceptButtonStyleClass: 'p-button-danger p-button-outlined',
            rejectLabel: 'Ne',
            rejectIcon: 'pi pi-check',
            rejectButtonStyleClass: 'p-button-secondary p-button-outlined',

            accept: () => {
                const deleteOperations: { [key: string]: Observable<any> } = {};
                this.selectedPaymentMethods.forEach(paymentMethod => {
                    if (paymentMethod.id) {
                        deleteOperations[`paymentMethod-${paymentMethod.id}`] = this.paymentMethodService.deletePaymentMethodById(paymentMethod.id);
                    }
                });
                forkJoin(deleteOperations).subscribe({
                    next: async () => {
                        try {
                            this.getAllPaymentMethods();
                            this.selectedPaymentMethods = [];
                            await this.toastService.showSuccessToast('Úspěch', 'Platební metody byly úspěšně odstraněny.');
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
