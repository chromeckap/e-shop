import { Component } from '@angular/core';
import {
    FormBuilder,
    FormGroup,
    ReactiveFormsModule,
    Validators
} from "@angular/forms";
import {FloatLabel} from "primeng/floatlabel";
import {InputText} from "primeng/inputtext";
import {Password} from "primeng/password";
import {Router, RouterLink} from "@angular/router";
import {Button} from "primeng/button";
import {AuthService} from "../../../../services/services/auth.service";
import {ToastService} from "../../../../shared/services/toast.service";
import {passwordMatchValidator} from "../../../../shared/validators/password-validator";

@Component({
    selector: 'app-register',
    imports: [
        ReactiveFormsModule,
        FloatLabel,
        InputText,
        Password,
        RouterLink,
        Button
    ],
    templateUrl: './register.component.html',
    standalone: true,
    styleUrl: './register.component.scss'
})
export class RegisterComponent {
    form: FormGroup;
    minLengthFirstName = 2;
    maxLengthFirstName = 50;
    minLengthLastName = 2;
    maxLengthLastName = 50;
    minLengthPassword = 6;

    constructor(
        private authService: AuthService,
        private toastService: ToastService,
        private formBuilder: FormBuilder,
        private router: Router

    ) {
        this.form = this.formBuilder.group({
            firstName: ['', [
                Validators.required,
                Validators.minLength(this.minLengthFirstName),
                Validators.maxLength(this.maxLengthFirstName)
            ]],
            lastName: ['', [
                Validators.required,
                Validators.minLength(this.minLengthLastName),
                Validators.maxLength(this.maxLengthLastName)
            ]],
            email: ['', [
                Validators.required, Validators.email
            ]],
            password: ['', [
                Validators.required,
                Validators.minLength(this.minLengthPassword),
            ]],
            confirmPassword: ['', [
                Validators.required,
                Validators.minLength(this.minLengthPassword),
            ]]
        }, { validators: passwordMatchValidator });
    }

    register() {
        if (this.form.invalid) return;

        const register = {
          firstName: this.form.getRawValue().firstName,
          lastName: this.form.getRawValue().lastName,
          email: this.form.getRawValue().email,
          password: this.form.getRawValue().password,
          confirmPassword: this.form.getRawValue().confirmPassword
        };

        this.authService.register(register).subscribe({
            next: async () => {
                try {
                    await this.toastService.showSuccessToast('Úspěch', 'Registrace byla úspěšná.');
                    await this.router.navigate(['/prihlaseni']);
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
