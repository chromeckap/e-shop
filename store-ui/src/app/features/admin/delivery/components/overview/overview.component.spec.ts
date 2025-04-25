import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OverviewComponent } from './overview.component';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('OverviewComponent', () => {
    let component: OverviewComponent;
    let fixture: ComponentFixture<OverviewComponent>;
    let formBuilder: FormBuilder;
    let form: FormGroup;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [
                OverviewComponent,
                ReactiveFormsModule
            ],
            schemas: [NO_ERRORS_SCHEMA] // Ignore PrimeNG components
        }).compileComponents();

        formBuilder = TestBed.inject(FormBuilder);

        // Create form with validation
        form = formBuilder.group({
            id: [{ value: null, disabled: true }],
            name: ['', Validators.required],
            isActive: [true],
            price: [null, Validators.required],
            isFreeForOrderAbove: [false],
            freeForOrderAbove: [null, Validators.required]
        });

        fixture = TestBed.createComponent(OverviewComponent);
        component = fixture.componentInstance;

        // Provide the form
        component.form = form;

        fixture.detectChanges();
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    it('should receive form input correctly', () => {
        expect(component.form).toBe(form);
    });

    describe('Form validation behavior', () => {

        it('should validate required fields', () => {
            // Initially form should be invalid
            expect(component.form.valid).toBeFalse();

            // Fill required fields
            component.form.get('name')?.setValue('Test Delivery Method');
            component.form.get('price')?.setValue(100);
            component.form.get('freeForOrderAbove')?.setValue(500);

            // Form should now be valid
            expect(component.form.valid).toBeTrue();
        });

        it('should disable the id field', () => {
            const idControl = component.form.get('id');
            expect(idControl?.disabled).toBeTrue();
        });
    });

    describe('Toggle behavior', () => {
        it('should have default toggle states', () => {
            expect(component.form.get('isActive')?.value).toBeTrue();
            expect(component.form.get('isFreeForOrderAbove')?.value).toBeFalse();
        });

        it('should update toggle states correctly', () => {
            // Change toggle states
            component.form.get('isActive')?.setValue(false);
            component.form.get('isFreeForOrderAbove')?.setValue(true);

            expect(component.form.get('isActive')?.value).toBeFalse();
            expect(component.form.get('isFreeForOrderAbove')?.value).toBeTrue();
        });
    });

    describe('Numeric input behavior', () => {
        it('should accept valid numeric values', () => {
            component.form.get('price')?.setValue(150);
            component.form.get('freeForOrderAbove')?.setValue(1000);

            expect(component.form.get('price')?.value).toBe(150);
            expect(component.form.get('freeForOrderAbove')?.value).toBe(1000);
            expect(component.form.get('price')?.valid).toBeTrue();
            expect(component.form.get('freeForOrderAbove')?.valid).toBeTrue();
        });

        it('should mark form as invalid with null numeric values', () => {
            component.form.get('price')?.setValue(null);
            component.form.get('freeForOrderAbove')?.setValue(null);

            expect(component.form.get('price')?.valid).toBeFalse();
            expect(component.form.get('freeForOrderAbove')?.valid).toBeFalse();
        });
    });
});
