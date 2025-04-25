export interface ProductRequest {
    id?: number;
    name?: string;
    description?: string;
    isVisible?: boolean;
    categoryIds?: number[];
    attributeIds?: number[];
    relatedProductIds?: number[];
}
