export interface OrderItemResponse {
    id?: number;
    productId?: number;
    name?: string;
    price?: number;
    quantity?: number;
    totalPrice?: number;
    values?: Record<string, string>[];
}
