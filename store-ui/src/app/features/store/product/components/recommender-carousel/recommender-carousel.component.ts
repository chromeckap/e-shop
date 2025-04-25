import {Component, Input, OnInit} from '@angular/core';
import {ProductOverviewResponse} from "../../../../../services/models/product/product-overview-response";
import {Carousel, CarouselResponsiveOptions} from "primeng/carousel";
import {ProductCardComponent} from "../product-card/product-card.component";

@Component({
    selector: 'app-recommender-carousel',
    imports: [
        Carousel,
        ProductCardComponent
    ],
    templateUrl: './recommender-carousel.component.html',
    standalone: true,
    styleUrl: './recommender-carousel.component.scss'
})
export class RecommenderCarouselComponent implements OnInit {
    @Input() products: ProductOverviewResponse[] = [];
    responsiveOptions: CarouselResponsiveOptions[] | undefined;

    ngOnInit(): void {
        this.responsiveOptions = [
            {
                breakpoint: '1850px',
                numVisible: 4,
                numScroll: 4
            },
            {
                breakpoint: '1470px',
                numVisible: 3,
                numScroll: 3
            },
            {
                breakpoint: '1192px',
                numVisible: 2,
                numScroll: 2
            },
            {
                breakpoint: '880px',
                numVisible: 1,
                numScroll: 1
            }
        ]
    }
}
