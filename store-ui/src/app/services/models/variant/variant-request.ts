export interface VariantRequest {
    id?: number;
    productId?: number;
    sku?: string;
    basePrice?: number;
    discountedPrice?: number;
    quantity?: number;
    quantityUnlimited?: boolean;
    attributeValueIds?: number[];
}
