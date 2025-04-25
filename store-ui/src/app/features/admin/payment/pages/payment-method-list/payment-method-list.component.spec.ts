import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { PaymentMethodListComponent } from './payment-method-list.component';
import { PaymentMethodService } from '../../../../../services/services/payment-method.service';
import { ConfirmationService } from 'primeng/api';
import { ToastService } from '../../../../../shared/services/toast.service';
import { Router } from '@angular/router';
import { TableModule } from 'primeng/table';
import { Toolbar } from 'primeng/toolbar';
import { Button } from 'primeng/button';
import { IconField } from 'primeng/iconfield';
import { InputText } from 'primeng/inputtext';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { CustomCurrencyPipe } from '../../../../../shared/pipes/CustomCurrencyPipe';
import { Tag } from 'primeng/tag';
import { of, throwError } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';
import { PaymentMethodResponse } from '../../../../../services/models/payment-method/payment-method-response';

describe('PaymentMethodListComponent', () => {
    let component: PaymentMethodListComponent;
    let fixture: ComponentFixture<PaymentMethodListComponent>;
    let paymentMethodServiceSpy: jasmine.SpyObj<PaymentMethodService>;
    let confirmationServiceSpy: jasmine.SpyObj<ConfirmationService>;
    let toastServiceSpy: jasmine.SpyObj<ToastService>;
    let routerSpy: jasmine.SpyObj<Router>;

    // Mock data
    const mockPaymentMethods: PaymentMethodResponse[] = [
        {
            id: 1,
            name: 'Credit Card Payment',
            price: 50,
            isActive: true,
            isFreeForOrderAbove: true,
            freeForOrderAbove: 1000
        },
        {
            id: 2,
            name: 'PayPal',
            price: 25,
            isActive: false,
            isFreeForOrderAbove: false,
            freeForOrderAbove: 0
        },
        {
            id: 3,
            name: 'Bank Transfer',
            price: 10,
            isActive: true,
            isFreeForOrderAbove: true,
            freeForOrderAbove: 500
        }
    ];

    beforeEach(async () => {
        // Create spies
        const paymentMethodSpy = jasmine.createSpyObj('PaymentMethodService', [
            'getAllPaymentMethods',
            'deletePaymentMethodById'
        ]);
        const confirmationSpy = jasmine.createSpyObj('ConfirmationService', ['confirm']);
        const toastSpy = jasmine.createSpyObj('ToastService', ['showSuccessToast', 'showErrorToast']);
        const routerSpy = jasmine.createSpyObj('Router', ['navigate']);

        await TestBed.configureTestingModule({
            imports: [
                NoopAnimationsModule,
                TableModule,
                Toolbar,
                Button,
                IconField,
                InputText,
                ConfirmDialog,
                CustomCurrencyPipe,
                Tag,
                PaymentMethodListComponent
            ],
            providers: [
                { provide: PaymentMethodService, useValue: paymentMethodSpy },
                { provide: ConfirmationService, useValue: confirmationSpy },
                { provide: ToastService, useValue: toastSpy },
                { provide: Router, useValue: routerSpy }
            ]
        }).compileComponents();

        paymentMethodServiceSpy = TestBed.inject(PaymentMethodService) as jasmine.SpyObj<PaymentMethodService>;
        confirmationServiceSpy = TestBed.inject(ConfirmationService) as jasmine.SpyObj<ConfirmationService>;
        toastServiceSpy = TestBed.inject(ToastService) as jasmine.SpyObj<ToastService>;

        // Setup default return values
        paymentMethodServiceSpy.getAllPaymentMethods.and.returnValue(of(mockPaymentMethods));
        paymentMethodServiceSpy.deletePaymentMethodById.and.returnValue(of());
        toastServiceSpy.showSuccessToast.and.returnValue(Promise.resolve());
        toastServiceSpy.showErrorToast.and.returnValue(Promise.resolve());
        routerSpy.navigate.and.returnValue(Promise.resolve(true));

        fixture = TestBed.createComponent(PaymentMethodListComponent);
        component = fixture.componentInstance;
    });

    it('should create', () => {
        fixture.detectChanges();
        expect(component).toBeTruthy();
    });

    it('should load payment methods on init', fakeAsync(() => {
        fixture.detectChanges();
        tick();

        expect(paymentMethodServiceSpy.getAllPaymentMethods).toHaveBeenCalled();
        expect(component.paymentMethods).toEqual(mockPaymentMethods);
        expect(component.paymentMethods.length).toBe(3);
    }));

    it('should render payment methods in the table', fakeAsync(() => {
        fixture.detectChanges();
        tick();

        // Get table rows (skip header row)
        const rows = fixture.debugElement.queryAll(By.css('tr'));

        // We should have header row + 3 data rows
        expect(rows.length).toBeGreaterThan(3);

        // Check some content in the rows
        const nameElements = fixture.debugElement.queryAll(By.css('a.hover\\:underline'));
        expect(nameElements.length).toBe(3);
        expect(nameElements[0].nativeElement.textContent).toContain('Credit Card Payment');
        expect(nameElements[1].nativeElement.textContent).toContain('PayPal');
        expect(nameElements[2].nativeElement.textContent).toContain('Bank Transfer');
    }));

    it('should navigate to create page when clicking "Nová platební metoda"', fakeAsync(() => {
        fixture.detectChanges();

        // Find and click the 'New payment method' button
        const newButton = fixture.debugElement.query(By.css('p-button[label="Nová platební metoda"]'));
        newButton.triggerEventHandler('onClick');
        tick();

    }));

    it('should handle delete errors', fakeAsync(() => {
        fixture.detectChanges();
        tick();

        // Make service throw error
        const errorResponse = {
            error: {
                detail: 'Cannot delete payment method'
            }
        };
        paymentMethodServiceSpy.deletePaymentMethodById.and.returnValue(throwError(() => errorResponse));

        spyOn(console, 'log');

        // Find and click the 'Delete' button for the first payment method
        const deleteButtons = fixture.debugElement.queryAll(By.css('p-button[icon="pi pi-trash"]'));
        deleteButtons[0].triggerEventHandler('onClick');
        tick();

        expect(toastServiceSpy.showErrorToast);
    }));

    it('should disable "Delete selected" button when no items are selected', fakeAsync(() => {
        component.selectedPaymentMethods = [];
        fixture.detectChanges();
        tick();

        const deleteSelectedButton = fixture.debugElement.query(By.css('p-button[label="Smazat vybrané"]'));
        expect(deleteSelectedButton.attributes['ng-reflect-disabled']).toBe('true');
    }));

    it('should enable "Delete selected" button when items are selected', fakeAsync(() => {
        component.selectedPaymentMethods = [mockPaymentMethods[0]];
        fixture.detectChanges();
        tick();

        const deleteSelectedButton = fixture.debugElement.query(By.css('p-button[label="Smazat vybrané"]'));
        expect(deleteSelectedButton.attributes['ng-reflect-disabled']).toBe('false');
    }));

    it('should filter table when using the search input', fakeAsync(() => {
        fixture.detectChanges();
        tick();

        // Get the table instance
        spyOn(component, 'onGlobalFilter');

        // Find the search input and trigger input event
        const searchInput = fixture.debugElement.query(By.css('input[type="text"]'));
        const inputEvent = new Event('input');
        searchInput.nativeElement.value = 'PayPal';
        searchInput.nativeElement.dispatchEvent(inputEvent);

        expect(component.onGlobalFilter).toHaveBeenCalled();
    }));
});
