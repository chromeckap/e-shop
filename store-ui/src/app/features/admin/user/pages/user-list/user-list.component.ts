import {Component, OnInit} from '@angular/core';
import {UserPageResponse} from "../../../../../services/models/user/user-page-response";
import {UserService} from "../../../../../services/services/user.service";
import {ConfirmationService} from "primeng/api";
import {UserResponse} from "../../../../../services/models/user/user-response";
import {getRoleInfo} from "../../services/get-role-info";
import {TableModule} from "primeng/table";
import {Tag} from "primeng/tag";
import {Button} from "primeng/button";
import {ConfirmDialog} from "primeng/confirmdialog";
import {ToastService} from "../../../../../shared/services/toast.service";

@Component({
    selector: 'app-user-list',
    imports: [
        TableModule,
        Tag,
        Button,
        ConfirmDialog
    ],
    templateUrl: './user-list.component.html',
    standalone: true,
    styleUrl: './user-list.component.scss',
    providers: [ConfirmationService]
})
export class UserListComponent implements OnInit {
    userPage: UserPageResponse = {};
    page = 0;
    size = 10;
    attribute = 'id';
    direction = 'desc';

    constructor(
        private userService: UserService,
        private confirmationService: ConfirmationService,
        private toastService: ToastService
    ) {}

    ngOnInit(): void {
        this.getAllUsers();
    }

    private getAllUsers() {
        this.userService.getAllUsers({
            pageNumber: this.page,
            pageSize: this.size,
            attribute: this.attribute,
            direction: this.direction
        })
            .subscribe({
                next: (users) => {
                    this.userPage = users;
                }
            });
    }

    getRoleInfo(user: UserResponse) {
        return getRoleInfo(user);
    }

    getFullName(user: UserResponse) {
        return user.firstName + " " + user.lastName;
    }

    onPageChange(event: any) {
        this.page = event.first / event.rows;
        this.size = event.rows;
        this.getAllUsers();
    }

    deleteUser(user: UserResponse) {
        this.confirmationService.confirm({
            header: 'Potvrzení odstranění',
            icon: 'pi pi-exclamation-triangle',
            message: 'Opravdu chceš odstranit uživatele ' + this.getFullName(user) + "?",
            acceptLabel: 'Ano',
            acceptIcon: 'pi pi-check',
            acceptButtonStyleClass: 'p-button-danger p-button-outlined',
            rejectLabel: 'Ne',
            rejectIcon: 'pi pi-check',
            rejectButtonStyleClass: 'p-button-secondary p-button-outlined',

            accept: () => {
                this.userService.deleteUserById(user.id!).subscribe({
                    next: async () => {
                        try {
                            this.getAllUsers();
                            await this.toastService.showSuccessToast('Úspěch', 'Uživatel byl úspěšně odstraněn.');
                        } catch (error) {
                            console.log("Chyba při operacích po odstranění:", error);
                        }
                    },
                    error: async (error) => {
                        console.log(error);
                        try {
                            await this.toastService.showErrorToast('Chyba', error.error.detail);
                        } catch (toastError) {
                            console.log("Chyba při zobrazení toastu:", toastError);
                        }
                    }
                });
            }
        });
    }

    changeUserRole(user: UserResponse) {
        const requiredRole = user.role === 'ADMIN' ? 'CUSTOMER' : 'ADMIN';

        this.confirmationService.confirm({
            header: 'Potvrzení změny role',
            icon: 'pi pi-exclamation-triangle',
            message: 'Opravdu chceš uživateli ' + this.getFullName(user) + " udělit roli " + requiredRole + "?",
            acceptLabel: 'Ano',
            acceptIcon: 'pi pi-check',
            acceptButtonStyleClass: 'p-button-success p-button-outlined',
            rejectLabel: 'Ne',
            rejectIcon: 'pi pi-check',
            rejectButtonStyleClass: 'p-button-secondary p-button-outlined',

            accept: () => {
                this.userService.updateUserRole(user.id!, requiredRole).subscribe({
                    next: async () => {
                        try {
                            this.getAllUsers();
                            await this.toastService.showSuccessToast('Úspěch', 'Role uživatele byla úspěšně změněna.');
                        } catch (error) {
                            console.log("Chyba při operacích po odstranění:", error);
                        }
                    },
                    error: async (error) => {
                        console.log(error);
                        try {
                            await this.toastService.showErrorToast('Chyba', error.error.detail);
                        } catch (toastError) {
                            console.log("Chyba při zobrazení toastu:", toastError);
                        }
                    }
                });
            }
        });
    }

}
