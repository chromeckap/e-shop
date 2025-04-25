import {Component, Input} from '@angular/core';
import {FormGroup, ReactiveFormsModule} from "@angular/forms";
import {Divider} from "primeng/divider";
import {FloatLabel} from "primeng/floatlabel";
import {InputGroup} from "primeng/inputgroup";
import {InputGroupAddon} from "primeng/inputgroupaddon";
import {InputNumber} from "primeng/inputnumber";
import {InputText} from "primeng/inputtext";
import {ToggleButton} from "primeng/togglebutton";

@Component({
    selector: 'app-overview',
    imports: [
        Divider,
        FloatLabel,
        InputGroup,
        InputGroupAddon,
        InputNumber,
        InputText,
        ReactiveFormsModule,
        ToggleButton
    ],
    templateUrl: './overview.component.html',
    standalone: true,
    styleUrl: './overview.component.scss'
})
export class OverviewComponent {
    @Input() form!: FormGroup;
}
