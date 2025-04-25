import {Component, Input} from '@angular/core';
import {FormGroup, ReactiveFormsModule} from "@angular/forms";
import {CategoryOverviewResponse} from "../../../../../services/models/category/category-overview-response";
import {Divider} from "primeng/divider";
import {FloatLabel} from "primeng/floatlabel";
import {TreeSelect} from "primeng/treeselect";

@Component({
    selector: 'app-parent-select',
    imports: [
        Divider,
        FloatLabel,
        ReactiveFormsModule,
        TreeSelect
    ],
    templateUrl: './parent-select.component.html',
    standalone: true,
    styleUrl: './parent-select.component.scss'
})
export class ParentSelectComponent {
    @Input() form!: FormGroup;
    @Input() categories: CategoryOverviewResponse[] = [];
}
