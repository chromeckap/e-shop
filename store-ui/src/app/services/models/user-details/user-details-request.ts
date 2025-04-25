import {AddressRequest} from "../address/address-request";

export interface UserDetailsRequest {
    id?: number;
    firstName?: string;
    lastName?: string;
    email?: string;
    address?: AddressRequest;
}
