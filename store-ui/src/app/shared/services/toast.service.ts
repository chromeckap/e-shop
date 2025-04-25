import {Injectable} from "@angular/core";
import {MessageService} from "primeng/api";

@Injectable({
    providedIn: 'root'
})
export class ToastService {
    TOAST_KEY = 'global';

    constructor(
        private messageService: MessageService
    ) {}

    async showSuccessToast(summary: string, detail: string) {
        await this.showToast(summary, detail, 'success', 3000);
    }

    async showErrorToast(summary: string, detail: string = "Došlo k chybě.") {
        await this.showToast(summary, detail, 'error', 5000);
    }

    async showToast(summary: string, detail: string, severity: string, life: number) {
        this.messageService.add({
            key: this.TOAST_KEY,
            summary: summary,
            detail: detail,
            severity: severity,
            life: life
        });
    }
}
