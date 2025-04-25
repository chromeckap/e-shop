import {UserResponse} from "./user-response";

export interface UserPageResponse {
    content?: Array<UserResponse>;
    first?: boolean;
    last?: boolean;
    number?: number;
    size?: number;
    totalElements?: number;
    totalPages?: number;
}
