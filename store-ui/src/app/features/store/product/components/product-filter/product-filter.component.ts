import {Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';
import {MultiSelect, MultiSelectChangeEvent} from "primeng/multiselect";
import {FilterRangesResponse} from "../../../../../services/models/product/filter-ranges-response";
import {CustomCurrencyPipe} from "../../../../../shared/pipes/CustomCurrencyPipe";
import {Slider} from "primeng/slider";
import {Drawer} from "primeng/drawer";
import {FormsModule} from "@angular/forms";
import {PrimeTemplate} from "primeng/api";
import {FloatLabel} from "primeng/floatlabel";
import {Select} from "primeng/select";
import {Button} from "primeng/button";
import {Popover} from "primeng/popover";

@Component({
    selector: 'app-product-filter',
    imports: [
        MultiSelect,
        CustomCurrencyPipe,
        Slider,
        Drawer,
        FormsModule,
        PrimeTemplate,
        FloatLabel,
        Select,
        Button,
        Popover
    ],
    templateUrl: './product-filter.component.html',
    standalone: true,
    styleUrl: './product-filter.component.scss'
})
export class ProductFilterComponent implements OnChanges {
    @Input() filterRanges: FilterRangesResponse = {};
    @Output() filterChanged = new EventEmitter<{
        rangeValues: number[],
        selectedAttributeValues: { [attributeId: number]: number[] }
    }>();
    @Output() sortChanged = new EventEmitter<{ attribute: string, direction: string }>();

    sortOptions = [
        { name: 'Od nejnovějšího', attribute: 'id', direction: 'desc' },
        { name: 'Podle názvu', attribute: 'name', direction: 'asc' },
        { name: 'Od nejlevnějšího', attribute: 'price', direction: 'asc' },
        { name: 'Od nejdražšího', attribute: 'price', direction: 'desc' },
    ];

    selectedSortOption = this.sortOptions[0];
    rangeValues: number[] = [0, 100];
    drawerVisible = false;
    selectedAttributeValues: { [attributeId: number]: number[] } = {};
    debounceTimer: any;

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['filterRanges'] && this.filterRanges) {
            const minPrice = this.filterRanges.lowPrice || 0;
            const maxPrice = this.filterRanges.maxPrice || 0;

            if (minPrice !== this.rangeValues[0] || maxPrice !== this.rangeValues[1]) {
                this.rangeValues = [minPrice, maxPrice];
            }
        }
    }

    calculateStep(steps: number = 20): number {
        const min = this.filterRanges.lowPrice || 0;
        const max = this.filterRanges.maxPrice || 0;
        const range = max - min;
        const rawStep = range / steps;

        if (rawStep < 1) return Math.round(rawStep * 100) / 100;
        if (rawStep < 10) return Math.round(rawStep);
        if (rawStep < 100) return Math.round(rawStep / 5) * 5;
        return Math.round(rawStep / 10) * 10;
    }

    onPriceChange(): void {
        if (this.debounceTimer) {
            clearTimeout(this.debounceTimer);
        }

        this.debounceTimer = setTimeout(() => {
            this.emitFilterChange();
        }, 300);
    }

    onAttributeChange(attribute: any, event: MultiSelectChangeEvent): void {
        this.selectedAttributeValues[attribute.id] = event.value;
        this.emitFilterChange();
    }

    onSortChange(): void {
        this.sortChanged.emit({
            attribute: this.selectedSortOption.attribute,
            direction: this.selectedSortOption.direction
        });
    }

    private emitFilterChange(): void {
        this.filterChanged.emit({
            rangeValues: this.rangeValues,
            selectedAttributeValues: this.selectedAttributeValues
        });
    }
}
