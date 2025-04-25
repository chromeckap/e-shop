import {VariantResponse} from "../variant/variant-response";
import {ProductOverviewResponse} from "./product-overview-response";
import {AttributeResponse} from "../attribute/attribute-response";

export interface ProductResponse {
    id?: number;
    name?: string;
    description?: string;
    price?: number;
    basePrice?: number;
    isPriceEqual?: boolean;
    isVisible?: boolean;
    variants?: VariantResponse[];
    categoryIds?: number[];
    relatedProducts?: ProductOverviewResponse[];
    attributes?: AttributeResponse[];
    imagePaths?: Array<string>;
}
