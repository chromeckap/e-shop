import {ReviewResponse} from "./review-response";

export interface ReviewPageResponse {
    content?: Array<ReviewResponse>;
    first?: boolean;
    last?: boolean;
    number?: number;
    size?: number;
    totalElements?: number;
    totalPages?: number;
}
