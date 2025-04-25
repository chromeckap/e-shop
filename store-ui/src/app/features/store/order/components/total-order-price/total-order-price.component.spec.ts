import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { By } from '@angular/platform-browser';

import { TotalOrderPriceComponent } from './total-order-price.component';
import { CustomCurrencyPipe } from '../../../../../shared/pipes/CustomCurrencyPipe';

describe('TotalOrderPriceComponent', () => {
    let component: TotalOrderPriceComponent;
    let fixture: ComponentFixture<TotalOrderPriceComponent>;
    let formBuilder: FormBuilder;
    let form: FormGroup;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                TotalOrderPriceComponent
            ],
            providers: [
                CustomCurrencyPipe
            ],
            schemas: [NO_ERRORS_SCHEMA]
        }).compileComponents();

        formBuilder = TestBed.inject(FormBuilder);
    });

    beforeEach(() => {
        // Create form with necessary controls
        form = formBuilder.group({
            paymentMethod: [null],
            deliveryMethod: [null]
        });

        fixture = TestBed.createComponent(TotalOrderPriceComponent);
        component = fixture.componentInstance;
        component.form = form;
        component.cartTotal = 500;
        fixture.detectChanges();
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    it('should display cart total correctly', () => {
        const cartTotalElement = fixture.debugElement.queryAll(By.css('.flex.justify-between'))[0];
        expect(cartTotalElement.nativeElement.textContent).toContain('Celková cena produktů');
        // Cannot check exact formatted price due to CustomCurrencyPipe
    });

    describe('getPaymentPrice method', () => {
        it('should return "0" when payment method is undefined', () => {
            form.get('paymentMethod')?.setValue(undefined);
            expect(component.getPaymentPrice()).toBe("0");
        });

        it('should return 0 when payment method price is 0', () => {
            form.get('paymentMethod')?.setValue({
                price: 0
            });
            expect(component.getPaymentPrice()).toBe(0);
        });

        it('should return payment price when price > 0 and cart total < free threshold', () => {
            form.get('paymentMethod')?.setValue({
                price: 50,
                isFreeForOrderAbove: true,
                freeForOrderAbove: 1000
            });
            component.cartTotal = 500;
            expect(component.getPaymentPrice()).toBe(50);
        });

        it('should return 0 when price > 0 and cart total >= free threshold', () => {
            form.get('paymentMethod')?.setValue({
                price: 50,
                isFreeForOrderAbove: true,
                freeForOrderAbove: 1000
            });
            component.cartTotal = 1500;
            expect(component.getPaymentPrice()).toBe(0);
        });
    });

    describe('getDeliveryPrice method', () => {
        it('should return "0" when delivery method is undefined', () => {
            form.get('deliveryMethod')?.setValue(undefined);
            expect(component.getDeliveryPrice()).toBe("0");
        });

        it('should return "0" when delivery method price is undefined', () => {
            form.get('deliveryMethod')?.setValue({});
            expect(component.getDeliveryPrice()).toBe("0");
        });

        it('should return 0 when delivery method price is 0', () => {
            form.get('deliveryMethod')?.setValue({
                price: 0
            });
            expect(component.getDeliveryPrice()).toBe(0);
        });

        it('should return delivery price when price > 0 and cart total < free threshold', () => {
            form.get('deliveryMethod')?.setValue({
                price: 100,
                isFreeForOrderAbove: true,
                freeForOrderAbove: 1000
            });
            component.cartTotal = 500;
            expect(component.getDeliveryPrice()).toBe(100);
        });

        it('should return 0 when price > 0 and cart total >= free threshold', () => {
            form.get('deliveryMethod')?.setValue({
                price: 100,
                isFreeForOrderAbove: true,
                freeForOrderAbove: 1000
            });
            component.cartTotal = 1500;
            expect(component.getDeliveryPrice()).toBe(0);
        });
    });

    describe('getTotalPrice method', () => {
        it('should calculate total correctly with all prices', () => {
            component.cartTotal = 500;

            // Set up spies to return specific values
            spyOn(component, 'getPaymentPrice').and.returnValue(50);
            spyOn(component, 'getDeliveryPrice').and.returnValue(100);

            expect(component.getTotalPrice()).toBe(650);
        });

        it('should calculate total correctly with free payment', () => {
            component.cartTotal = 500;

            // Set up spies to return specific values
            spyOn(component, 'getPaymentPrice').and.returnValue(0);
            spyOn(component, 'getDeliveryPrice').and.returnValue(100);

            expect(component.getTotalPrice()).toBe(600);
        });

        it('should calculate total correctly with free delivery', () => {
            component.cartTotal = 500;

            // Set up spies to return specific values
            spyOn(component, 'getPaymentPrice').and.returnValue(50);
            spyOn(component, 'getDeliveryPrice').and.returnValue(0);

            expect(component.getTotalPrice()).toBe(550);
        });

        it('should calculate total correctly with both free payment and delivery', () => {
            component.cartTotal = 500;

            // Set up spies to return specific values
            spyOn(component, 'getPaymentPrice').and.returnValue(0);
            spyOn(component, 'getDeliveryPrice').and.returnValue(0);

            expect(component.getTotalPrice()).toBe(500);
        });

        it('should handle string values in price calculations', () => {
            component.cartTotal = 500;

            // Set up spies to return specific values
            spyOn(component, 'getPaymentPrice').and.returnValue("0");
            spyOn(component, 'getDeliveryPrice').and.returnValue("0");

            expect(component.getTotalPrice()).toBe(500);
        });
    });

    it('should display free tag when payment is free', () => {
        spyOn(component, 'getPaymentPrice').and.returnValue(0);
        fixture.detectChanges();

        const paymentElement = fixture.debugElement.queryAll(By.css('.flex.justify-between'))[1];
        const tagElement = paymentElement.query(By.css('p-tag'));
        expect(tagElement).toBeTruthy();
    });

    it('should display price when payment is not free', () => {
        spyOn(component, 'getPaymentPrice').and.returnValue(50);
        fixture.detectChanges();

        const paymentElement = fixture.debugElement.queryAll(By.css('.flex.justify-between'))[1];
        const priceElement = paymentElement.query(By.css('span:last-child'));
        expect(priceElement).toBeTruthy();
    });

    it('should display free tag when delivery is free', () => {
        spyOn(component, 'getDeliveryPrice').and.returnValue(0);
        fixture.detectChanges();

        const deliveryElement = fixture.debugElement.queryAll(By.css('.flex.justify-between'))[2];
        const tagElement = deliveryElement.query(By.css('p-tag'));
        expect(tagElement).toBeTruthy();
    });

    it('should display price when delivery is not free', () => {
        spyOn(component, 'getDeliveryPrice').and.returnValue(100);
        fixture.detectChanges();

        const deliveryElement = fixture.debugElement.queryAll(By.css('.flex.justify-between'))[2];
        const priceElement = deliveryElement.query(By.css('span:last-child'));
        expect(priceElement).toBeTruthy();
    });

    it('should display total order price', () => {
        spyOn(component, 'getTotalPrice').and.returnValue(650);
        fixture.detectChanges();

        const totalElement = fixture.debugElement.query(By.css('.flex.justify-between.font-bold'));
        expect(totalElement.nativeElement.textContent).toContain('Celková cena objednávky');
        // Cannot check exact formatted price due to CustomCurrencyPipe
    });
});
