import {Component, EventEmitter, Input, Output} from '@angular/core';
import {PurchaseResponse} from "../../../../../services/models/order/purchase-response";
import {TableModule} from "primeng/table";
import {ProductService} from "../../../../../services/services/product.service";
import {Router} from "@angular/router";
import {CustomCurrencyPipe} from "../../../../../shared/pipes/CustomCurrencyPipe";
import {Button} from "primeng/button";
import {CartResponse} from "../../../../../services/models/shopping-cart/cart-response";
import {InputNumber} from "primeng/inputnumber";
import {FormsModule} from "@angular/forms";
import {Tag} from "primeng/tag";

@Component({
    selector: 'app-cart-item-list',
    imports: [
        TableModule,
        CustomCurrencyPipe,
        Button,
        InputNumber,
        FormsModule,
        Tag
    ],
    templateUrl: './cart-item-list.component.html',
    standalone: true,
    styleUrl: './cart-item-list.component.scss'
})
export class CartItemListComponent {
    @Input() cart!: CartResponse;
    @Output() removeItemFromCart = new EventEmitter<number>();
    @Output() changeItemQuantity = new EventEmitter<{ variantId: number, quantity: number }>();
    minQuantity: number = 1;
    maxQuantity: number = 50;

    constructor(
        private productService: ProductService,
        private router: Router
    ) {}

    getImage(item: PurchaseResponse) {
        return item.primaryImagePath
            ? this.productService.getImage(item.productId!, item.primaryImagePath)
            : 'assets/img/image-not-found.png';
    }

    navigateToProduct(item: any) {
        this.router.navigate(['/produkt/' + item.productId])
            .catch((error) => {
                console.log('Při navigaci došlo k chybě:', error);
            });
    }

    removeCartItem(item: PurchaseResponse) {
        this.removeItemFromCart.emit(item.variantId);
    }

    objectKeys(object: any) {
        return Object.keys(object);
    }

    onInputNumberChange(event: any, item: any) {
        if (event.value !== null && event.value !== undefined) {
            const newQuantity = Number(event.value - event.formattedValue);

            if (newQuantity > this.maxQuantity) return;
            if (isNaN(newQuantity)) return;

            this.changeItemQuantity.emit({
                variantId: item.variantId,
                quantity: newQuantity
            });
        }
    }
}
