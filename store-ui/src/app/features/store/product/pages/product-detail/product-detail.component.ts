import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ProductResponse} from "../../../../../services/models/product/product-response";
import {ProductService} from "../../../../../services/services/product.service";
import {ActivatedRoute} from "@angular/router";
import {RecommendationService} from "../../../../../services/services/recommendation.service";
import {ProductOverviewResponse} from "../../../../../services/models/product/product-overview-response";
import {RecommenderCarouselComponent} from "../../components/recommender-carousel/recommender-carousel.component";
import {Subscription} from "rxjs";
import {ReviewContainerComponent} from "../../../review/components/review-container/review-container.component";
import {ProductInfoComponent} from "../../components/product-info/product-info.component";

@Component({
    selector: 'app-product-detail',
    imports: [
        RecommenderCarouselComponent,
        ReviewContainerComponent,
        ProductInfoComponent
    ],
    templateUrl: './product-detail.component.html',
    standalone: true,
    styleUrl: './product-detail.component.scss'
})
export class ProductDetailComponent implements OnInit, OnDestroy {
    product: ProductResponse = {};
    recommendedProducts: ProductOverviewResponse[] = [];
    @ViewChild(ReviewContainerComponent) reviewsPage!: ReviewContainerComponent;
    @ViewChild(ProductInfoComponent) productInfo!: ProductInfoComponent;


    private routeSubscription: Subscription | undefined;

    constructor(
        private productService: ProductService,
        private recommendationService: RecommendationService,
        private activatedRoute: ActivatedRoute,
    ) {}

    ngOnInit(): void {
        this.routeSubscription = this.activatedRoute.params.subscribe(params => {
            const id = +params['id'];
            if (id) {
                this.loadData(id);
            }
        });
    }

    ngOnDestroy(): void {
        this.routeSubscription?.unsubscribe();
    }


    private loadData(id: number) {
        if (!id) return;

        this.productService.getProductById(id)
            .subscribe(product => {
                this.product = product;
                this.reviewsPage.setReviewsByProductId(id);

                this.productInfo.loadData(product);
            });

        this.recommendationService.getRecommendationsByProductId(id)
            .subscribe(recommendedProducts => {
                this.recommendedProducts = recommendedProducts;
            });
    }
}
