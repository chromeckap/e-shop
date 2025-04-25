import {PurchaseResponse} from "../order/purchase-response";

export interface CartResponse {
    id?: number;
    userId?: number;
    totalPrice?: number;
    items?: PurchaseResponse[];
}
