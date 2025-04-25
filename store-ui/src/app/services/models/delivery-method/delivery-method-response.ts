export interface DeliveryMethodResponse {
    id?: number;
    name?: string;
    courierType?: Map<string, string>;
    isActive?: boolean;
    price?: number;
    isFreeForOrderAbove?: boolean;
    freeForOrderAbove?: number;
}
