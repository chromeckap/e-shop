export interface CategoryOverviewResponse {
    id?: number;
    name?: string;
    description?: string;
    children?: CategoryOverviewResponse[];
}
