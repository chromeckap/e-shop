import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {TreeNode} from "primeng/api";
import {CategoryService} from "../../../../../services/services/category.service";
import {CategoryOverviewResponse} from "../../../../../services/models/category/category-overview-response";
import {Divider} from "primeng/divider";
import {FormGroup, ReactiveFormsModule} from "@angular/forms";
import {TreeTableModule} from "primeng/treetable";
import {takeUntil} from "rxjs/operators";
import {Subject} from "rxjs";
import {RefreshService} from "../../services/refresh.service";

@Component({
    selector: 'app-category-treeselect',
    imports: [
        Divider,
        ReactiveFormsModule,
        TreeTableModule
    ],
    templateUrl: './category-treeselect.component.html',
    standalone: true,
    styleUrl: './category-treeselect.component.scss'
})
export class CategoryTreeSelectComponent implements OnInit, OnDestroy {
    @Input() form!: FormGroup;
    categoriesTree: TreeNode[] = [];
    categories: CategoryOverviewResponse[] = [];
    selectionKeys: { [key: string]: { checked?: boolean; partialChecked?: boolean } } = {};

    private destroy$ = new Subject<void>();

    constructor(
        private categoryService: CategoryService,
        private refreshService: RefreshService
    ) {}

    ngOnInit(): void {
        this.getAllCategories();

        this.refreshService.refresh$
            .pipe(takeUntil(this.destroy$))
            .subscribe(() => {
                this.categoriesTree = this.transformCategories(this.categories);
            });
    }

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();
    }

    private getAllCategories() {
        this.categoryService.getAllCategories().subscribe({
            next: (categories) => {
                this.categoriesTree = this.transformCategories(categories);
                this.categories = categories;
            }
        });
    }

    transformCategories(categories: Array<CategoryOverviewResponse>, parentId: string | null = null): TreeNode[] {
        const categoryIds = this.form.get('categoryIds')?.value;

        return categories.map((category, index) => {
            const key = parentId !== null ? `${parentId}-${index}` : `${index}`;
            const isSelected = categoryIds.includes(category.id);

            if (isSelected) {
                this.selectionKeys[key] = {checked: true };
            }

            const children = category.children && category.children.length > 0
                ? this.transformCategories(category.children, key)
                : undefined;

            if (children && children.length > 0) {
                const hasSelectedChild = children.some(child => this.selectionKeys[child.key!]?.checked);
                if (hasSelectedChild) {
                    this.selectionKeys[key] = { partialChecked: true };
                }
            }

            return {
                key,
                data: {
                    id: category.id,
                    name: category.name,
                    parentId: parentId
                },
                children
            };
        });
    }

    getSelectedCategoryIds(): number[] {
        const getSelectedIdsRecursive = (rows: any[]): number[] =>
            rows.reduce((selectedIds, row) => [
                ...selectedIds,
                ...(this.selectionKeys[row.key]?.checked || this.selectionKeys[row.key]?.partialChecked ? [row.data.id] : []),
                ...getSelectedIdsRecursive(row.children || [])
            ], []);

        return getSelectedIdsRecursive(this.categoriesTree);
    }

}
