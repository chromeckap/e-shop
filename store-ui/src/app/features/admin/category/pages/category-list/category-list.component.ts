import {Component, OnInit} from '@angular/core';
import {ConfirmationService, TreeNode} from "primeng/api";
import {Router} from "@angular/router";
import {CategoryService} from "../../../../../services/services/category.service";
import {CategoryOverviewResponse} from "../../../../../services/models/category/category-overview-response";
import {CategoryResponse} from "../../../../../services/models/category/category-response";
import {Toolbar} from "primeng/toolbar";
import {Button} from "primeng/button";
import {TreeTableModule} from "primeng/treetable";
import {ConfirmDialog} from "primeng/confirmdialog";
import {ToastService} from "../../../../../shared/services/toast.service";

@Component({
    selector: 'app-category-list',
    imports: [
        Toolbar,
        Button,
        TreeTableModule,
        ConfirmDialog
    ],
    templateUrl: './category-list.component.html',
    standalone: true,
    styleUrl: './category-list.component.scss',
    providers: [ConfirmationService]
})
export class CategoryListComponent implements OnInit {
    categories: TreeNode[] = [];
    isExpanded = false;

    constructor(
        private categoryService: CategoryService,
        private confirmationService: ConfirmationService,
        private toastService: ToastService,
        private router: Router
    ) {}

    ngOnInit(): void {
        this.getAllCategories();
    }

    private getAllCategories() {
        this.categoryService.getAllCategories()
            .subscribe({
                next: (categories) => {
                    this.categories = this.transformCategories(categories);
                    this.toggleExpand();
                }
            });
    }

    private transformCategories(categories: Array<CategoryOverviewResponse>, parentId: number | null = null): TreeNode[] {
        let keyCount = 1;
        return categories.map(category => {
            const key = keyCount++;
            return {
                key: key.toString(),
                data: {
                    id: category.id,
                    name: category.name,
                    parentId: parentId
                },
                children: category.children && category.children.length > 0
                    ? this.transformCategories(category.children, category.id)
                    : undefined
            };
        });
    }

    toggleExpand() {
        this.isExpanded = !this.isExpanded;
        this.expandAll(this.categories, this.isExpanded);
        this.categories = [...this.categories];
    }

    private expandAll(categories: TreeNode[], expand: boolean) {
        categories.forEach(category => {
            category.expanded = expand;
            if (category.children)
                this.expandAll(category.children, expand);
        });
    }

    createCategory() {
        this.router.navigate(['admin/kategorie/vytvorit'])
            .catch(error => {
                console.log("Při navigaci došlo k chybě: ", error);
            });
    }

    editCategory(category: CategoryResponse) {
        this.router.navigate(['admin/kategorie/upravit', category.id])
            .catch(error => {
                console.log("Při navigaci došlo k chybě: ", error);
            });
    }

    deleteCategory(category: CategoryOverviewResponse) {
        this.confirmationService.confirm({
            header: 'Potvrzení odstranění',
            icon: 'pi pi-exclamation-triangle',
            message: 'Opravdu chceš odstranit kategorii ' + category.name + "?",
            acceptLabel: 'Ano',
            acceptIcon: 'pi pi-check',
            acceptButtonStyleClass: 'p-button-danger p-button-outlined',
            rejectLabel: 'Ne',
            rejectIcon: 'pi pi-check',
            rejectButtonStyleClass: 'p-button-secondary p-button-outlined',

            accept: () => {
                this.categoryService.deleteCategoryById(category.id!)
                    .subscribe({
                        next: async () => {
                            try {
                                await this.toastService.showSuccessToast('Úspěch', 'Kategorie byla úspěšně odstraněna.');
                                this.categories = this.removeCategoryNode(this.categories, category.id!);
                            } catch (error) {
                                console.log("Chyba při zobrazení toastu:", error);
                            }
                        },
                        error: async (error) => {
                            try {
                                await this.toastService.showErrorToast('Chyba', error.error.detail);
                            } catch (toastError) {
                                console.log("Chyba při zobrazení chybového toastu:", toastError);
                            }
                            console.log(error);
                        }
                    });
            }
        });
    }

    private removeCategoryNode(categories: TreeNode[], id: number): TreeNode[] {
        let newCategoryNodes: TreeNode[] = [];

        categories.forEach(category => {
            if (category.data.id === id) {
                if (category.children)
                    newCategoryNodes.push(...category.children);
            } else {
                let updatedChildren = category.children
                    ? this.removeCategoryNode(category.children, id)
                    : [];
                newCategoryNodes.push({
                    ...category,
                    children: updatedChildren
                });
            }
        });

        return newCategoryNodes;
    }
}
