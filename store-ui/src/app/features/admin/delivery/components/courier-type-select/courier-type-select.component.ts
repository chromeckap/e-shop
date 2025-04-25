import {Component, Input} from '@angular/core';
import {FormGroup, ReactiveFormsModule} from "@angular/forms";
import {CourierType} from "../../../../../services/models/delivery-method/courier-type";
import {Divider} from "primeng/divider";
import {FloatLabel} from "primeng/floatlabel";
import {Select} from "primeng/select";

@Component({
    selector: 'app-courier-type-select',
    imports: [
        Divider,
        FloatLabel,
        ReactiveFormsModule,
        Select
    ],
    templateUrl: './courier-type-select.component.html',
    standalone: true,
    styleUrl: './courier-type-select.component.scss'
})
export class CourierTypeSelectComponent {
    @Input() form!: FormGroup;
    @Input() courierTypes: CourierType[] = [];
}
