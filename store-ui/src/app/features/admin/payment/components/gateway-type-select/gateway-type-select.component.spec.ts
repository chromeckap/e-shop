import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GatewayTypeSelectComponent } from './gateway-type-select.component';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { PaymentGatewayType } from '../../../../../services/models/payment-method/payment-gateway-type';
import { Divider } from 'primeng/divider';
import { FloatLabel } from 'primeng/floatlabel';
import { Select } from 'primeng/select';
import { Component, ViewChild } from '@angular/core';
import { By } from '@angular/platform-browser';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

// Create a test host component to provide the form
@Component({
    selector: 'app-test-host',
    standalone: true,
    imports: [GatewayTypeSelectComponent, ReactiveFormsModule],
    template: `
        <app-gateway-type-select
            [form]="form"
            [gatewayTypes]="gatewayTypes"
        ></app-gateway-type-select>
    `
})
class TestHostComponent {
    @ViewChild(GatewayTypeSelectComponent) gatewayTypeSelectComponent!: GatewayTypeSelectComponent;
    form!: FormGroup;
    gatewayTypes: PaymentGatewayType[] = [];

    constructor(private fb: FormBuilder) {
        this.createForm();
        this.setupGatewayTypes();
    }

    createForm() {
        this.form = this.fb.group({
            gatewayType: [null, Validators.required]
        });
    }

    setupGatewayTypes() {
        this.gatewayTypes = [
            { id: '1', name: 'Credit Card', code: 'CREDIT_CARD' },
            { id: '2', name: 'PayPal', code: 'PAYPAL' },
            { id: '3', name: 'Bank Transfer', code: 'BANK_TRANSFER' }
        ];
    }
}

describe('GatewayTypeSelectComponent', () => {
    let hostComponent: TestHostComponent;
    let hostFixture: ComponentFixture<TestHostComponent>;
    let component: GatewayTypeSelectComponent;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                NoopAnimationsModule,
                TestHostComponent,
                GatewayTypeSelectComponent,
                Divider,
                FloatLabel,
                Select
            ]
        }).compileComponents();

        hostFixture = TestBed.createComponent(TestHostComponent);
        hostComponent = hostFixture.componentInstance;
        hostFixture.detectChanges();
        component = hostComponent.gatewayTypeSelectComponent;
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should receive form from parent component', () => {
        expect(component.form).toBeTruthy();
        expect(component.form.get('gatewayType')).toBeTruthy();
    });

    it('should receive gateway types from parent component', () => {
        expect(component.gatewayTypes).toBeTruthy();
        expect(component.gatewayTypes.length).toBe(3);
        expect(component.gatewayTypes[0]['name']).toBe('Credit Card');
    });

    it('should render p-select component', () => {
        const selectElement = hostFixture.debugElement.query(By.css('p-select'));
        expect(selectElement).toBeTruthy();
    });

    it('should display validation error when gatewayType is touched and invalid', () => {
        // Initially the error should not be shown as the field is untouched
        let errorElement = hostFixture.debugElement.query(By.css('.text-red-500'));
        expect(errorElement).toBeFalsy();

        // Mark the control as touched
        const control = component.form.get('gatewayType');
        control?.markAsTouched();
        hostFixture.detectChanges();

        // Now the error should be visible
        errorElement = hostFixture.debugElement.query(By.css('.text-red-500'));
        expect(errorElement).toBeTruthy();
        expect(errorElement.nativeElement.textContent).toContain('Typ platební metody je povinný');
    });

    it('should pass gateway types as options to p-select', () => {
        const selectElement = hostFixture.debugElement.query(By.css('p-select'));
        expect(selectElement.attributes['ng-reflect-options']).toBeTruthy();

        // Since the actual binding is not easily accessible in the test,
        // we can verify the component property is correctly set
        expect(component.gatewayTypes).toEqual(hostComponent.gatewayTypes);
    });

    it('should update form value when gateway type is selected', () => {
        // Since we can't easily interact with PrimeNG components in unit tests,
        // we can simulate form value changes programmatically

        // Initial value should be null
        expect(component.form.get('gatewayType')?.value).toBeNull();

        // Set value programmatically
        const selectedGateway = hostComponent.gatewayTypes[1]; // PayPal
        component.form.get('gatewayType')?.setValue(selectedGateway);
        hostFixture.detectChanges();

        // Check if value was updated
        expect(component.form.get('gatewayType')?.value).toBe(selectedGateway);
    });

    it('should mark form control as valid when value is selected', () => {
        // Initial state should be invalid
        expect(component.form.get('gatewayType')?.valid).toBeFalse();

        // Set valid value
        component.form.get('gatewayType')?.setValue(hostComponent.gatewayTypes[0]);
        hostFixture.detectChanges();

        // Now should be valid
        expect(component.form.get('gatewayType')?.valid).toBeTrue();
    });

    it('should handle empty gateway types array', () => {
        // Set empty gateway types
        hostComponent.gatewayTypes = [];
        hostFixture.detectChanges();

        // Component should still render without errors
        expect(component.gatewayTypes.length).toBe(0);
        const selectElement = hostFixture.debugElement.query(By.css('p-select'));
        expect(selectElement).toBeTruthy();
    });
});
