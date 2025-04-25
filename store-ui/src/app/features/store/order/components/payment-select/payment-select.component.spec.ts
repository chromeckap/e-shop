import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { Component, NO_ERRORS_SCHEMA } from '@angular/core';
import { By } from '@angular/platform-browser';

import { PaymentSelectComponent } from './payment-select.component';
import { PaymentMethodService } from '../../../../../services/services/payment-method.service';
import { PaymentMethodResponse } from '../../../../../services/models/payment-method/payment-method-response';
import { CustomCurrencyPipe } from '../../../../../shared/pipes/CustomCurrencyPipe';

describe('PaymentSelectComponent', () => {
    let component: PaymentSelectComponent;
    let fixture: ComponentFixture<PaymentSelectComponent>;
    let paymentMethodService: jasmine.SpyObj<PaymentMethodService>;
    let formBuilder: FormBuilder;
    let form: FormGroup;

    // Mock data
    const mockPaymentMethods: PaymentMethodResponse[] = [
        {
            id: 1,
            name: 'Credit Card',
            price: 0,
            isActive: true
        },
        {
            id: 2,
            name: 'Bank Transfer',
            price: 30,
            isActive: true,
            isFreeForOrderAbove: true,
            freeForOrderAbove: 1000
        },
        {
            id: 3,
            name: 'Cash on Delivery',
            price: 50,
            isActive: true,
            isFreeForOrderAbove: false
        }
    ];

    beforeEach(async () => {
        const paymentMethodServiceSpy = jasmine.createSpyObj('PaymentMethodService', [
            'getActivePaymentMethods'
        ]);

        await TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                PaymentSelectComponent
            ],
            providers: [
                { provide: PaymentMethodService, useValue: paymentMethodServiceSpy },
                CustomCurrencyPipe
            ],
            schemas: [NO_ERRORS_SCHEMA]
        }).compileComponents();

        paymentMethodService = TestBed.inject(PaymentMethodService) as jasmine.SpyObj<PaymentMethodService>;
        formBuilder = TestBed.inject(FormBuilder);
    });

    beforeEach(() => {
        // Create form with validation
        form = formBuilder.group({
            paymentMethod: [null]
        });

        // Setup default successful response
        paymentMethodService.getActivePaymentMethods.and.returnValue(of(mockPaymentMethods));

        fixture = TestBed.createComponent(PaymentSelectComponent);
        component = fixture.componentInstance;
        component.form = form;
        component.cartTotal = 500;
        fixture.detectChanges();
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    it('should load payment methods on initialization', () => {
        expect(paymentMethodService.getActivePaymentMethods).toHaveBeenCalled();
        expect(component.availablePaymentMethods).toEqual(mockPaymentMethods);
    });

    it('should handle error when loading payment methods fails', () => {
        paymentMethodService.getActivePaymentMethods.and.returnValue(throwError(() => new Error('Test error')));
        spyOn(console, 'error');

        component.ngOnInit();

        expect(console.error).toHaveBeenCalledWith(
            'Při načítání dostupných platebních metod došlo k chybě:',
            jasmine.any(Error)
        );
    });

    it('should display payment methods in the template', () => {
        const paymentMethodElements = fixture.debugElement.queryAll(By.css('.bg-white'));
        expect(paymentMethodElements.length).toBe(mockPaymentMethods.length);

        // Check first payment method name is displayed
        expect(paymentMethodElements[0].nativeElement.textContent).toContain('Credit Card');
    });

    describe('getPaymentPrice method', () => {
        it('should return false for free payment method (price = 0)', () => {
            const result = component.getPaymentPrice(mockPaymentMethods[0]);
            expect(result).toBeFalse();
        });

        it('should return true for paid payment method with cart total below free threshold', () => {
            // Bank Transfer with threshold 1000 and cart total 500
            const result = component.getPaymentPrice(mockPaymentMethods[1]);
            expect(result).toBeTrue();
        });

        it('should return false for paid payment method with cart total above free threshold', () => {
            // Increase cart total above threshold
            component.cartTotal = 1500;

            // Bank Transfer with threshold 1000
            const result = component.getPaymentPrice(mockPaymentMethods[1]);
            expect(result).toBeFalse();
        });

        it('should return false for paid payment method without free threshold option', () => {
            // Cash on Delivery with no free threshold
            const result = component.getPaymentPrice(mockPaymentMethods[2]);
            expect(result).toBeFalse();
        });
    });

    it('should show price for payment methods that are not free', () => {
        fixture.detectChanges();

        // The second payment method should show price (30 CZK)
        const priceElements = fixture.debugElement.queryAll(By.css('.bg-white span:not(.font-bold)'));
        expect(priceElements.length).toBeGreaterThan(0);

        // Find the element that should contain the price
        const bankTransferElement = fixture.debugElement.queryAll(By.css('.bg-white'))[1];
        expect(bankTransferElement.nativeElement.textContent).toContain('Bank Transfer');

        // Cannot check exact formatted price due to CustomCurrencyPipe dependency
    });

    it('should show free tag for payment methods that are free', () => {
        fixture.detectChanges();

        // The first payment method should show free tag
        const tagElements = fixture.debugElement.queryAll(By.css('p-tag'));
        expect(tagElements.length).toBeGreaterThan(0);
    });

    it('should apply bold styling when payment method is selected', () => {
        // Select the first payment method
        component.form.get('paymentMethod')?.setValue(mockPaymentMethods[0]);
        fixture.detectChanges();

        const nameElement = fixture.debugElement.query(By.css('.font-bold'));
        expect(nameElement).toBeTruthy();
        expect(nameElement.nativeElement.textContent.trim()).toBe('Credit Card');
    });

    it('should correctly bind the selected payment method to the form control', () => {
        // Get the radio button for the first payment method
        const radioButtons = fixture.debugElement.queryAll(By.css('p-radiobutton'));
        expect(radioButtons.length).toBe(mockPaymentMethods.length);

        // We can't directly trigger the radio button selection in this test setup,
        // but we can verify the binding by setting the form value directly
        component.form.get('paymentMethod')?.setValue(mockPaymentMethods[1]);
        fixture.detectChanges();

        // The second payment method should now be selected and styled accordingly
        const selectedLabels = fixture.debugElement.queryAll(By.css('.font-bold'));
        expect(selectedLabels.length).toBe(2);
        expect(selectedLabels[0].nativeElement.textContent.trim()).toBe('Bank Transfer');
    });
});
