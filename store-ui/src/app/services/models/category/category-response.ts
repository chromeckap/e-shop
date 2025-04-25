import {CategoryOverviewResponse} from "./category-overview-response";

export interface CategoryResponse {
    id?: number;
    name?: string;
    description?: string;
    parent?: CategoryOverviewResponse;
    children?: CategoryOverviewResponse[];
}
