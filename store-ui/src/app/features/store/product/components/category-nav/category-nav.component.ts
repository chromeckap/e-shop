import {Component, Input} from '@angular/core';
import {MenuItem} from "primeng/api";
import {CategoryResponse} from "../../../../../services/models/category/category-response";
import {Menubar} from "primeng/menubar";
import {Toolbar} from "primeng/toolbar";

@Component({
    selector: 'app-category-nav',
    imports: [
        Menubar,
        Toolbar
    ],
    templateUrl: './category-nav.component.html',
    standalone: true,
    styleUrl: './category-nav.component.scss'
})
export class CategoryNavComponent {
    @Input() category: CategoryResponse = {};
    @Input() childCategories: MenuItem[] = [];

}
