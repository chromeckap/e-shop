import {TagDetails} from "../../../../services/models/tag-details";
import {UserResponse} from "../../../../services/models/user/user-response";

export function getRoleInfo(user: UserResponse): TagDetails {
    return ROLE_STATUS_MAP[user.role!] || { value: 'Chybí role', severity: 'warn', icon: 'pi pi-exclamation-triangle' };
}

const ROLE_STATUS_MAP: Record<string, TagDetails> = {
    'ADMIN': { value: 'Administrátor', severity: 'danger', icon: 'pi pi-crown' },
    'CUSTOMER': { value: 'Zákazník', severity: 'secondary', icon: 'pi pi-user' }
};
