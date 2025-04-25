import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CourierTypeSelectComponent } from './courier-type-select.component';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { By } from '@angular/platform-browser';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { CourierType } from '../../../../../services/models/delivery-method/courier-type';

describe('CourierTypeSelectComponent', () => {
    let component: CourierTypeSelectComponent;
    let fixture: ComponentFixture<CourierTypeSelectComponent>;
    let formBuilder: FormBuilder;
    let form: FormGroup;

    // Mock courier types data
    const mockCourierTypes: CourierType[] = [
        { id: '1', name: 'Standard Delivery' },
        { id: '2', name: 'Express Delivery' },
        { id: '3', name: 'Same Day Delivery' }
    ];

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [
                CourierTypeSelectComponent,
                ReactiveFormsModule
            ],
            schemas: [NO_ERRORS_SCHEMA] // Ignore unknown elements like p-select
        }).compileComponents();

        formBuilder = TestBed.inject(FormBuilder);

        // Create form with validation
        form = formBuilder.group({
            courierType: [null, Validators.required]
        });

        fixture = TestBed.createComponent(CourierTypeSelectComponent);
        component = fixture.componentInstance;

        // Set input properties
        component.form = form;
        component.courierTypes = mockCourierTypes;

        fixture.detectChanges();
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    it('should receive form input correctly', () => {
        expect(component.form).toBe(form);
    });

    it('should receive courierTypes input correctly', () => {
        expect(component.courierTypes).toEqual(mockCourierTypes);
        expect(component.courierTypes.length).toBe(3);
    });

    it('should render title and description', () => {
        const titleElement = fixture.debugElement.query(By.css('h2'));
        const descriptionElement = fixture.debugElement.query(By.css('p.text-gray-500'));

        expect(titleElement.nativeElement.textContent).toBe('Kurýr');
        expect(descriptionElement.nativeElement.textContent).toBe('Typ kurýra pro zpracování logiky.');
    });

    it('should not display validation error when form is pristine', () => {
        const errorElement = fixture.debugElement.query(By.css('.text-red-500'));
        expect(errorElement).toBeNull();
    });

    it('should display validation error when field is touched and invalid', () => {
        // Mark control as touched
        const control = component.form.get('courierType');
        control?.markAsTouched();
        fixture.detectChanges();

        const errorElement = fixture.debugElement.query(By.css('.text-red-500'));
        expect(errorElement).toBeTruthy();
        expect(errorElement.nativeElement.textContent).toBe('Typ kurýra je povinný.');
    });

    it('should not display validation error when value is valid', () => {
        // Set a valid value and mark as touched
        const control = component.form.get('courierType');
        control?.setValue(mockCourierTypes[0]);
        control?.markAsTouched();
        fixture.detectChanges();

        const errorElement = fixture.debugElement.query(By.css('.text-red-500'));
        expect(errorElement).toBeNull();
    });

    it('should bind to correct form control', () => {
        // Set a value programmatically
        component.form.get('courierType')?.setValue(mockCourierTypes[1]);
        fixture.detectChanges();

        // Check that the p-select has the right form control
        const selectElement = fixture.debugElement.query(By.css('p-select'));
        expect(selectElement.attributes['formControlName']).toBe('courierType');
    });
});
