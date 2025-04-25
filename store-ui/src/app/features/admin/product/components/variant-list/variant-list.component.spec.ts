import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { VariantListComponent } from './variant-list.component';
import { FormBuilder, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { VariantService } from '../../../../../services/services/variant.service';
import { ToastService } from '../../../../../shared/services/toast.service';
import { VariantResponse } from '../../../../../services/models/variant/variant-response';
import { AttributeResponse } from '../../../../../services/models/attribute/attribute-response';
import { of, throwError } from 'rxjs';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('VariantListComponent', () => {
    let component: VariantListComponent;
    let fixture: ComponentFixture<VariantListComponent>;
    let mockVariantService: jasmine.SpyObj<VariantService>;
    let mockToastService: jasmine.SpyObj<ToastService>;
    let formBuilder: FormBuilder;

    beforeEach(async () => {
        mockVariantService = jasmine.createSpyObj('VariantService', [
            'createVariant',
            'updateVariant',
            'deleteVariantById'
        ]);
        mockToastService = jasmine.createSpyObj('ToastService', [
            'showSuccessToast',
            'showErrorToast'
        ]);

        await TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                FormsModule,
                VariantListComponent
            ],
            providers: [
                FormBuilder,
                { provide: VariantService, useValue: mockVariantService },
                { provide: ToastService, useValue: mockToastService }
            ],
            schemas: [NO_ERRORS_SCHEMA]
        }).compileComponents();

        formBuilder = TestBed.inject(FormBuilder);
        fixture = TestBed.createComponent(VariantListComponent);
        component = fixture.componentInstance;

        // Initialize the form control
        component.form = formBuilder.group({
            variants: [[]],
            attributes: [[]]
        });

        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    describe('setStoredVariants', () => {
        it('should set stored variants', () => {
            const mockVariants: VariantResponse[] = [
                { id: 1, sku: 'TEST-1', quantity: 10, basePrice: 100, discountedPrice: 80 }
            ];

            component.setStoredVariants(mockVariants);
            expect(component.storedVariants).toEqual(mockVariants);
        });
    });

    describe('openDialog', () => {
        it('should open dialog with the provided variant', () => {
            const variant: VariantResponse = {
                id: 1,
                sku: 'TEST-1',
                quantity: 10,
                basePrice: 100,
                discountedPrice: 80
            };

            component.openDialog(variant);

            expect(component.editDialogVisible).toBeTrue();
            expect(component.editingVariant).toEqual(variant);
            expect(component.editingVariant).not.toBe(variant); // Should be a copy
        });
    });

    describe('addVariant', () => {
        it('should open dialog with empty variant when there are enough attribute combinations', async () => {
            component.form.get('variants')?.setValue([{ id: 1 }]);
            component.form.get('attributes')?.setValue([{ id: 1 }, { id: 2 }]);

            await component.addVariant();

            expect(component.editDialogVisible).toBeTrue();
            expect(component.editingVariant).toEqual({
                sku: '',
                quantity: 0,
                quantityUnlimited: false,
                basePrice: 0,
                discountedPrice: 0,
                attributeValues: []
            });
        });
    });

    describe('saveVariant', () => {
        it('should update existing variant', () => {
            const existingVariants = [
                { id: 1, sku: 'OLD-SKU', quantity: 5, basePrice: 100, discountedPrice: 80 }
            ];
            component.form.get('variants')?.setValue(existingVariants);
            component.editingVariant = {
                id: 1,
                sku: 'NEW-SKU',
                quantity: 10,
                basePrice: 120,
                discountedPrice: 100
            };

            component.saveVariant();

            const updatedVariants = component.form.get('variants')?.value;
            expect(updatedVariants.length).toBe(1);
            expect(updatedVariants[0].sku).toBe('NEW-SKU');
            expect(component.editDialogVisible).toBeFalse();
        });

        it('should add new variant with generated ID', () => {
            const existingVariants = [
                { id: 1, sku: 'EXISTING-SKU', quantity: 5, basePrice: 100, discountedPrice: 80 }
            ];
            component.form.get('variants')?.setValue(existingVariants);
            component.editingVariant = {
                sku: 'NEW-SKU',
                quantity: 10,
                basePrice: 120,
                discountedPrice: 100
            };

            component.saveVariant();

            const updatedVariants = component.form.get('variants')?.value;
            expect(updatedVariants.length).toBe(2);
            expect(updatedVariants[1].id).toBe(2); // Should generate ID = max + 1
            expect(updatedVariants[1].sku).toBe('NEW-SKU');
            expect(component.editDialogVisible).toBeFalse();
        });
    });

    describe('deleteVariant', () => {
        it('should remove variant from the form', () => {
            const existingVariants = [
                { id: 1, sku: 'SKU-1' },
                { id: 2, sku: 'SKU-2' }
            ];
            component.form.get('variants')?.setValue(existingVariants);

            component.deleteVariant({ id: 1, sku: 'SKU-1' });

            const updatedVariants = component.form.get('variants')?.value;
            expect(updatedVariants.length).toBe(1);
            expect(updatedVariants[0].id).toBe(2);
        });
    });

    describe('getAttributeValue', () => {
        it('should return "Chybí" when attribute value does not exist', () => {
            const variant: VariantResponse = {
                attributeValues: [
                    { id: 101, value: 'Red' }
                ]
            };
            const attribute: AttributeResponse = { id: 1, name: 'Color', values: [] };

            const result = component.getAttributeValue(variant, attribute);

            expect(result).toBe('Chybí');
        });

        it('should return "Chybí" when attributeValues is not an object', () => {
            const variant: VariantResponse = {};
            const attribute: AttributeResponse = { id: 1, name: 'Color', values: [] };

            const result = component.getAttributeValue(variant, attribute);

            expect(result).toBe('Chybí');
        });
    });

    describe('getAttributeValues', () => {
        it('should return attribute values for the given attribute ID', () => {
            const attributeValues = [{ id: 101, value: 'Red' }, { id: 102, value: 'Blue' }];
            component.form.get('attributes')?.setValue([
                { id: 1, name: 'Color', values: attributeValues }
            ]);

            const result = component.getAttributeValues(1);

            expect(result).toEqual(attributeValues);
        });

        it('should return empty array when attribute values are not found', () => {
            component.form.get('attributes')?.setValue([
                { id: 1, name: 'Color' }
            ]);

            const result = component.getAttributeValues(1);

            expect(result).toEqual([]);
        });
    });

    describe('manageVariants', () => {
        it('should create, update and delete variants as needed', fakeAsync(() => {
            // Setup initial state
            component.storedVariants = [
                { id: 1, sku: 'OLD-1', productId: 100 },
                { id: 2, sku: 'TO-DELETE', productId: 100 }
            ];

            // Setup new variants in the form
            component.form.get('variants')?.setValue([
                {
                    id: 1,
                    sku: 'UPDATED-1',
                    basePrice: 100,
                    discountedPrice: 80,
                    quantity: 10,
                    quantityUnlimited: false,
                    attributeValues: {
                        1: { id: 101, value: 'Red' }
                    }
                },
                {
                    sku: 'NEW-1',
                    basePrice: 120,
                    discountedPrice: 90,
                    quantity: 5,
                    quantityUnlimited: true,
                    attributeValues: {
                        1: { id: 102, value: 'Blue' },
                        2: { id: 201, value: 'Large' }
                    }
                }
            ]);

            // Mock service responses
            mockVariantService.createVariant.and.returnValue(of());
            mockVariantService.updateVariant.and.returnValue(of());
            mockVariantService.deleteVariantById.and.returnValue(of());
            mockToastService.showSuccessToast.and.resolveTo();

            // Execute
            component.manageVariants(100).subscribe(result => {
                expect(result).toBeTruthy();

                // Verify create calls
                expect(mockVariantService.createVariant).toHaveBeenCalledWith(jasmine.objectContaining({
                    sku: 'NEW-1',
                    productId: 100,
                    attributeValueIds: [102, 201]
                }));

                // Verify update calls
                expect(mockVariantService.updateVariant).toHaveBeenCalledWith(1, jasmine.objectContaining({
                    sku: 'UPDATED-1',
                    productId: 100,
                    attributeValueIds: [101]
                }));

                // Verify delete calls
                expect(mockVariantService.deleteVariantById).toHaveBeenCalledWith(2);

                // Verify success toast
                expect(mockToastService.showSuccessToast).toHaveBeenCalledWith(
                    'Úspěch',
                    'Varianty byly úspěšně uloženy.'
                );
            });

            tick();
        }));

        it('should handle errors and show error toast', fakeAsync(() => {
            // Setup
            component.storedVariants = [];
            component.form.get('variants')?.setValue([{
                sku: 'NEW-1',
                basePrice: 100,
                discountedPrice: 80,
                quantity: 10,
                attributeValues: {}
            }]);

            const errorResponse = { error: { detail: 'API Error Message' } };
            mockVariantService.createVariant.and.returnValue(throwError(() => errorResponse));
            mockToastService.showErrorToast.and.resolveTo();

            // Execute
            component.manageVariants(100).subscribe({
                error: (err) => {
                    expect(err).toBe(errorResponse);
                    expect(mockToastService.showErrorToast).toHaveBeenCalledWith(
                        'Chyba',
                        'API Error Message'
                    );
                }
            });

            tick();
        }));
    });
});
