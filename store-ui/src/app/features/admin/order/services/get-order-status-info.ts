import {OrderOverviewResponse} from "../../../../services/models/order/order-overview-response";
import {TagDetails} from "../../../../services/models/tag-details";

export function getOrderStatusInfo(order: OrderOverviewResponse): TagDetails {
    return ORDER_STATUS_MAP[order.status!] || { value: 'Chybí definice', severity: 'warn', icon: 'pi pi-exclamation-triangle' };
}

export const ORDER_STATUS_MAP: Record<string, TagDetails> = {
    'CREATED': { value: 'Vytvořena', severity: 'info', icon: 'pi pi-flag-fill' },
    'SENT': { value: 'Odeslána', severity: 'secondary', icon: 'pi pi-send' },
    'DELIVERED': { value: 'Doručena', severity: 'success', icon: 'pi pi-check-circle' },
    'CANCELED': { value: 'Zrušena', severity: 'danger', icon: 'pi pi-times-circle' }
};

export function getOrderStatusOptions() {
    return Object.entries(ORDER_STATUS_MAP).map(([key, details]) => ({
        label: details.value,
        value: key
    }));
}
