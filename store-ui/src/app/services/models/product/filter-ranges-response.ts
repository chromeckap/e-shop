import {AttributeResponse} from "../attribute/attribute-response";

export interface FilterRangesResponse {
    lowPrice?: number;
    maxPrice?: number;
    attributes?: AttributeResponse[];
}
