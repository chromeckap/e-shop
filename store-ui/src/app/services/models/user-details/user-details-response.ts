import {AddressResponse} from "../address/address-response";

export interface UserDetailsResponse {
    id?: number;
    userId?: number;
    firstName?: string;
    lastName?: string;
    email?: string;
    address?: AddressResponse;
}
