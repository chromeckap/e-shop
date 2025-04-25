import {Component, OnInit} from '@angular/core';
import {CourierType} from "../../../../../services/models/delivery-method/courier-type";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ConfirmationService} from "primeng/api";
import {ActivatedRoute, Router} from "@angular/router";
import {Button} from "primeng/button";
import {ConfirmDialog} from "primeng/confirmdialog";
import {NgClass} from "@angular/common";
import {Toolbar} from "primeng/toolbar";
import {CourierTypeSelectComponent} from "../../components/courier-type-select/courier-type-select.component";
import {OverviewComponent} from "../../components/overview/overview.component";
import {ToastService} from "../../../../../shared/services/toast.service";
import {DeliveryMethodService} from "../../../../../services/services/delivery-method.service";

@Component({
    selector: 'app-manage-delivery-method',
    imports: [
        Button,
        ConfirmDialog,
        Toolbar,
        NgClass,
        CourierTypeSelectComponent,
        OverviewComponent
    ],
    templateUrl: './manage-delivery-method.component.html',
    standalone: true,
    styleUrl: './manage-delivery-method.component.scss',
    providers: [ConfirmationService]
})
export class ManageDeliveryMethodComponent implements OnInit {
    courierTypes: CourierType[] = [];
    form: FormGroup;

    constructor(
        private deliveryMethodService: DeliveryMethodService,
        private confirmationService: ConfirmationService,
        private toastService: ToastService,
        private formBuilder: FormBuilder,
        private router: Router,
        private activatedRoute: ActivatedRoute
    ) {
        this.form = this.formBuilder.group({
            id: [{ value: null, disabled: true }],
            name: ['', Validators.required],
            courierType: [null, Validators.required],
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
        this.deliveryMethodService.getCourierTypes().subscribe({
            next: (courierTypes) => {
                this.courierTypes = courierTypes;

                const id = this.activatedRoute.snapshot.params['id'];
                if (id) {
                    this.deliveryMethodService.getDeliveryMethodById(id)
                        .subscribe(deliveryMethod => {
                            this.form.patchValue(deliveryMethod);
                        });
                }
            }
        });
    }

    get isEditing() {
        return this.form.getRawValue().id;
    }

    saveDeliveryMethod() {
        if (this.form.invalid) {
            return;
        }

        const deliveryMethod = {
            id: this.form.getRawValue().id,
            name: this.form.getRawValue().name,
            type: this.form.getRawValue().courierType.type,
            isActive: this.form.getRawValue().isActive,
            price: this.form.getRawValue().price,
            isFreeForOrderAbove: this.form.getRawValue().isFreeForOrderAbove,
            freeForOrderAbove: this.form.getRawValue().freeForOrderAbove
        };

        const observable = this.isEditing
            ? this.deliveryMethodService.updateDeliveryMethod(deliveryMethod.id, deliveryMethod)
            : this.deliveryMethodService.createDeliveryMethod(deliveryMethod);

        observable.subscribe({
            next: async (id) => {
                try {
                    await this.toastService.showSuccessToast('Úspěch', 'Dopravní metoda byla úspěšně uložena.');
                    await this.router.navigate(['admin/dopravni-metody/upravit/' + id]);
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

    deleteDeliveryMethod() {
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
                this.deliveryMethodService.deleteDeliveryMethodById(this.form.getRawValue().id).subscribe({
                    next: async () => {
                        try {
                            await this.toastService.showSuccessToast('Úspěch', 'Dopravní metoda byla úspěšně odstraněna.');
                            await this.router.navigate(['/admin/dopravni-metody']);
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
