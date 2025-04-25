import {Component, Input} from '@angular/core';
import {FormGroup, ReactiveFormsModule} from "@angular/forms";
import {FloatLabel} from "primeng/floatlabel";
import {InputText} from "primeng/inputtext";
import {Divider} from "primeng/divider";

@Component({
    selector: 'app-overview',
    imports: [
        ReactiveFormsModule,
        FloatLabel,
        InputText,
        Divider
    ],
    templateUrl: './overview.component.html',
    standalone: true,
    styleUrl: './overview.component.scss'
})
export class OverviewComponent {
    @Input() form!: FormGroup;

}
