import {OrderItemResponse} from "./order-item-response";
import {UserDetailsResponse} from "../user-details/user-details-response";

export interface OrderResponse {
    id?: number;
    userDetails?: UserDetailsResponse;
    items?: OrderItemResponse[];
    additionalCosts?: Record<string, number>[];
    totalPrice?: number;
    createTime?: string;
    updateTime?: string;
    status?: string;
}
