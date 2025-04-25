import {Component, OnInit} from '@angular/core';
import {ValueListComponent} from "../../components/value-list/value-list.component";
import {OverviewComponent} from "../../components/overview/overview.component";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {AttributeService} from "../../../../../services/services/attribute.service";
import {ConfirmationService} from "primeng/api";
import {ActivatedRoute, Router} from "@angular/router";
import {AttributeValueResponse} from "../../../../../services/models/attribute/attribute-value-response";
import {Toolbar} from "primeng/toolbar";
import {NgClass} from "@angular/common";
import {Button} from "primeng/button";
import {ConfirmDialog} from "primeng/confirmdialog";
import {ToastService} from "../../../../../shared/services/toast.service";

@Component({
    selector: 'app-manage-attribute',
    imports: [
        ValueListComponent,
        OverviewComponent,
        ConfirmDialog,
        Toolbar,
        NgClass,
        Button,
    ],
    templateUrl: './manage-attribute.component.html',
    standalone: true,
    styleUrl: './manage-attribute.component.scss',
    providers: [ConfirmationService]
})
export class ManageAttributeComponent implements OnInit {
    attributeValues: Array<AttributeValueResponse> = [];
    form: FormGroup;

    constructor(
        private attributeService: AttributeService,
        private confirmationService: ConfirmationService,
        private toastService: ToastService,
        private formBuilder: FormBuilder,
        private router: Router,
        private activatedRoute: ActivatedRoute
    ) {
        this.form = this.formBuilder.group({
            id: [{ value: null, disabled: true }],
            name: ['', Validators.required]
        });
    }

    ngOnInit(): void {
        const id = this.activatedRoute.snapshot.params['id'];
        if (id) {
            this.attributeService.getAttributeById(id)
                .subscribe(attribute => {
                    this.form.patchValue(attribute);
                    this.attributeValues = [...attribute.values!]
                });
        }
    }

    get isEditing() {
        return this.form.getRawValue().id;
    }

    saveAttribute() {
        if (!this.form.valid) {
            return;
        }

        const attribute = {
            id: this.form.getRawValue().id,
            name: this.form.getRawValue().name,
            values: this.attributeValues
        };

        const observable = this.isEditing
            ? this.attributeService.updateAttribute(attribute.id, attribute)
            : this.attributeService.createAttribute(attribute);

        observable.subscribe({
            next: async (id) => {
                try {
                    await this.toastService.showSuccessToast('Úspěch', 'Atribut byl úspěšně uložen.');
                    await this.router.navigate(['admin/atributy/upravit/' + id]);
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

    deleteAttribute() {
        this.confirmationService.confirm({
            header: 'Potvrzení odstranění',
            icon: 'pi pi-exclamation-triangle',
            message: 'Opravdu chceš odstranit atribut ' + this.form.getRawValue().name + "?",
            acceptLabel: 'Ano',
            acceptIcon: 'pi pi-check',
            acceptButtonStyleClass: 'p-button-danger p-button-outlined',
            rejectLabel: 'Ne',
            rejectIcon: 'pi pi-check',
            rejectButtonStyleClass: 'p-button-secondary p-button-outlined',

            accept: () => {
                this.attributeService.deleteAttributeById(this.form.getRawValue().id).subscribe({
                    next: async () => {
                        try {
                            await this.toastService.showSuccessToast('Úspěch', 'Atribut byl úspěšně odstraněn.');
                            await this.router.navigate(['/admin/atributy']);
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
