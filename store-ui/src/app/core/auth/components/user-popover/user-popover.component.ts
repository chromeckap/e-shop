import {Component, ViewChild} from '@angular/core';
import {Router, RouterLink} from "@angular/router";
import {Popover} from "primeng/popover";
import {NgClass} from "@angular/common";
import {AuthService} from "../../../../services/services/auth.service";
import {ToastService} from "../../../../shared/services/toast.service";
import {Divider} from "primeng/divider";

@Component({
    selector: 'app-user-popover',
    imports: [
        RouterLink,
        Popover,
        NgClass,
        Divider
    ],
    templateUrl: './user-popover.component.html',
    standalone: true,
    styleUrl: './user-popover.component.scss'
})
export class UserPopoverComponent {
    isPopoverOpen = false;
    @ViewChild('popover') popover!: Popover;

    constructor(
        protected authService: AuthService,
        private toastService: ToastService,
        private router: Router
    ) {}

    toggle(event: Event, popover: any) {
        this.isPopoverOpen = !this.isPopoverOpen;
        popover.toggle(event);
    }

    logout() {
        this.authService.logout().subscribe({
            next: async () => {
                try {
                    await this.toastService.showSuccessToast('Úspěch', 'Byl jsi úspěšně odhlášen.');
                    await this.router.navigate(['/']);
                } catch (error) {
                    console.log("Chyba při zobrazení toastu nebo navigaci:", error);
                }
            },
            error: async (error) => {
                console.log(error);
                try {
                    await this.toastService.showErrorToast('Chyba', error.error.detail);
                } catch (toastError) {
                    console.log("Chyba při zobrazení chybového toastu:", toastError);
                }
            }
        });
    }
}
