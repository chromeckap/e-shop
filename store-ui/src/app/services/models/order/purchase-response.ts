export interface PurchaseResponse {
    productId?: number;
    variantId?: number;
    name?: string;
    primaryImagePath?: string;
    price?: number;
    quantity?: number;
    availableQuantity?: number;
    isAvailable?: number;
    totalPrice?: number;
    values?: Record<string, string>;
}
