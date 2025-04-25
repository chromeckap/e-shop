import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { By } from '@angular/platform-browser';

import { AdditionalAddressComponent } from './additional-address.component';


describe('AdditionalAddressComponent', () => {
    let component: AdditionalAddressComponent;
    let fixture: ComponentFixture<AdditionalAddressComponent>;
    let formBuilder: FormBuilder;
    let form: FormGroup;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                AdditionalAddressComponent
            ],
            schemas: [NO_ERRORS_SCHEMA] // Ignore unknown elements like pInputText
        }).compileComponents();

        formBuilder = TestBed.inject(FormBuilder);
    });

    beforeEach(() => {
        // Create form with validation
        form = formBuilder.group({
            isManualAddressRequired: [false],
            street: ['', Validators.required],
            postalCode: ['', Validators.required],
            city: ['', Validators.required]
        });

        fixture = TestBed.createComponent(AdditionalAddressComponent);
        component = fixture.componentInstance;
        component.form = form;
        fixture.detectChanges();
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    it('should not display address fields when isManualAddressRequired is false', () => {
        form.get('isManualAddressRequired')?.setValue(false);
        fixture.detectChanges();

        const addressHeading = fixture.debugElement.query(By.css('h4'));
        expect(addressHeading).toBeFalsy();

        const streetInput = fixture.debugElement.query(By.css('#street'));
        expect(streetInput).toBeFalsy();
    });

    it('should display address fields when isManualAddressRequired is true', () => {
        form.get('isManualAddressRequired')?.setValue(true);
        fixture.detectChanges();

        const addressHeading = fixture.debugElement.query(By.css('h4'));
        expect(addressHeading).toBeTruthy();
        expect(addressHeading.nativeElement.textContent).toContain('Adresa doručení');

        const streetInput = fixture.debugElement.query(By.css('#street'));
        expect(streetInput).toBeTruthy();

        const postalCodeInput = fixture.debugElement.query(By.css('#postalCode'));
        expect(postalCodeInput).toBeTruthy();

        const cityInput = fixture.debugElement.query(By.css('#city'));
        expect(cityInput).toBeTruthy();
    });

    it('should not show validation messages when fields are untouched', () => {
        form.get('isManualAddressRequired')?.setValue(true);
        fixture.detectChanges();

        const errorMessages = fixture.debugElement.queryAll(By.css('.text-red-500'));
        expect(errorMessages.length).toBe(0);
    });

    it('should show validation message for street when invalid and touched', () => {
        form.get('isManualAddressRequired')?.setValue(true);

        // Mark as touched but leave value empty to trigger validation error
        const streetControl = form.get('street');
        streetControl?.setValue('');
        streetControl?.markAsTouched();
        streetControl?.updateValueAndValidity();

        fixture.detectChanges();

        const errorMessages = fixture.debugElement.queryAll(By.css('.text-red-500'));
        expect(errorMessages.length).toBe(1);
        expect(errorMessages[0].nativeElement.textContent).toContain('Ulice je povinná');
    });

    it('should show validation message for postal code when invalid and touched', () => {
        form.get('isManualAddressRequired')?.setValue(true);

        // Mark as touched but leave value empty to trigger validation error
        const postalCodeControl = form.get('postalCode');
        postalCodeControl?.setValue('');
        postalCodeControl?.markAsTouched();
        postalCodeControl?.updateValueAndValidity();

        fixture.detectChanges();

        const errorMessages = fixture.debugElement.queryAll(By.css('.text-red-500'));
        expect(errorMessages.length).toBeGreaterThan(0);

        // Find the specific error for postal code
        const postalCodeError = fixture.debugElement.queryAll(By.css('.text-red-500')).find(
            el => el.nativeElement.textContent.includes('Směrovací číslo je povinné')
        );
        expect(postalCodeError).toBeTruthy();
    });

    it('should show validation message for city when invalid and touched', () => {
        form.get('isManualAddressRequired')?.setValue(true);

        // Mark as touched but leave value empty to trigger validation error
        const cityControl = form.get('city');
        cityControl?.setValue('');
        cityControl?.markAsTouched();
        cityControl?.updateValueAndValidity();

        fixture.detectChanges();

        const errorMessages = fixture.debugElement.queryAll(By.css('.text-red-500'));
        expect(errorMessages.length).toBeGreaterThan(0);

        // Find the specific error for city
        const cityError = fixture.debugElement.queryAll(By.css('.text-red-500')).find(
            el => el.nativeElement.textContent.includes('Město je povinné')
        );
        expect(cityError).toBeTruthy();
    });

    it('should not show validation messages when fields are valid', () => {
        form.get('isManualAddressRequired')?.setValue(true);

        // Set valid values for all required fields
        form.get('street')?.setValue('Test Street');
        form.get('postalCode')?.setValue('12345');
        form.get('city')?.setValue('Test City');

        // Mark all as touched
        Object.keys(form.controls).forEach(key => {
            form.get(key)?.markAsTouched();
        });

        fixture.detectChanges();

        const errorMessages = fixture.debugElement.queryAll(By.css('.text-red-500'));
        expect(errorMessages.length).toBe(0);
    });

    it('should properly bind form controls to inputs', () => {
        form.get('isManualAddressRequired')?.setValue(true);
        fixture.detectChanges();

        // Set values programmatically
        form.get('street')?.setValue('Test Street');
        form.get('postalCode')?.setValue('12345');
        form.get('city')?.setValue('Test City');

        fixture.detectChanges();

        // Check that the input values match
        const streetInput = fixture.debugElement.query(By.css('#street')).nativeElement;
        const postalCodeInput = fixture.debugElement.query(By.css('#postalCode')).nativeElement;
        const cityInput = fixture.debugElement.query(By.css('#city')).nativeElement;

        expect(streetInput.value).toBe('Test Street');
        expect(postalCodeInput.value).toBe('12345');
        expect(cityInput.value).toBe('Test City');
    });

    it('should update form values when inputs change', () => {
        form.get('isManualAddressRequired')?.setValue(true);
        fixture.detectChanges();

        // Simulate user input
        const streetInput = fixture.debugElement.query(By.css('#street')).nativeElement;
        streetInput.value = 'New Street';
        streetInput.dispatchEvent(new Event('input'));

        const postalCodeInput = fixture.debugElement.query(By.css('#postalCode')).nativeElement;
        postalCodeInput.value = '54321';
        postalCodeInput.dispatchEvent(new Event('input'));

        const cityInput = fixture.debugElement.query(By.css('#city')).nativeElement;
        cityInput.value = 'New City';
        cityInput.dispatchEvent(new Event('input'));

        fixture.detectChanges();

        // Check that form values were updated
        expect(form.get('street')?.value).toBe('New Street');
        expect(form.get('postalCode')?.value).toBe('54321');
        expect(form.get('city')?.value).toBe('New City');
    });
});
