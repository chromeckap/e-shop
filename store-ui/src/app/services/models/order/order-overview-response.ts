import {UserDetailsResponse} from "../user-details/user-details-response";

export interface OrderOverviewResponse {
    id?: number;
    userDetails?: UserDetailsResponse;
    totalPrice?: number;
    createTime?: string;
    updateTime?: string;
    status?: string;
}
