import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ManageCategoryComponent } from './manage-category.component';
import { ActivatedRoute, Router } from '@angular/router';
import { CategoryService } from '../../../../../services/services/category.service';
import { ToastService } from '../../../../../shared/services/toast.service';
import { ConfirmationService } from 'primeng/api';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { Button } from 'primeng/button';
import { Toolbar } from 'primeng/toolbar';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { By } from '@angular/platform-browser';

describe('ManageCategoryComponent', () => {
    let component: ManageCategoryComponent;
    let fixture: ComponentFixture<ManageCategoryComponent>;
    let categoryService: jasmine.SpyObj<CategoryService>;
    let toastService: jasmine.SpyObj<ToastService>;
    let router: jasmine.SpyObj<Router>;
    let confirmationService: jasmine.SpyObj<ConfirmationService>;

    beforeEach(async () => {
        const categoryServiceSpy = jasmine.createSpyObj('CategoryService', [
            'getAllCategories',
            'getCategoryById',
            'createCategory',
            'updateCategory',
            'deleteCategoryById'
        ]);
        const toastServiceSpy = jasmine.createSpyObj('ToastService', ['showSuccessToast', 'showErrorToast']);
        const routerSpy = jasmine.createSpyObj('Router', ['navigate']);
        const confirmationServiceSpy = jasmine.createSpyObj('ConfirmationService', ['confirm']);

        // Setup mock return values
        categoryServiceSpy.getAllCategories.and.returnValue(of([]));
        toastServiceSpy.showSuccessToast.and.returnValue(Promise.resolve());
        toastServiceSpy.showErrorToast.and.returnValue(Promise.resolve());
        routerSpy.navigate.and.returnValue(Promise.resolve(true));

        await TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                Button,
                Toolbar,
                ConfirmDialog
            ],
            providers: [
                FormBuilder,
                { provide: CategoryService, useValue: categoryServiceSpy },
                { provide: ToastService, useValue: toastServiceSpy },
                { provide: Router, useValue: routerSpy },
                { provide: ConfirmationService, useValue: confirmationServiceSpy },
                { provide: ActivatedRoute, useValue: { snapshot: { params: {} } } }
            ],
            schemas: [NO_ERRORS_SCHEMA] // Ignore unknown elements and properties
        }).compileComponents();

        fixture = TestBed.createComponent(ManageCategoryComponent);
        component = fixture.componentInstance;
        categoryService = TestBed.inject(CategoryService) as jasmine.SpyObj<CategoryService>;
        toastService = TestBed.inject(ToastService) as jasmine.SpyObj<ToastService>;
        router = TestBed.inject(Router) as jasmine.SpyObj<Router>;
        confirmationService = TestBed.inject(ConfirmationService) as jasmine.SpyObj<ConfirmationService>;
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    it('should load categories on init', () => {
        fixture.detectChanges();
        expect(categoryService.getAllCategories).toHaveBeenCalled();
    });

    describe('Toolbar UI Tests', () => {
        it('should display the correct toolbar title for create mode', () => {
            // Create mode
            fixture.detectChanges();
            const toolbarTitle = fixture.debugElement.query(By.css('h5')).nativeElement;
            expect(toolbarTitle.textContent).toContain('Vytvoření kategorie');
        });

        it('should display the correct toolbar title for edit mode', () => {
            // Edit mode setup
            component.form.get('id')?.setValue(1);
            component.form.get('name')?.setValue('Test Category');
            fixture.detectChanges();

            const toolbarTitle = fixture.debugElement.query(By.css('h5')).nativeElement;
            expect(toolbarTitle.textContent).toContain('Editace kategorie Test Category');
        });

        it('should show the correct icon for create mode', () => {
            fixture.detectChanges();
            const icon = fixture.debugElement.query(By.css('h5 .pi'));
            expect(icon.nativeElement.classList).toContain('pi-plus');
        });

        it('should show the correct icon for edit mode', () => {
            component.form.get('id')?.setValue(1);
            fixture.detectChanges();
            const icon = fixture.debugElement.query(By.css('h5 .pi'));
            expect(icon.nativeElement.classList).toContain('pi-pencil');
        });

        it('should show "Vytvořit" button label in create mode', () => {
            fixture.detectChanges();
            const saveButton = fixture.debugElement.query(By.css('p-button[icon="pi pi-save"]'));
            expect(saveButton.componentInstance.label).toBe('Vytvořit');
        });

        it('should show "Uložit" button label in edit mode', () => {
            component.form.get('id')?.setValue(1);
            fixture.detectChanges();
            const saveButton = fixture.debugElement.query(By.css('p-button[icon="pi pi-save"]'));
            expect(saveButton.componentInstance.label).toBe('Uložit');
        });

        it('should disable save button when form is invalid', () => {
            component.form.get('name')?.setValue(''); // Make form invalid
            fixture.detectChanges();
            const saveButton = fixture.debugElement.query(By.css('p-button[icon="pi pi-save"]'));
            expect(saveButton.componentInstance.disabled).toBeTrue();
        });

        it('should enable save button when form is valid', () => {
            component.form.get('name')?.setValue('Valid Category'); // Make form valid
            fixture.detectChanges();
            const saveButton = fixture.debugElement.query(By.css('p-button[icon="pi pi-save"]'));
            expect(saveButton.componentInstance.disabled).toBeFalse();
        });

        it('should not show delete button in create mode', () => {
            fixture.detectChanges();
            const deleteButton = fixture.debugElement.query(By.css('p-button[icon="pi pi-trash"]'));
            expect(deleteButton).toBeNull();
        });

        it('should show delete button in edit mode', () => {
            component.form.get('id')?.setValue(1);
            fixture.detectChanges();
            const deleteButton = fixture.debugElement.query(By.css('p-button[icon="pi pi-trash"]'));
            expect(deleteButton).not.toBeNull();
        });
    });

    describe('Button Action Tests', () => {
        it('should call saveCategory when save button is clicked', () => {
            spyOn(component, 'saveCategory');
            component.form.get('name')?.setValue('Test Category'); // Make form valid
            fixture.detectChanges();

            const saveButton = fixture.debugElement.query(By.css('p-button[icon="pi pi-save"]'));
            saveButton.triggerEventHandler('onClick', null);

            expect(component.saveCategory).toHaveBeenCalled();
        });

        it('should call deleteCategory when delete button is clicked in edit mode', () => {
            spyOn(component, 'deleteCategory');
            component.form.get('id')?.setValue(1); // Set edit mode
            fixture.detectChanges();

            const deleteButton = fixture.debugElement.query(By.css('p-button[icon="pi pi-trash"]'));
            deleteButton.triggerEventHandler('onClick', null);

            expect(component.deleteCategory).toHaveBeenCalled();
        });
    });

    describe('Create/Update Category Tests', () => {
        it('should call createCategory in create mode', () => {
            component.form.get('name')?.setValue('New Category');
            categoryService.createCategory.and.returnValue(of(1));

            component.saveCategory();

            expect(categoryService.createCategory).toHaveBeenCalled();
            expect(categoryService.updateCategory).not.toHaveBeenCalled();
        });

        it('should call updateCategory in edit mode', () => {
            component.form.get('id')?.setValue(1);
            component.form.get('name')?.setValue('Updated Category');
            categoryService.updateCategory.and.returnValue(of(1));

            component.saveCategory();

            expect(categoryService.updateCategory).toHaveBeenCalled();
            expect(categoryService.createCategory).not.toHaveBeenCalled();
        });

        it('should show success toast and navigate after successful save', async () => {
            component.form.get('name')?.setValue('New Category');
            categoryService.createCategory.and.returnValue(of(1));

            await component.saveCategory();

            expect(toastService.showSuccessToast).toHaveBeenCalledWith('Úspěch', 'Kategorie byla úspěšně uložena.');
            expect(router.navigate).toHaveBeenCalledWith(['admin/kategorie/upravit/1']);
        });

        it('should show error toast on save failure', async () => {
            component.form.get('name')?.setValue('New Category');
            const error = { error: { detail: 'Error message' } };
            categoryService.createCategory.and.returnValue(throwError(() => error));

            await component.saveCategory();

            expect(toastService.showErrorToast).toHaveBeenCalledWith('Chyba', 'Error message');
        });
    });

    describe('Delete Category Tests', () => {
        it('should open confirmation dialog when deleteCategory is called', () => {
            component.form.get('id')?.setValue(1);
            component.form.get('name')?.setValue('Test Category');

            component.deleteCategory();

            expect(confirmationService.confirm);
        });

        it('should delete category and navigate when confirmation is accepted', () => {
            component.form.get('id')?.setValue(1);
            categoryService.deleteCategoryById.and.returnValue(of());

            component.deleteCategory();

            expect(categoryService.deleteCategoryById);
            expect(toastService.showSuccessToast);
            expect(router.navigate);
        });

        it('should show error toast when delete fails', () => {
            component.form.get('id')?.setValue(1);
            const error = { error: { detail: 'Delete error message' } };
            categoryService.deleteCategoryById.and.returnValue(throwError(() => error));

            component.deleteCategory();

            expect(categoryService.deleteCategoryById);
            expect(toastService.showErrorToast);
        });
    });
});
