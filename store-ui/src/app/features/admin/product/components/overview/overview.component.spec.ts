import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OverviewComponent } from './overview.component';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Divider } from 'primeng/divider';
import { FloatLabel } from 'primeng/floatlabel';
import { InputText } from 'primeng/inputtext';
import { Editor } from 'primeng/editor';
import { Component, ViewChild } from '@angular/core';
import { By } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

// Create a test host component to provide the form
@Component({
    selector: 'app-test-host',
    standalone: true,
    imports: [OverviewComponent, ReactiveFormsModule],
    template: `
    <app-overview [form]="form"></app-overview>
  `
})
class TestHostComponent {
    @ViewChild(OverviewComponent) overviewComponent!: OverviewComponent;
    form!: FormGroup;

    constructor(private fb: FormBuilder) {
        this.createForm();
    }

    createForm() {
        this.form = this.fb.group({
            id: [{ value: '123', disabled: true }],
            name: ['', Validators.required],
            description: ['']
        });
    }
}

describe('OverviewComponent', () => {
    let hostComponent: TestHostComponent;
    let hostFixture: ComponentFixture<TestHostComponent>;
    let component: OverviewComponent;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                NoopAnimationsModule,
                TestHostComponent,
                OverviewComponent,
                Divider,
                FloatLabel,
                InputText,
                Editor
            ]
        }).compileComponents();

        hostFixture = TestBed.createComponent(TestHostComponent);
        hostComponent = hostFixture.componentInstance;
        hostFixture.detectChanges();
        component = hostComponent.overviewComponent;
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should receive form from parent component', () => {
        expect(component.form).toBeTruthy();
        expect(component.form.get('id')).toBeTruthy();
        expect(component.form.get('name')).toBeTruthy();
        expect(component.form.get('description')).toBeTruthy();
    });

    it('should render all form controls', () => {
        // Check if all form controls are rendered
        const idInput = hostFixture.debugElement.query(By.css('#id'));
        const nameInput = hostFixture.debugElement.query(By.css('#name'));
        const descriptionEditor = hostFixture.debugElement.query(By.css('#description'));

        expect(idInput).toBeTruthy();
        expect(nameInput).toBeTruthy();
        expect(descriptionEditor).toBeTruthy();
    });

    it('should have ID field disabled', () => {
        const idInput = hostFixture.debugElement.query(By.css('#id'));
        expect(idInput.nativeElement.disabled).toBeTrue();
    });

    it('should not display validation error initially', () => {
        const errorElement = hostFixture.debugElement.query(By.css('.text-red-500'));
        expect(errorElement).toBeNull();
    });

    it('should display validation error when name is touched and invalid', () => {
        // Mark the control as touched
        const nameControl = component.form.get('name');
        nameControl?.markAsTouched();
        hostFixture.detectChanges();

        // Now the error should be visible
        const errorElement = hostFixture.debugElement.query(By.css('.text-red-500'));
        expect(errorElement).toBeTruthy();
        expect(errorElement.nativeElement.textContent).toContain('Název produktu je povinný');
    });

    it('should not display validation error when name is valid', () => {
        // Set a valid value for name
        component.form.get('name')?.setValue('Test Product');
        component.form.get('name')?.markAsTouched();
        hostFixture.detectChanges();

        // Error should not be visible
        const errorElement = hostFixture.debugElement.query(By.css('.text-red-500'));
        expect(errorElement).toBeNull();
    });

    it('should update form value when name is entered', () => {
        const testName = 'Test Product';

        // Set value programmatically
        component.form.get('name')?.setValue(testName);
        hostFixture.detectChanges();

        // Check if value was updated
        expect(component.form.get('name')?.value).toBe(testName);
    });

    it('should update form value when description is entered', () => {
        const testDescription = '<p>This is a test description</p>';

        // Set value programmatically
        component.form.get('description')?.setValue(testDescription);
        hostFixture.detectChanges();

        // Check if value was updated
        expect(component.form.get('description')?.value).toBe(testDescription);
    });

    it('should bind form controls to form inputs correctly', () => {
        const idInput = hostFixture.debugElement.query(By.css('#id'));
        const nameInput = hostFixture.debugElement.query(By.css('#name'));

        // Check for formControlName attribute
        expect(idInput.attributes['formControlName']).toBe('id');
        expect(nameInput.attributes['formControlName']).toBe('name');

        // For the editor component, we check it's bound using the p-editor directive
        const editorElement = hostFixture.debugElement.query(By.css('p-editor'));
        expect(editorElement.attributes['formControlName']).toBe('description');
    });

    it('should make the form valid when required fields are filled', () => {
        // Initially form should be invalid
        expect(component.form.valid).toBeFalse();

        // Fill required field
        component.form.patchValue({
            name: 'Test Product'
        });

        // Now form should be valid
        expect(component.form.valid).toBeTrue();
    });

    it('should render section title and description', () => {
        const title = hostFixture.debugElement.query(By.css('h2'));
        const description = hostFixture.debugElement.query(By.css('p.text-gray-500'));

        expect(title.nativeElement.textContent).toBe('Základní informace');
        expect(description.nativeElement.textContent).toBe('Základní informace o produktu.');
    });
});
