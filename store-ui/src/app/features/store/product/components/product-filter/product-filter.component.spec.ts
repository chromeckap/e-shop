import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ProductFilterComponent } from './product-filter.component';
import { MultiSelectChangeEvent } from 'primeng/multiselect';
import { FilterRangesResponse } from '../../../../../services/models/product/filter-ranges-response';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CustomCurrencyPipe } from '../../../../../shared/pipes/CustomCurrencyPipe';

describe('ProductFilterComponent', () => {
    let component: ProductFilterComponent;
    let fixture: ComponentFixture<ProductFilterComponent>;

    const mockFilterRanges: FilterRangesResponse = {
        lowPrice: 100,
        maxPrice: 5000,
        attributes: [
            {
                id: 1,
                name: 'Color',
                values: [
                    { id: 1, value: 'Red' },
                    { id: 2, value: 'Blue' },
                    { id: 3, value: 'Green' }
                ]
            },
            {
                id: 2,
                name: 'Size',
                values: [
                    { id: 4, value: 'S' },
                    { id: 5, value: 'M' },
                    { id: 6, value: 'L' }
                ]
            }
        ]
    };

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [
                ProductFilterComponent,
                FormsModule
            ],
            providers: [
                CustomCurrencyPipe
            ],
            schemas: [NO_ERRORS_SCHEMA] // For PrimeNG components
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(ProductFilterComponent);
        component = fixture.componentInstance;
        component.filterRanges = mockFilterRanges;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should initialize with default values', () => {
        expect(component.sortOptions.length).toBe(4);
        expect(component.selectedSortOption).toEqual(component.sortOptions[0]);
        expect(component.drawerVisible).toBeFalse();
        expect(component.selectedAttributeValues).toEqual({});
    });

    it('should update range values when filterRanges changes', () => {
        const newFilterRanges: FilterRangesResponse = {
            lowPrice: 200,
            maxPrice: 8000
        };

        component.filterRanges = newFilterRanges;
        component.ngOnChanges({
            filterRanges: {
                currentValue: newFilterRanges,
                previousValue: mockFilterRanges,
                firstChange: false,
                isFirstChange: () => false
            }
        });

        expect(component.rangeValues).toEqual([200, 8000]);
    });

    it('should not update range values if they are the same', () => {
        // Set initial values
        component.rangeValues = [100, 5000];

        const sameFilterRanges: FilterRangesResponse = {
            lowPrice: 100,
            maxPrice: 5000
        };

        component.filterRanges = sameFilterRanges;
        component.ngOnChanges({
            filterRanges: {
                currentValue: sameFilterRanges,
                previousValue: mockFilterRanges,
                firstChange: false,
                isFirstChange: () => false
            }
        });

        expect(component.rangeValues).toEqual([100, 5000]);
    });

    it('should emit filter changes after price change with debounce', fakeAsync(() => {
        spyOn(component.filterChanged, 'emit');
        component.rangeValues = [200, 3000];

        component.onPriceChange();
        expect(component.filterChanged.emit).not.toHaveBeenCalled();

        tick(300); // Wait for debounce

        expect(component.filterChanged.emit).toHaveBeenCalledWith({
            rangeValues: [200, 3000],
            selectedAttributeValues: {}
        });
    }));

    it('should clear previous debounce timer on rapid price changes', fakeAsync(() => {
        spyOn(component.filterChanged, 'emit');

        component.rangeValues = [200, 3000];
        component.onPriceChange();

        tick(100); // Only wait 100ms

        component.rangeValues = [250, 3500];
        component.onPriceChange();

        tick(150); // Only 150ms passed after the second change
        expect(component.filterChanged.emit).not.toHaveBeenCalled();

        tick(150); // Now 300ms passed after the second change
        expect(component.filterChanged.emit).toHaveBeenCalledTimes(1);
        expect(component.filterChanged.emit).toHaveBeenCalledWith({
            rangeValues: [250, 3500],
            selectedAttributeValues: {}
        });
    }));

    it('should update selectedAttributeValues and emit changes on attribute change', () => {
        spyOn(component.filterChanged, 'emit');

        const attribute = { id: 1, name: 'Color' };
        const event = { value: [1, 3] } as MultiSelectChangeEvent;

        component.onAttributeChange(attribute, event);

        expect(component.selectedAttributeValues[1]).toEqual([1, 3]);
        expect(component.filterChanged.emit).toHaveBeenCalledWith({
            rangeValues: component.rangeValues,
            selectedAttributeValues: { 1: [1, 3] }
        });
    });

    it('should emit sort changes when sort option changes', () => {
        spyOn(component.sortChanged, 'emit');

        component.selectedSortOption = component.sortOptions[2]; // "Od nejlevnějšího"
        component.onSortChange();

        expect(component.sortChanged.emit).toHaveBeenCalledWith({
            attribute: 'price',
            direction: 'asc'
        });
    });

    it('should handle undefined filterRanges values', () => {
        component.filterRanges = {};
        component.ngOnChanges({
            filterRanges: {
                currentValue: {},
                previousValue: mockFilterRanges,
                firstChange: false,
                isFirstChange: () => false
            }
        });

        expect(component.rangeValues).toEqual([0, 0]);
        expect(component.calculateStep()).toBe(0);
    });

    it('should correctly emit filter changes for multiple attribute selections', () => {
        spyOn(component.filterChanged, 'emit');

        // First attribute selection
        component.onAttributeChange({ id: 1 }, { value: [1, 2] } as MultiSelectChangeEvent);

        expect(component.selectedAttributeValues).toEqual({ 1: [1, 2] });
        expect(component.filterChanged.emit).toHaveBeenCalledWith({
            rangeValues: component.rangeValues,
            selectedAttributeValues: { 1: [1, 2] }
        });

        // Second attribute selection
        component.onAttributeChange({ id: 2 }, { value: [4, 5] } as MultiSelectChangeEvent);

        expect(component.selectedAttributeValues).toEqual({ 1: [1, 2], 2: [4, 5] });
        expect(component.filterChanged.emit).toHaveBeenCalledWith({
            rangeValues: component.rangeValues,
            selectedAttributeValues: { 1: [1, 2], 2: [4, 5] }
        });
    });
});
