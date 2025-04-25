import {Component, Input} from '@angular/core';
import {FormGroup, ReactiveFormsModule} from "@angular/forms";
import {FloatLabel} from "primeng/floatlabel";
import {InputText} from "primeng/inputtext";

@Component({
    selector: 'app-additional-address',
    imports: [
        FloatLabel,
        InputText,
        ReactiveFormsModule
    ],
    templateUrl: './additional-address.component.html',
    standalone: true,
    styleUrl: './additional-address.component.scss'
})
export class AdditionalAddressComponent {
    @Input() form!: FormGroup;

}
