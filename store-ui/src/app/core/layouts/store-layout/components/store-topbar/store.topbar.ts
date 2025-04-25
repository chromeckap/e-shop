import {Component, OnDestroy, OnInit} from '@angular/core';
import {CategoryService} from "../../../../../services/services/category.service";
import {MenuItem} from "primeng/api";
import {CategoryOverviewResponse} from "../../../../../services/models/category/category-overview-response";
import {Router, RouterLink} from "@angular/router";
import {Menubar} from "primeng/menubar";
import {OverlayBadge} from "primeng/overlaybadge";
import {SearchProductTopbar} from "../search-product-topbar/search-product.topbar";
import {ShoppingCartService} from "../../../../../services/services/shopping-cart.service";
import {UserPopoverComponent} from "../../../../auth/components/user-popover/user-popover.component";
import {Subscription} from "rxjs";
import {AuthService} from "../../../../../services/services/auth.service";

@Component({
    selector: 'store-topbar',
    imports: [
        Menubar,
        OverlayBadge,
        RouterLink,
        SearchProductTopbar,
        UserPopoverComponent
    ],
    templateUrl: './store.topbar.html',
    standalone: true,
    styleUrl: './store.topbar.scss'
})
export class StoreTopbar implements OnInit, OnDestroy {
    categories: MenuItem[] = [];
    shoppingCartItemsCount: number = 0;
    private cartSubscription: Subscription | null = null;
    private authSubscription: Subscription | null = null;

    constructor(
        private categoryService: CategoryService,
        private shoppingCartService: ShoppingCartService,
        private authService: AuthService,
        private router: Router
    ) {}

    ngOnInit() {
        this.getAllCategories();

        this.cartSubscription = this.shoppingCartService.cartItemsCount$.subscribe(count => {
            this.shoppingCartItemsCount = count;
        });

        this.authSubscription = this.authService.currentUser$.subscribe(user => {
            if (user?.id) {
                this.loadCart();
            } else {
                this.shoppingCartItemsCount = 0;
            }
        });

        if (this.authService.isLoggedIn)
            this.loadCart();

    }

    ngOnDestroy() {
        if (this.cartSubscription) {
            this.cartSubscription.unsubscribe();
        }
        if (this.authSubscription) {
            this.authSubscription.unsubscribe();
        }
    }

    private loadCart(): void {
        this.shoppingCartService.getCartForCurrentUser().subscribe({
            error: (error) => {
                console.error('Chyba při načítání košíku:', error);
            }
        });
    }

    private getAllCategories() {
        this.categoryService.getAllCategories().subscribe({
            next: (categories) => {
                this.categories = this.convertCategoriesToMenuItems(categories);
            }
        });
    }

    convertCategoriesToMenuItems(categories: CategoryOverviewResponse[]): MenuItem[] {
        if (!categories) return [];

        return categories.map(category => {
            return {
                label: category.name,
                id: category.id?.toString(),
                command: () => {
                    this.router.navigate(['/kategorie/' + category.id + '/produkty'])
                        .catch((error) => {
                            console.log('Při navigaci došlo k chybě:', error);
                        });
                }
            }});
    }

    goToCart() {
        if (!this.authService.isLoggedIn) {
            this.router.navigate(['/prihlaseni'])
                .catch((error) => {
                    console.log('Při navigaci došlo k chybě:', error);
                });

            return;
        }

        this.router.navigate(['/kosik'])
            .catch((error) => {
                console.log('Při navigaci došlo k chybě:', error);
            });
    }
}
