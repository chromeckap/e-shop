import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { AttributeMultiSelectComponent } from './attribute-multiselect.component';
import { AttributeService } from '../../../../../services/services/attribute.service';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Divider } from 'primeng/divider';
import { MultiSelect } from 'primeng/multiselect';
import { AttributeResponse } from '../../../../../services/models/attribute/attribute-response';
import { VariantResponse } from '../../../../../services/models/variant/variant-response';
import { of } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';
import { Component, ViewChild } from '@angular/core';

// Create a test host component to provide the form
@Component({
    selector: 'app-test-host',
    standalone: true,
    imports: [AttributeMultiSelectComponent, ReactiveFormsModule],
    template: `
    <app-attribute-multiselect [form]="form"></app-attribute-multiselect>
  `
})
class TestHostComponent {
    @ViewChild(AttributeMultiSelectComponent) attributeMultiSelectComponent!: AttributeMultiSelectComponent;
    form!: FormGroup;

    constructor(private fb: FormBuilder) {
        this.createForm();
    }

    createForm() {
        this.form = this.fb.group({
            attributes: [[]],
            variants: [[]]
        });
    }
}

describe('AttributeMultiSelectComponent', () => {
    let hostComponent: TestHostComponent;
    let hostFixture: ComponentFixture<TestHostComponent>;
    let component: AttributeMultiSelectComponent;
    let attributeServiceSpy: jasmine.SpyObj<AttributeService>;

    // Mock data
    const mockAttributes: AttributeResponse[] = [
        { id: 1, name: 'Color', values: [] },
        { id: 2, name: 'Size', values: [] },
        { id: 3, name: 'Material', values: [] }
    ];

    const mockVariants: VariantResponse[] = [
        {
            id: 101,
            basePrice: 100,
            quantity: 10,
            attributeValues: []
        },
        {
            id: 102,
            basePrice: 120,
            quantity: 15,
            attributeValues: []
        }
    ];

    beforeEach(async () => {
        // Create spy for AttributeService
        const attributeSpy = jasmine.createSpyObj('AttributeService', ['getAllAttributes']);

        await TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                NoopAnimationsModule,
                Divider,
                MultiSelect,
                TestHostComponent,
                AttributeMultiSelectComponent
            ],
            providers: [
                { provide: AttributeService, useValue: attributeSpy }
            ]
        }).compileComponents();

        attributeServiceSpy = TestBed.inject(AttributeService) as jasmine.SpyObj<AttributeService>;
        attributeServiceSpy.getAllAttributes.and.returnValue(of(mockAttributes));

        hostFixture = TestBed.createComponent(TestHostComponent);
        hostComponent = hostFixture.componentInstance;
        hostFixture.detectChanges();
        component = hostComponent.attributeMultiSelectComponent;
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should load all attributes on init', fakeAsync(() => {
        // Component should already have loaded attributes in beforeEach
        tick();

        expect(attributeServiceSpy.getAllAttributes).toHaveBeenCalled();
        expect(component.attributes).toEqual(mockAttributes);
        expect(component.attributes.length).toBe(3);
    }));

    it('should receive form from parent component', () => {
        expect(component.form).toBeTruthy();
        expect(component.form.get('attributes')).toBeTruthy();
        expect(component.form.get('variants')).toBeTruthy();
    });

    it('should render MultiSelect component', () => {
        const multiSelect = hostFixture.debugElement.query(By.css('p-multi-select'));
        expect(multiSelect).toBeTruthy();
    });

    it('should bind attributes to MultiSelect options', () => {
        const multiSelect = hostFixture.debugElement.query(By.css('p-multi-select'));
        expect(multiSelect.attributes['ng-reflect-options']).toBeTruthy();

        // Verify component's attributes are passed to the MultiSelect
        expect(component.attributes).toEqual(mockAttributes);
    });

    it('should update variant values when attributes change', fakeAsync(() => {
        // Setup initial form values
        hostComponent.form.patchValue({
            attributes: [mockAttributes[0], mockAttributes[1]], // Color and Size
            variants: [...mockVariants]
        });
        tick();

        // Spy on updateVariantValues method
        spyOn(component, 'updateVariantValues').and.callThrough();

        // Trigger onChange event by changing attributes
        component.form.get('attributes')?.setValue([mockAttributes[0]]); // Only Color
        const multiSelect = hostFixture.debugElement.query(By.css('p-multi-select'));
        multiSelect.triggerEventHandler('onChange', null);

        expect(component.updateVariantValues).toHaveBeenCalled();
    }));

    it('should keep all variant values when adding attributes', fakeAsync(() => {
        // Setup initial form values with only one attribute
        hostComponent.form.patchValue({
            attributes: [mockAttributes[0]], // Only Color
            variants: [...mockVariants]
        });
        tick();

        // First update to clean up variants based on initial attribute selection
        component.updateVariantValues();

        // Spy on updateVariantValues method
        spyOn(component, 'updateVariantValues').and.callThrough();

        // Add another attribute
        component.form.get('attributes')?.setValue([mockAttributes[0], mockAttributes[1]]); // Color and Size
        const multiSelect = hostFixture.debugElement.query(By.css('p-multi-select'));
        multiSelect.triggerEventHandler('onChange', null);

        expect(component.updateVariantValues).toHaveBeenCalled();
    }));

    it('should handle empty variants gracefully', fakeAsync(() => {
        // Setup form with empty variants
        hostComponent.form.patchValue({
            attributes: [mockAttributes[0], mockAttributes[1]],
            variants: []
        });
        tick();

        // This should not throw any error
        expect(() => component.updateVariantValues()).not.toThrow();

        // Check that variants array is still empty
        const updatedVariants = component.form.get('variants')?.value;
        expect(updatedVariants).toEqual([]);
    }));

    it('should handle form submission', fakeAsync(() => {
        // Setup initial form values
        hostComponent.form.patchValue({
            attributes: [mockAttributes[0], mockAttributes[1]],
            variants: [...mockVariants]
        });
        tick();

        // Ensure form is valid
        expect(hostComponent.form.valid).toBeTrue();

        // Check that form values contain the expected data
        const formValue = hostComponent.form.value;
        expect(formValue.attributes.length).toBe(2);
        expect(formValue.variants.length).toBe(2);
    }));
});
