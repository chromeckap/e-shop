import {Component, OnInit} from '@angular/core';
import {ConfirmationService} from "primeng/api";
import {CategoryOverviewResponse} from "../../../../../services/models/category/category-overview-response";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {CategoryService} from "../../../../../services/services/category.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Button} from "primeng/button";
import {NgClass} from "@angular/common";
import {Toolbar} from "primeng/toolbar";
import {ConfirmDialog} from "primeng/confirmdialog";
import {OverviewComponent} from "../../components/overview/overview.component";
import {ParentSelectComponent} from "../../components/parent-select/parent-select.component";
import {ToastService} from "../../../../../shared/services/toast.service";

@Component({
    selector: 'app-manage-category',
    imports: [
        Button,
        Toolbar,
        NgClass,
        ConfirmDialog,
        OverviewComponent,
        ParentSelectComponent
    ],
    templateUrl: './manage-category.component.html',
    standalone: true,
    styleUrl: './manage-category.component.scss',
    providers: [ConfirmationService]
})
export class ManageCategoryComponent implements OnInit {
    categories: CategoryOverviewResponse[] = [];
    form: FormGroup;

    constructor(
        private categoryService: CategoryService,
        private confirmationService: ConfirmationService,
        private toastService: ToastService,
        private formBuilder: FormBuilder,
        private router: Router,
        private activatedRoute: ActivatedRoute
    ) {
        this.form = this.formBuilder.group({
            id: [{ value: null, disabled: true }],
            name: ['', Validators.required],
            description: [''],
            parent: [null]
        });
    }

    ngOnInit(): void {
        this.loadData();
    }

    private loadData() {
        this.categoryService.getAllCategories()
            .subscribe({
                next: (categories) => {
                    this.categories = this.convertToTreeFormat(categories);

                    const id = this.activatedRoute.snapshot.params['id'];
                    if (id) {
                        this.categoryService.getCategoryById(id)
                            .subscribe(category => {
                                this.form.patchValue(category);

                                const parentId = this.form.getRawValue().parent?.id;
                                const node = parentId ? this.findTreeSelectedCategories(this.categories, parentId) : null;
                                if (node) {
                                    this.form.get('parent')?.setValue(node);
                                }
                            });
                    }
                }
            });
    }

    get isEditing() {
        return this.form.getRawValue().id;
    }

    private findTreeSelectedCategories(nodes: any[], value: number): any | null {
        for (const node of nodes) {
            if (node.value === value) {
                return node;
            }
            if (node.children && node.children.length > 0) {
                const found = this.findTreeSelectedCategories(node.children, value);
                if (found) {
                    return found;
                }
            }
        }
        return null;
    }

    private convertToTreeFormat(categories: CategoryOverviewResponse[]): any[] {
        return categories.map(category => ({
            label: category.name,
            value: category.id,
            children: category.children ? this.convertToTreeFormat(category.children) : []
        }));
    }

    saveCategory() {
        if (this.form.invalid) {
            return;
        }

        const category = {
            id: this.form.getRawValue().id,
            name: this.form.getRawValue().name,
            description: this.form.getRawValue().description,
            parentId: this.form.getRawValue().parent?.value || null
        };

        const observable = this.isEditing
            ? this.categoryService.updateCategory(category.id, category)
            : this.categoryService.createCategory(category);

        observable.subscribe({
            next: async (id) => {
                try {
                    await this.toastService.showSuccessToast('Úspěch', 'Kategorie byla úspěšně uložena.');
                    await this.router.navigate(['admin/kategorie/upravit/' + id]);
                } catch (error) {
                    console.log("Chyba při zobrazení toastu:", error);
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

    deleteCategory() {
        this.confirmationService.confirm({
            header: 'Potvrzení odstranění',
            icon: 'pi pi-exclamation-triangle',
            message: 'Opravdu chceš odstranit kategorii ' + this.form.getRawValue().name + "?",
            acceptLabel: 'Ano',
            acceptIcon: 'pi pi-check',
            acceptButtonStyleClass: 'p-button-danger p-button-outlined',
            rejectLabel: 'Ne',
            rejectIcon: 'pi pi-check',
            rejectButtonStyleClass: 'p-button-secondary p-button-outlined',

            accept: () => {
                this.categoryService.deleteCategoryById(this.form.getRawValue().id).subscribe({
                    next: async () => {
                        try {
                            await this.toastService.showSuccessToast('Úspěch', 'Kategorie byla úspěšně odstraněna.');
                            await this.router.navigate(['admin/kategorie']);
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
