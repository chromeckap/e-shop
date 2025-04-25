import {UserDetailsRequest} from "../user-details/user-details-request";
import {PurchaseRequest} from "./purchase-request";

export interface OrderRequest {
    id?: number;
    userDetails?: UserDetailsRequest;
    products?: PurchaseRequest[];
    paymentMethodId?: number;
    deliveryMethodId?: number;
}
