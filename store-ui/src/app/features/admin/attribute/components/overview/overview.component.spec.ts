import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OverviewComponent } from './overview.component';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { FloatLabel } from 'primeng/floatlabel';
import { InputText } from 'primeng/inputtext';
import { Divider } from 'primeng/divider';

describe('OverviewComponent', () => {
    let component: OverviewComponent;
    let fixture: ComponentFixture<OverviewComponent>;
    let formBuilder: FormBuilder;
    let testForm: FormGroup;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [
                OverviewComponent,
                ReactiveFormsModule,
                FloatLabel,
                InputText,
                Divider
            ],
            providers: [
                FormBuilder
            ],
            schemas: [NO_ERRORS_SCHEMA] // For PrimeNG components
        })
            .compileComponents();

        formBuilder = TestBed.inject(FormBuilder);

        // Create a test form to pass to the component
        testForm = formBuilder.group({
            id: [{ value: 'test-id-123', disabled: true }],
            name: ['', Validators.required]
        });

        fixture = TestBed.createComponent(OverviewComponent);
        component = fixture.componentInstance;

        // Set the input form before change detection
        component.form = testForm;

        fixture.detectChanges();
    });

    it('should display the correct headings', () => {
        const h2Element = fixture.debugElement.query(By.css('h2'));
        const pElement = fixture.debugElement.query(By.css('p.text-gray-500'));

        expect(h2Element).toBeTruthy();
        expect(h2Element.nativeElement.textContent).toBe('Základní informace');

        expect(pElement).toBeTruthy();
        expect(pElement.nativeElement.textContent).toBe('Základní informace o atributu.');
    });

    it('should bind the form correctly', () => {
        // Check that the component correctly uses the provided form
        const idInput = fixture.debugElement.query(By.css('#id'));
        const nameInput = fixture.debugElement.query(By.css('#name'));

        expect(idInput).toBeTruthy();
        expect(nameInput).toBeTruthy();

        // Check that the ID field is disabled as expected
        expect(idInput.nativeElement.disabled).toBe(true);
        expect(nameInput.nativeElement.disabled).toBe(false);
    });

    it('should display form values correctly', () => {
        const idInput = fixture.debugElement.query(By.css('#id')).nativeElement;

        // Check that the ID field displays the value from the form
        expect(idInput.value).toBe('test-id-123');

        // Update the name field
        testForm.get('name')?.setValue('Test Attribute');
        fixture.detectChanges();

        const nameInput = fixture.debugElement.query(By.css('#name')).nativeElement;
        expect(nameInput.value).toBe('Test Attribute');
    });

    it('should not show error message when name field is untouched', () => {
        const errorMessage = fixture.debugElement.query(By.css('.text-red-500'));
        expect(errorMessage).toBeFalsy();
    });

    it('should show error message when name field is touched and empty', () => {
        // Touch the name field and leave it empty
        testForm.get('name')?.markAsTouched();
        fixture.detectChanges();

        const errorMessage = fixture.debugElement.query(By.css('.text-red-500'));
        expect(errorMessage).toBeTruthy();
        expect(errorMessage.nativeElement.textContent).toContain('Název atributu je povinný');
    });

    it('should not show error message when name field is valid', () => {
        // Set a valid value and mark as touched
        testForm.get('name')?.setValue('Valid Name');
        testForm.get('name')?.markAsTouched();
        fixture.detectChanges();

        const errorMessage = fixture.debugElement.query(By.css('.text-red-500'));
        expect(errorMessage).toBeFalsy();
    });

    it('should have correct labels for form fields', () => {
        const labels = fixture.debugElement.queryAll(By.css('label'));

        expect(labels.length).toBe(2);
        expect(labels[0].nativeElement.textContent).toBe('ID atributu');
        expect(labels[1].nativeElement.textContent).toBe('Název atributu *');
    });
});
