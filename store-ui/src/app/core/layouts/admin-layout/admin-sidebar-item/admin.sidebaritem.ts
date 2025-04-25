import {Component, HostBinding, Input, OnDestroy, OnInit} from '@angular/core';
import {NgClass, NgForOf, NgIf} from "@angular/common";
import {Ripple} from "primeng/ripple";
import {NavigationEnd, Router, RouterLink, RouterLinkActive} from "@angular/router";
import {animate, state, style, transition, trigger} from "@angular/animations";
import {MenuItem} from "primeng/api";
import {Subscription} from "rxjs";
import {LayoutService} from "../../services/layout.service";
import {filter} from "rxjs/operators";

@Component({
    selector: '[admin-sidebar-item]',
    imports: [
        Ripple,
        RouterLinkActive,
        NgClass,
        RouterLink,
        NgForOf,
        NgIf
    ],
    templateUrl: './admin.sidebaritem.html',
    standalone: true,
    animations: [
        trigger('children', [
            state('collapsed', style({height: '0'})),
            state('expanded', style({height: '*'})),
            transition('collapsed <=> expanded', animate('400ms cubic-bezier(0.86, 0, 0.07, 1)'))
        ])
    ]
})
export class AdminSidebarItem implements OnInit, OnDestroy {
    @Input() item!: MenuItem;
    @Input() index!: number;
    @Input() @HostBinding('class.layout-root-menuitem') root!: boolean;
    @Input() parentKey!: string;

    active = false;
    menuSourceSubscription: Subscription;
    menuResetSubscription: Subscription;
    key: string = '';

    constructor(
        public router: Router,
        private layoutService: LayoutService
    ) {
        this.menuSourceSubscription = this.layoutService.menuSource$.subscribe((value) => {
            Promise.resolve(null).then(() => {
                if (value.routeEvent) {
                    this.active = value.key === this.key || value.key.startsWith(this.key + '-');
                } else {
                    if (value.key !== this.key && !value.key.startsWith(this.key + '-')) {
                        this.active = false;
                    }
                }
            });
        });

        this.menuResetSubscription = this.layoutService.resetSource$.subscribe(() => {
            this.active = false;
        });

        this.router.events.pipe(filter((event) => event instanceof NavigationEnd)).subscribe((params) => {
            if (this.item.routerLink) {
                this.updateActiveStateFromRoute();
            }
        });
    }

    ngOnInit() {
        this.key = this.parentKey ? this.parentKey + '-' + this.index : String(this.index);

        if (this.item.routerLink) {
            this.updateActiveStateFromRoute();
        }
    }

    updateActiveStateFromRoute() {
        let activeRoute = this.router.isActive(this.item.routerLink[0], { paths: 'exact', queryParams: 'ignored', matrixParams: 'ignored', fragment: 'ignored' });

        if (activeRoute) {
            this.layoutService.onMenuStateChange({ key: this.key, routeEvent: true });
        }
    }

    itemClick(event: Event) {
        if (this.item.disabled) {
            event.preventDefault();
            return;
        }

        if (this.item.command) {
            this.item.command({ originalEvent: event, item: this.item });
        }

        if (this.item.items) {
            this.active = !this.active;
        }

        this.layoutService.onMenuStateChange({ key: this.key });
    }

    get submenuAnimation() {
        return this.root ? 'expanded' : this.active ? 'expanded' : 'collapsed';
    }

    @HostBinding('class.active-menuitem')
    get activeClass() {
        return this.active && !this.root;
    }

    ngOnDestroy() {
        if (this.menuSourceSubscription) {
            this.menuSourceSubscription.unsubscribe();
        }

        if (this.menuResetSubscription) {
            this.menuResetSubscription.unsubscribe();
        }
    }
}
