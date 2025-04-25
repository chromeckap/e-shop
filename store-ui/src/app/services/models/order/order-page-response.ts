import {OrderOverviewResponse} from "./order-overview-response";

export interface OrderPageResponse {
    content?: Array<OrderOverviewResponse>;
    first?: boolean;
    last?: boolean;
    number?: number;
    size?: number;
    totalElements?: number;
    totalPages?: number;
}
