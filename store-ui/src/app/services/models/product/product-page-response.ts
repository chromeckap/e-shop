import {ProductOverviewResponse} from "./product-overview-response";

export interface ProductPageResponse {
    content?: Array<ProductOverviewResponse>;
    first?: boolean;
    last?: boolean;
    number?: number;
    size?: number;
    totalElements?: number;
    totalPages?: number;
}
