export interface ProductOverviewResponse {
    id?: number;
    name?: string;
    price?: number;
    basePrice?: number;
    isPriceEqual?: boolean;
    isVisible?: boolean;
    categoryIds?: number[];
    relatedProductIds?: number[];
    primaryImagePath?: string;
}
