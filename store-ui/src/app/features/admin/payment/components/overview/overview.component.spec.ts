import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OverviewComponent } from './overview.component';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Divider } from 'primeng/divider';
import { FloatLabel } from 'primeng/floatlabel';
import { InputText } from 'primeng/inputtext';
import { InputNumber } from 'primeng/inputnumber';
import { InputGroup } from 'primeng/inputgroup';
import { InputGroupAddon } from 'primeng/inputgroupaddon';
import { ToggleButton } from 'primeng/togglebutton';
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
            isActive: [false],
            price: [0, Validators.required],
            isFreeForOrderAbove: [false],
            freeForOrderAbove: [0, Validators.required]
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
                InputNumber,
                InputGroup,
                InputGroupAddon,
                ToggleButton
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
        expect(component.form.get('isActive')).toBeTruthy();
        expect(component.form.get('price')).toBeTruthy();
        expect(component.form.get('isFreeForOrderAbove')).toBeTruthy();
        expect(component.form.get('freeForOrderAbove')).toBeTruthy();
    });

    it('should render all form controls', () => {
        // Check if all form controls are rendered
        const idInput = hostFixture.debugElement.query(By.css('#id'));
        const nameInput = hostFixture.debugElement.query(By.css('#name'));
        const isActiveToggle = hostFixture.debugElement.query(By.css('#isActive'));
        const priceInput = hostFixture.debugElement.query(By.css('#price'));
        const isFreeForOrderAboveToggle = hostFixture.debugElement.query(By.css('#isFreeForOrderAbove'));
        const freeForOrderAboveInput = hostFixture.debugElement.query(By.css('#freeForOrderAbove'));

        expect(idInput).toBeTruthy();
        expect(nameInput).toBeTruthy();
        expect(isActiveToggle).toBeTruthy();
        expect(priceInput).toBeTruthy();
        expect(isFreeForOrderAboveToggle).toBeTruthy();
        expect(freeForOrderAboveInput).toBeTruthy();
    });

    it('should update form value when name is entered', () => {
        hostFixture.debugElement.query(By.css('#name'));
        const testName = 'Test Payment Method';

        // Set value programmatically
        component.form.get('name')?.setValue(testName);
        hostFixture.detectChanges();

        // Check if value was updated
        expect(component.form.get('name')?.value).toBe(testName);
    });

    it('should toggle isActive value', () => {
        // Initial value should be false
        expect(component.form.get('isActive')?.value).toBeFalse();

        // Toggle to true
        component.form.get('isActive')?.setValue(true);
        hostFixture.detectChanges();

        // Check if value was updated
        expect(component.form.get('isActive')?.value).toBeTrue();
    });

    it('should update price value', () => {
        const testPrice = 100;

        // Set value programmatically
        component.form.get('price')?.setValue(testPrice);
        hostFixture.detectChanges();

        // Check if value was updated
        expect(component.form.get('price')?.value).toBe(testPrice);
    });

    it('should display validation error when price is touched and invalid', () => {
        // Set price to null to make it invalid
        component.form.get('price')?.setValue(null);
        component.form.get('price')?.markAsTouched();
        hostFixture.detectChanges();

        // Find the error message for price
        const errorElements = hostFixture.debugElement.queryAll(By.css('.text-red-500'));
        const priceErrorElement = Array.from(errorElements).find(
            el => el.nativeElement.textContent.includes('Cena platební metody je povinná')
        );

        expect(priceErrorElement).toBeTruthy();
    });

    it('should toggle isFreeForOrderAbove value', () => {
        // Initial value should be false
        expect(component.form.get('isFreeForOrderAbove')?.value).toBeFalse();

        // Toggle to true
        component.form.get('isFreeForOrderAbove')?.setValue(true);
        hostFixture.detectChanges();

        // Check if value was updated
        expect(component.form.get('isFreeForOrderAbove')?.value).toBeTrue();
    });

    it('should update freeForOrderAbove value', () => {
        const testValue = 1000;

        // Set value programmatically
        component.form.get('freeForOrderAbove')?.setValue(testValue);
        hostFixture.detectChanges();

        // Check if value was updated
        expect(component.form.get('freeForOrderAbove')?.value).toBe(testValue);
    });

    it('should display validation error when freeForOrderAbove is touched and invalid', () => {
        // Set freeForOrderAbove to null to make it invalid
        component.form.get('freeForOrderAbove')?.setValue(null);
        component.form.get('freeForOrderAbove')?.markAsTouched();
        hostFixture.detectChanges();

        // Find the error message for freeForOrderAbove
        const errorElements = hostFixture.debugElement.queryAll(By.css('.text-red-500'));
        const freeForOrderAboveErrorElement = Array.from(errorElements).find(
            el => el.nativeElement.textContent.includes('Hranice ceny objednávky pro uplatnění je povinná')
        );

        expect(freeForOrderAboveErrorElement).toBeTruthy();
    });

    it('should make form valid when all required fields are provided', () => {
        // Set all required values
        component.form.patchValue({
            name: 'Test Payment Method',
            price: 100,
            freeForOrderAbove: 1000
        });

        expect(component.form.valid).toBeTrue();
    });

    it('should make form invalid when a required field is missing', () => {
        // Set all except name
        component.form.patchValue({
            name: '',
            price: 100,
            freeForOrderAbove: 1000
        });

        expect(component.form.valid).toBeFalse();

        // Set all except price
        component.form.patchValue({
            name: 'Test Payment Method',
            price: null,
            freeForOrderAbove: 1000
        });

        expect(component.form.valid).toBeFalse();

        // Set all except freeForOrderAbove
        component.form.patchValue({
            name: 'Test Payment Method',
            price: 100,
            freeForOrderAbove: null
        });

        expect(component.form.valid).toBeFalse();
    });
});
