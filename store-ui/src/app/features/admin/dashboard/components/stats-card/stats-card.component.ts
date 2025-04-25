import {Component, Input} from '@angular/core';
import {NgClass} from "@angular/common";

@Component({
    selector: 'app-stats-card',
    imports: [
        NgClass
    ],
    templateUrl: './stats-card.component.html',
    standalone: true,
    styleUrl: './stats-card.component.scss'
})
export class StatsCardComponent {
    @Input() title: string = '';
    @Input() value: string = '';
    @Input() icon: string = '';
    @Input() iconColor: string = '';
    @Input() highlightText: string = '';

    get iconClass() {
        return `${this.icon} text-${this.iconColor}-500`;
    }

    get iconBgClass() {
        return `bg-${this.iconColor}-100`;
    }
}
