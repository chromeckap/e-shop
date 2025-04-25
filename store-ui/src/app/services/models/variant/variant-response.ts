import {AttributeValueResponse} from "../attribute/attribute-value-response";

export interface VariantResponse {
    id?: number;
    productId?: number;
    sku?: string;
    basePrice?: number;
    discountedPrice?: number;
    quantity?: number;
    quantityUnlimited?: boolean;
    attributeValues?: AttributeValueResponse[];
}
