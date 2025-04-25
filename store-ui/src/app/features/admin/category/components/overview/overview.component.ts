import {Component, Input} from '@angular/core';
import {FormGroup, ReactiveFormsModule} from "@angular/forms";
import {Divider} from "primeng/divider";
import {FloatLabel} from "primeng/floatlabel";
import {InputText} from "primeng/inputtext";
import {Textarea} from "primeng/textarea";

@Component({
    selector: 'app-overview',
    imports: [
        Divider,
        FloatLabel,
        InputText,
        ReactiveFormsModule,
        Textarea
    ],
    templateUrl: './overview.component.html',
    standalone: true,
    styleUrl: './overview.component.scss'
})
export class OverviewComponent {
    @Input() form!: FormGroup;

}
