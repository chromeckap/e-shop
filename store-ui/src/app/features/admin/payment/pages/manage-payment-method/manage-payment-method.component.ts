import {Component, OnInit} from '@angular/core';
import {ConfirmationService} from "primeng/api";
import {PaymentGatewayType} from "../../../../../services/models/payment-method/payment-gateway-type";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {PaymentMethodService} from "../../../../../services/services/payment-method.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Button} from "primeng/button";
import {NgClass} from "@angular/common";
import {Toolbar} from "primeng/toolbar";
import {ConfirmDialog} from "primeng/confirmdialog";
import {OverviewComponent} from "../../components/overview/overview.component";
import {GatewayTypeSelectComponent} from "../../components/gateway-type-select/gateway-type-select.component";
import {ToastService} from "../../../../../shared/services/toast.service";

@Component({
    selector: 'app-manage-payment-method',
    imports: [
        Button,
        Toolbar,
        NgClass,
        ConfirmDialog,
        OverviewComponent,
        GatewayTypeSelectComponent
    ],
    templateUrl: './manage-payment-method.component.html',
    standalone: true,
    styleUrl: './manage-payment-method.component.scss',
    providers: [ConfirmationService]
})
export class ManagePaymentMethodComponent implements OnInit {
    gatewayTypes: PaymentGatewayType[] = [];
    form: FormGroup;

    constructor(
        private paymentMethodService: PaymentMethodService,
        private confirmationService: ConfirmationService,
        private toastService: ToastService,
        private formBuilder: FormBuilder,
        private router: Router,
        private activatedRoute: ActivatedRoute
    ) {
        this.form = this.formBuilder.group({
            id: [{ value: null, disabled: true }],
            name: ['', Validators.required],
            gatewayType: [null, Validators.required],
            isActive: [false, Validators.required],
            price: [0, Validators.required],
            isFreeForOrderAbove: [false, Validators.required],
            freeForOrderAbove: [0, Validators.required]
        });
    }

    ngOnInit(): void {
        this.loadData();
    }

    private loadData() {
        this.paymentMethodService.getPaymentGatewayTypes().subscribe({
            next: (gatewayTypes) => {
                this.gatewayTypes = gatewayTypes;

                const id = this.activatedRoute.snapshot.params['id'];
                if (id) {
                    this.paymentMethodService.getPaymentMethodById(id)
                        .subscribe(paymentMethod => {
                            this.form.patchValue(paymentMethod);
                        });
                }
            }
        });
    }

    get isEditing() {
        return this.form.getRawValue().id;
    }

    savePaymentMethod() {
        if (this.form.invalid) {
            return;
        }

        const paymentMethod = {
            id: this.form.getRawValue().id,
            name: this.form.getRawValue().name,
            type: this.form.getRawValue().gatewayType.type,
            isActive: this.form.getRawValue().isActive,
            price: this.form.getRawValue().price,
            isFreeForOrderAbove: this.form.getRawValue().isFreeForOrderAbove,
            freeForOrderAbove: this.form.getRawValue().freeForOrderAbove
        };

        const observable = this.isEditing
            ? this.paymentMethodService.updatePaymentMethod(paymentMethod.id, paymentMethod)
            : this.paymentMethodService.createPaymentMethod(paymentMethod);

        observable.subscribe({
            next: async (id) => {
                try {
                    await this.toastService.showSuccessToast('Úspěch', 'Platební metoda byla úspěšně uložena.');
                    await this.router.navigate(['admin/platebni-metody/upravit/' + id]);
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

    deletePaymentMethod() {
        this.confirmationService.confirm({
            header: 'Potvrzení odstranění',
            icon: 'pi pi-exclamation-triangle',
            message: 'Opravdu chceš odstranit platební metodu ' + this.form.getRawValue().name + "?",
            acceptLabel: 'Ano',
            acceptIcon: 'pi pi-check',
            acceptButtonStyleClass: 'p-button-danger p-button-outlined',
            rejectLabel: 'Ne',
            rejectIcon: 'pi pi-check',
            rejectButtonStyleClass: 'p-button-secondary p-button-outlined',

            accept: () => {
                this.paymentMethodService.deletePaymentMethodById(this.form.getRawValue().id).subscribe({
                    next: async () => {
                        try {
                            await this.toastService.showSuccessToast('Úspěch', 'Platební metoda byla úspěšně odstraněna.');
                            await this.router.navigate(['/admin/platebni-metody']);
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
