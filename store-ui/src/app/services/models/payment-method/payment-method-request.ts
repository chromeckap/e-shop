export interface PaymentMethodRequest {
    id?: number;
    name?: string;
    type?: string;
    isActive?: boolean;
    price?: number;
    isFreeForOrderAbove?: boolean;
    freeForOrderAbove?: number;
}
