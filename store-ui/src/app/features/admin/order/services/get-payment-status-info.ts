import {TagDetails} from "../../../../services/models/tag-details";
import {PaymentResponse} from "../../../../services/models/payment/payment-response";

export function getPaymentStatusInfo(payment: PaymentResponse): TagDetails {
    return ORDER_STATUS_MAP[payment.status!] || { value: 'Chybí definice', severity: 'warn', icon: 'pi pi-exclamation-triangle' };
}

const ORDER_STATUS_MAP: Record<string, TagDetails> = {
    'PAID': { value: 'Zaplacena', severity: 'success', icon: 'pi pi-check-circle' },
    'UNPAID': { value: 'Nezaplacena', severity: 'danger', icon: 'pi pi-times-circle' },
    'CASH_ON_DELIVERY': { value: 'Dobírka', severity: 'secondary', icon: 'pi pi-receipt' },
    'EXPIRED': { value: 'Expirována', severity: 'warn', icon: 'pi pi-calendar-times' }
};
