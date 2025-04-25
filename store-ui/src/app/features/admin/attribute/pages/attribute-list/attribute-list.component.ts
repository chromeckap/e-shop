import {Component, OnInit} from '@angular/core';
import {AttributeResponse} from "../../../../../services/models/attribute/attribute-response";
import {AttributeService} from "../../../../../services/services/attribute.service";
import {ConfirmationService} from "primeng/api";
import {Router} from "@angular/router";
import {forkJoin, Observable} from "rxjs";
import {Table, TableModule} from "primeng/table";
import {Toolbar} from "primeng/toolbar";
import {Button} from "primeng/button";
import {IconField} from "primeng/iconfield";
import {InputText} from "primeng/inputtext";
import {ConfirmDialog} from "primeng/confirmdialog";
import {ToastService} from "../../../../../shared/services/toast.service";

@Component({
    selector: 'app-attribute-list',
    imports: [
        Toolbar,
        Button,
        TableModule,
        IconField,
        InputText,
        ConfirmDialog
    ],
    templateUrl: './attribute-list.component.html',
    styleUrl: './attribute-list.component.scss',
    standalone: true,
    providers: [ConfirmationService]
})
export class AttributeListComponent implements OnInit {
    attributes: Array<AttributeResponse> = [];
    selectedAttributes: Array<AttributeResponse> = [];

    constructor(
        private attributeService: AttributeService,
        private confirmationService: ConfirmationService,
        private toastService: ToastService,
        private router: Router
    ) {}

    ngOnInit(): void {
        this.getAllAttributes();
    }

    private getAllAttributes() {
        this.attributeService.getAllAttributes()
            .subscribe({
                next: (attributes) => {
                    this.attributes = attributes;
                }
            });
    }

    createAttribute() {
        this.router.navigate(['admin/atributy/vytvorit'])
            .catch(error => {
                console.log("Při navigaci došlo k chybě: ", error);
            });
    }

    editAttribute(attribute: AttributeResponse) {
        this.router.navigate(['admin/atributy/upravit', attribute.id])
            .catch(error => {
                console.log("Při navigaci došlo k chybě: ", error);
            });
    }

    deleteAttribute(attribute: AttributeResponse) {
        this.confirmationService.confirm({
            header: 'Potvrzení odstranění',
            icon: 'pi pi-exclamation-triangle',
            message: 'Opravdu chceš odstranit atribut ' + attribute.name + "?",
            acceptLabel: 'Ano',
            acceptIcon: 'pi pi-check',
            acceptButtonStyleClass: 'p-button-danger p-button-outlined',
            rejectLabel: 'Ne',
            rejectIcon: 'pi pi-check',
            rejectButtonStyleClass: 'p-button-secondary p-button-outlined',

            accept: () => {
                this.attributeService.deleteAttributeById(attribute.id!).subscribe({
                    next: async () => {
                        try {
                            this.getAllAttributes();
                            await this.toastService.showSuccessToast('Úspěch', 'Atribut byl úspěšně odstraněn.');
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

    deleteSelectedAttributes() {
        this.confirmationService.confirm({
            header: 'Potvrzení odstranění',
            icon: 'pi pi-exclamation-triangle',
            message: 'Opravdu chceš odstranit vybrané atributy?',
            acceptLabel: 'Ano',
            acceptIcon: 'pi pi-check',
            acceptButtonStyleClass: 'p-button-danger p-button-outlined',
            rejectLabel: 'Ne',
            rejectIcon: 'pi pi-check',
            rejectButtonStyleClass: 'p-button-secondary p-button-outlined',

            accept: () => {
                const deleteOperations: { [key: string]: Observable<any> } = {};
                this.selectedAttributes.forEach(attribute => {
                    if (attribute.id) {
                        deleteOperations[`attribute-${attribute.id}`] = this.attributeService.deleteAttributeById(attribute.id);
                    }
                });
                forkJoin(deleteOperations).subscribe({
                    next: async () => {
                        try {
                            this.getAllAttributes();
                            this.selectedAttributes = [];
                            await this.toastService.showSuccessToast('Úspěch', 'Atributy byly úspěšně odstraněny.');
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
