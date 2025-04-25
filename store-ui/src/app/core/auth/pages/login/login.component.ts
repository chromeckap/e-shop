import { Component } from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule, Validators} from "@angular/forms";
import {AuthService} from "../../../../services/services/auth.service";
import {ToastService} from "../../../../shared/services/toast.service";
import {Router, RouterLink} from "@angular/router";
import {Button} from "primeng/button";
import {FloatLabel} from "primeng/floatlabel";
import {InputText} from "primeng/inputtext";
import {Password} from "primeng/password";

@Component({
    selector: 'app-login',
    imports: [
        Button,
        FloatLabel,
        InputText,
        ReactiveFormsModule,
        Password,
        RouterLink
    ],
    templateUrl: './login.component.html',
    standalone: true,
    styleUrl: './login.component.scss'
})
export class LoginComponent {
    form: FormGroup;

    constructor(
        private authService: AuthService,
        private toastService: ToastService,
        private formBuilder: FormBuilder,
        private router: Router
    ) {
        this.form = this.formBuilder.group({
            email: ['', [Validators.required, Validators.email]],
            password: ['', [Validators.required]],
        });
    }

    login() {
        if (this.form.invalid) return;

        const login = {
            email: this.form.getRawValue().email,
            password: this.form.getRawValue().password
        };

        this.authService.login(login).subscribe({
            next: async () => {
                try {
                    await this.toastService.showSuccessToast('Úspěch', 'Přihlášení bylo úspěšné.');
                    await this.router.navigate(['/']);
                } catch (error) {
                    console.log("Chyba při zobrazení toastu nebo navigaci:", error);
                }
            },
            error: async (error) => {
                try {
                    await this.toastService.showErrorToast('Chyba', error.error.detail);
                } catch (toastError) {
                    console.log("Chyba při zobrazení chybového toastu:", toastError);
                }
            }
        });
    }

}
