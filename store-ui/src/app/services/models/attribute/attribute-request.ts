import {AttributeValueRequest} from "./attribute-value-request";

export interface AttributeRequest {
    id?: number;
    name?: string;
    values?: AttributeValueRequest[];
}
