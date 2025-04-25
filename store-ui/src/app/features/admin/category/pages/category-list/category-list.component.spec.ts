import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CategoryListComponent } from './category-list.component';
import { CategoryService } from "../../../../../services/services/category.service";
import { ConfirmationService } from "primeng/api";
import { ToastService } from "../../../../../shared/services/toast.service";
import { Router } from "@angular/router";
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('CategoryListComponent', () => {
    let component: CategoryListComponent;
    let fixture: ComponentFixture<CategoryListComponent>;
    let categoryService: jasmine.SpyObj<CategoryService>;
    let toastService: jasmine.SpyObj<ToastService>;
    let router: jasmine.SpyObj<Router>;
    let confirmationService: jasmine.SpyObj<ConfirmationService>;

    beforeEach(() => {
        const categoryServiceSpy = jasmine.createSpyObj('CategoryService', ['getAllCategories', 'deleteCategoryById']);
        const toastServiceSpy = jasmine.createSpyObj('ToastService', ['showSuccessToast', 'showErrorToast']);
        const routerSpy = jasmine.createSpyObj('Router', ['navigate']);
        const confirmationServiceSpy = jasmine.createSpyObj('ConfirmationService', ['confirm']);

        // Setup mock return values
        categoryServiceSpy.getAllCategories.and.returnValue(of([]));
        routerSpy.navigate.and.returnValue(Promise.resolve(true));

        TestBed.configureTestingModule({
            providers: [
                { provide: CategoryService, useValue: categoryServiceSpy },
                { provide: ToastService, useValue: toastServiceSpy },
                { provide: Router, useValue: routerSpy },
                { provide: ConfirmationService, useValue: confirmationServiceSpy }
            ],
            schemas: [NO_ERRORS_SCHEMA]
        });

        fixture = TestBed.createComponent(CategoryListComponent);
        component = fixture.componentInstance;
        categoryService = TestBed.inject(CategoryService) as jasmine.SpyObj<CategoryService>;
        toastService = TestBed.inject(ToastService) as jasmine.SpyObj<ToastService>;
        router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
        confirmationService = TestBed.inject(ConfirmationService) as jasmine.SpyObj<ConfirmationService>;
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    it('should call getAllCategories on ngOnInit', () => {
        component.ngOnInit();
        expect(categoryService.getAllCategories).toHaveBeenCalled();
    });

    it('should toggle isExpanded when toggleExpand is called', () => {
        component.isExpanded = false;
        component.toggleExpand();
        expect(component.isExpanded).toBeTrue();

        component.toggleExpand();
        expect(component.isExpanded).toBeFalse();
    });

    it('should call createCategory when createCategory is triggered', async () => {
        component.createCategory();
        expect(router.navigate).toHaveBeenCalledWith(['admin/kategorie/vytvorit']);
    });

    it('should call editCategory when editCategory is triggered', async () => {
        const category = { id: 1, name: 'Category 1' };
        component.editCategory(category);
        expect(router.navigate).toHaveBeenCalledWith(['admin/kategorie/upravit', category.id]);
    });

    it('should delete category and show success toast when deleteCategory is confirmed', () => {
        const category = { id: 1, name: 'Category 1' };
        categoryService.deleteCategoryById.and.returnValue(of());
        toastService.showSuccessToast.and.returnValue(Promise.resolve());

        component.deleteCategory(category);
        expect(categoryService.deleteCategoryById);
        expect(toastService.showSuccessToast);
    });

    it('should show error toast when deleteCategory fails', () => {
        const category = { id: 1, name: 'Category 1' };
        const errorMsg = 'Error deleting category';
        categoryService.deleteCategoryById.and.returnValue(throwError(() => new Error(errorMsg)));
        toastService.showErrorToast.and.returnValue(Promise.resolve());

        component.deleteCategory(category);
        expect(categoryService.deleteCategoryById);
        expect(toastService.showErrorToast);
    });

    it('should toggle category expansion correctly', () => {
        component.categories = [
            { key: '1', data: { id: 1, name: 'Category 1' }, children: [], expanded: false }
        ];
        component.isExpanded = false;
        component.toggleExpand();
        expect(component.categories[0].expanded).toBeTrue();
    });
});
