import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { SidebarInfoComponent } from './sidebar-info.component';
import { PaymentService } from '../../../../../services/services/payment.service';
import { OrderResponse } from '../../../../../services/models/order/order-response';
import { PaymentResponse } from '../../../../../services/models/payment/payment-response';
import { DatePipe } from '@angular/common';
import { Divider } from 'primeng/divider';
import { Tag } from 'primeng/tag';
import { of } from 'rxjs';

describe('SidebarInfoComponent', () => {
    let component: SidebarInfoComponent;
    let fixture: ComponentFixture<SidebarInfoComponent>;
    let paymentServiceSpy: jasmine.SpyObj<PaymentService>;

    // Mock data
    const mockOrder: OrderResponse = {
        id: 123,
        userDetails: {
            firstName: 'John',
            lastName: 'Doe',
            email: 'john.doe@example.com',
            address: {
                street: 'Example Street 123',
                city: 'Example City',
                postalCode: '12345'
            }
        }
    };

    const mockPayment: PaymentResponse = {
        id: 456,
        orderId: 123,
        status: 'PAID'
    };

    beforeEach(async () => {
        // Create spy for PaymentService
        const paymentSpy = jasmine.createSpyObj('PaymentService', ['getPaymentByOrderId']);

        await TestBed.configureTestingModule({
            imports: [SidebarInfoComponent, DatePipe, Divider, Tag],
            providers: [
                { provide: PaymentService, useValue: paymentSpy }
            ]
        }).compileComponents();

        paymentServiceSpy = TestBed.inject(PaymentService) as jasmine.SpyObj<PaymentService>;
        paymentServiceSpy.getPaymentByOrderId.and.returnValue(of(mockPayment));

        fixture = TestBed.createComponent(SidebarInfoComponent);
        component = fixture.componentInstance;
        component.order = { ...mockOrder };
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should initialize with the provided order', () => {
        fixture.detectChanges();
        expect(component.order).toEqual(mockOrder);
    });

    it('should call getPaymentByOrderId when the method is called', fakeAsync(() => {
        component.getPaymentByOrderId(123);
        tick();

        expect(paymentServiceSpy.getPaymentByOrderId).toHaveBeenCalledWith(123);
        expect(component.payment).toEqual(mockPayment);
    }));

    it('should display user details correctly', () => {
        fixture.detectChanges();
        const compiled = fixture.nativeElement;

        expect(compiled.textContent).toContain('John Doe');
        expect(compiled.textContent).toContain('john.doe@example.com');
    });

    it('should display address details correctly', () => {
        fixture.detectChanges();
        const compiled = fixture.nativeElement;

        expect(compiled.textContent).toContain('Example Street 123');
        expect(compiled.textContent).toContain('12345 Example City');
    });
});
