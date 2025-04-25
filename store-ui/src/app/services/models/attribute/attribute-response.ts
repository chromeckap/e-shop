import {AttributeValueResponse} from "./attribute-value-response";

export interface AttributeResponse {
    id: number;
    name: string;
    values: AttributeValueResponse[];
}
