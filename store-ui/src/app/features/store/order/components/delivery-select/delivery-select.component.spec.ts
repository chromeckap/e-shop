import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { By } from '@angular/platform-browser';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

import { DeliverySelectComponent } from './delivery-select.component';
import { DeliveryMethodService } from '../../../../../services/services/delivery-method.service';
import { DeliveryMethodResponse } from '../../../../../services/models/delivery-method/delivery-method-response';
import { CustomCurrencyPipe } from '../../../../../shared/pipes/CustomCurrencyPipe';

describe('DeliverySelectComponent', () => {
    let component: DeliverySelectComponent;
    let fixture: ComponentFixture<DeliverySelectComponent>;
    let deliveryMethodService: jasmine.SpyObj<DeliveryMethodService>;
    let sanitizer: jasmine.SpyObj<DomSanitizer>;
    let formBuilder: FormBuilder;
    let form: FormGroup;

    // Mock data
    const mockDeliveryMethods: DeliveryMethodResponse[] = [
        {
            id: 1,
            name: 'Standard Delivery',
            price: 100,
            isActive: true,
            isFreeForOrderAbove: true,
            freeForOrderAbove: 1000,
            courierType: new Map<string, string>([['type', 'standard']])
        },
        {
            id: 2,
            name: 'Express Delivery',
            price: 200,
            isActive: true,
            isFreeForOrderAbove: false,
            courierType: new Map<string, string>([['express', 'Express']])
        },
        {
            id: 3,
            name: 'Free Delivery',
            price: 0,
            isActive: true,
            courierType: new Map<string, string>([['free', 'Free']])
        }
    ];

    const mockSafeUrl: SafeResourceUrl = {} as SafeResourceUrl;

    beforeEach(async () => {
        const deliveryMethodServiceSpy = jasmine.createSpyObj('DeliveryMethodService', [
            'getActiveDeliveryMethods',
            'getCourierWidgetUrl'
        ]);
        const sanitizerSpy = jasmine.createSpyObj('DomSanitizer', ['bypassSecurityTrustResourceUrl']);

        await TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                DeliverySelectComponent
            ],
            providers: [
                { provide: DeliveryMethodService, useValue: deliveryMethodServiceSpy },
                { provide: DomSanitizer, useValue: sanitizerSpy },
                CustomCurrencyPipe
            ],
            schemas: [NO_ERRORS_SCHEMA]
        }).compileComponents();

        deliveryMethodService = TestBed.inject(DeliveryMethodService) as jasmine.SpyObj<DeliveryMethodService>;
        sanitizer = TestBed.inject(DomSanitizer) as jasmine.SpyObj<DomSanitizer>;
        formBuilder = TestBed.inject(FormBuilder);
    });

    beforeEach(() => {
        // Create form with validation
        form = formBuilder.group({
            deliveryMethod: [null],
            isManualAddressRequired: [false],
            street: [''],
            city: [''],
            postalCode: ['']
        });

        // Setup default successful responses
        deliveryMethodService.getActiveDeliveryMethods.and.returnValue(of(mockDeliveryMethods));
        deliveryMethodService.getCourierWidgetUrl.and.returnValue(of('https://example.com/widget'));
        sanitizer.bypassSecurityTrustResourceUrl.and.returnValue(mockSafeUrl);

        fixture = TestBed.createComponent(DeliverySelectComponent);
        component = fixture.componentInstance;
        component.form = form;
        component.cartTotal = 500;
        fixture.detectChanges();
    });

    afterEach(() => {
        // Ensure the event listener is properly removed
        component.ngOnDestroy();
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    it('should load delivery methods on initialization', () => {
        expect(deliveryMethodService.getActiveDeliveryMethods).toHaveBeenCalled();
        expect(component.availableDeliveryMethods).toEqual(mockDeliveryMethods);
    });

    it('should handle error when loading delivery methods fails', () => {
        deliveryMethodService.getActiveDeliveryMethods.and.returnValue(throwError(() => new Error('Test error')));
        spyOn(console, 'error');

        component.ngOnInit();

        expect(console.error).toHaveBeenCalledWith(
            'Při načítání dostupných metod pro doručení došlo k chybě:',
            jasmine.any(Error)
        );
    });

    it('should display delivery methods in the template', () => {
        const deliveryMethodElements = fixture.debugElement.queryAll(By.css('.bg-white'));
        expect(deliveryMethodElements.length).toBe(mockDeliveryMethods.length);

        // Check first delivery method name is displayed
        expect(deliveryMethodElements[0].nativeElement.textContent).toContain('Standard Delivery');
    });

    it('should calculate delivery price correctly', () => {
        // Case 1: Delivery with price and cart total below free threshold
        expect(component.getDeliveryPrice(mockDeliveryMethods[0])).toBeTrue();

        // Case 2: Delivery with price but no free threshold option
        expect(component.getDeliveryPrice(mockDeliveryMethods[1])).toBeFalse();

        // Case 3: Free delivery
        expect(component.getDeliveryPrice(mockDeliveryMethods[2])).toBeFalse();

        // Case 4: Cart total above free threshold
        component.cartTotal = 1500;
        expect(component.getDeliveryPrice(mockDeliveryMethods[0])).toBeFalse();
    });

    it('should open widget panel when delivery method with courier type is selected', () => {
        const courierType = { type: 'packeta', name: 'Packeta' };

        component.openWidgetPanel(courierType);

        expect(deliveryMethodService.getCourierWidgetUrl).toHaveBeenCalledWith('packeta');
        expect(sanitizer.bypassSecurityTrustResourceUrl).toHaveBeenCalledWith('https://example.com/widget');
        expect(component.dialogVisible).toBeTrue();
        expect(component.sanitizedWidgetUrl).toBe(mockSafeUrl);
    });

    it('should not open widget panel when courier type is not provided', () => {
        component.openWidgetPanel(null);

        expect(deliveryMethodService.getCourierWidgetUrl).not.toHaveBeenCalled();
        expect(component.dialogVisible).toBeFalse();
    });

    it('should handle error when loading widget URL fails', () => {
        deliveryMethodService.getCourierWidgetUrl.and.returnValue(
            throwError(() => new Error('Widget error'))
        );
        spyOn(console, 'error');

        component.openWidgetPanel({ type: 'standard' });

        expect(console.error).toHaveBeenCalledWith(
            'Při načítání widgetu doručení došlo k chybě:',
            jasmine.any(Error)
        );
    });

    it('should reset form values when dialog drag ends', () => {
        // Set some initial values
        form.get('deliveryMethod')?.setValue(mockDeliveryMethods[0]);
        form.get('street')?.setValue('Test Street');
        form.get('city')?.setValue('Test City');
        form.get('postalCode')?.setValue('12345');
        form.get('isManualAddressRequired')?.setValue(true);
        component.selectedPlaceName = 'Test Place';

        component.onDialogDragEnd();

        // Check all values are reset
        expect(form.get('deliveryMethod')?.value).toBeNull();
        expect(form.get('street')?.value).toBeNull();
        expect(form.get('city')?.value).toBeNull();
        expect(form.get('postalCode')?.value).toBeNull();
        expect(form.get('isManualAddressRequired')?.value).toBeNull();
        expect(component.selectedPlaceName).toBe('');
    });

    it('should handle Packeta delivery message event', () => {
        const packetaData = {
            packetaSelectedData: {
                place: 'Packeta Store',
                street: 'Packeta Street',
                city: 'Packeta City',
                zip: '12345'
            }
        };

        // Simulate message event
        const messageEvent = new MessageEvent('message', {
            data: packetaData
        });

        window.dispatchEvent(messageEvent);

        expect(component.selectedPlaceName).toBe('Packeta Store');
        expect(component.form.get('street')?.value).toBe('Packeta Street');
        expect(component.form.get('city')?.value).toBe('Packeta City');
        expect(component.form.get('postalCode')?.value).toBe('12345');
        expect(component.dialogVisible).toBeFalse();
    });

    it('should handle Balikovna delivery message event', () => {
        const balikovnaData = {
            point: {
                name: 'Balikovna Store',
                address: 'Balikovna Street, 12345, Balikovna City'
            }
        };

        // Simulate message event
        const messageEvent = new MessageEvent('message', {
            data: balikovnaData
        });

        window.dispatchEvent(messageEvent);

        expect(component.selectedPlaceName).toBe('Balikovna Store');
        expect(component.form.get('street')?.value).toBe('Balikovna Street');
        expect(component.form.get('city')?.value).toBe('Balikovna City');
        expect(component.form.get('postalCode')?.value).toBe('12345');
        expect(component.dialogVisible).toBeFalse();
    });

    it('should not process invalid message events', () => {
        // Set initial state
        component.selectedPlaceName = 'Initial Place';
        component.form.get('street')?.setValue('Initial Street');

        // Simulate invalid message event
        const invalidEvent = new MessageEvent('message', {
            data: { someOtherData: 'value' }
        });

        window.dispatchEvent(invalidEvent);

        // Check that nothing changed
        expect(component.selectedPlaceName).toBe('Initial Place');
        expect(component.form.get('street')?.value).toBe('Initial Street');
    });

    it('should display selected place name when delivery method is selected', () => {
        // Set selected delivery method and place name
        component.form.get('deliveryMethod')?.setValue(mockDeliveryMethods[0]);
        component.selectedPlaceName = 'Test Pickup Point';
        fixture.detectChanges();

        const placeName = fixture.debugElement.query(By.css('.bg-white span:nth-child(2)'));
        expect(placeName).toBeTruthy();
        expect(placeName.nativeElement.textContent).toContain('Test Pickup Point');
    });

    it('should add and remove event listener during component lifecycle', () => {
        spyOn(window, 'addEventListener');
        spyOn(window, 'removeEventListener');

        component.ngOnInit();
        component.ngOnDestroy();

        expect(window.addEventListener).toHaveBeenCalledWith('message', component.handleDeliveryMessage);
        expect(window.removeEventListener).toHaveBeenCalledWith('message', component.handleDeliveryMessage);
    });

    it('should display price for paid delivery', () => {
        component.form.get('deliveryMethod')?.setValue(mockDeliveryMethods[0]);
        fixture.detectChanges();

        const priceElement = fixture.debugElement.query(By.css('.bg-white:first-child div:last-child span'));
        expect(priceElement).toBeTruthy();
    });

    it('should display free tag for free delivery', () => {
        component.form.get('deliveryMethod')?.setValue(mockDeliveryMethods[2]);
        fixture.detectChanges();

        const tagElement = fixture.debugElement.query(By.css('.bg-white:last-child p-tag'));
        expect(tagElement).toBeTruthy();
    });
});
