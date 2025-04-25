import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {DeliveryMethodResponse} from "../../../../../services/models/delivery-method/delivery-method-response";
import {DeliveryMethodService} from "../../../../../services/services/delivery-method.service";
import {CustomCurrencyPipe} from "../../../../../shared/pipes/CustomCurrencyPipe";
import {RadioButton} from "primeng/radiobutton";
import {Tag} from "primeng/tag";
import {NgClass} from "@angular/common";
import {Dialog} from "primeng/dialog";
import {DomSanitizer, SafeResourceUrl} from "@angular/platform-browser";

@Component({
    selector: 'app-delivery-select',
    imports: [
        CustomCurrencyPipe,
        FormsModule,
        RadioButton,
        ReactiveFormsModule,
        Tag,
        NgClass,
        Dialog
    ],
    templateUrl: './delivery-select.component.html',
    standalone: true,
    styleUrl: './delivery-select.component.scss'
})
export class DeliverySelectComponent implements OnInit, OnDestroy {
    @Input() form!: FormGroup;
    @Input() cartTotal!: number;
    availableDeliveryMethods: DeliveryMethodResponse[] = [];
    dialogVisible: boolean = false;
    sanitizedWidgetUrl: SafeResourceUrl | undefined;
    selectedPlaceName: string = '';

    constructor(
        private deliveryMethodService: DeliveryMethodService,
        private sanitizer: DomSanitizer
    ) {}

    ngOnInit(): void {
        window.addEventListener('message', this.handleDeliveryMessage);

        this.deliveryMethodService.getActiveDeliveryMethods().subscribe({
            next: (deliveryMethods) => {
                this.availableDeliveryMethods = deliveryMethods;
            },
            error: (error) => {
                console.error('Při načítání dostupných metod pro doručení došlo k chybě:', error);
            }
        });
    }

    ngOnDestroy(): void {
        window.removeEventListener('message', this.handleDeliveryMessage);
    }

    handleDeliveryMessage = (event: MessageEvent) => {
        const data = event.data;
        if (!data.packetaSelectedData && !data.point) return;

        if (data.point) { //Balikovna
            this.selectedPlaceName = data.point.name;

            const addressParts = data.point.address.split(',').map((part: string) => part.trim());
            this.form.get('street')?.setValue(addressParts[0]);
            this.form.get('city')?.setValue(addressParts[2]);
            this.form.get('postalCode')?.setValue(addressParts[1]);

        } else if (data.packetaSelectedData) { //Packeta
            this.selectedPlaceName = data.packetaSelectedData.place;
            this.form.get('street')?.setValue(data.packetaSelectedData.street);
            this.form.get('city')?.setValue(data.packetaSelectedData.city);
            this.form.get('postalCode')?.setValue(data.packetaSelectedData.zip);
        }

        this.dialogVisible = false;
    }

    openWidgetPanel(courierType: any) {
        if (!courierType) return;

        this.selectedPlaceName = '';

        this.deliveryMethodService.getCourierWidgetUrl(courierType.type).subscribe({
            next: (widgetUrl) => {
                if (!widgetUrl) {
                    this.form.get('isManualAddressRequired')?.setValue(true);
                    this.form.get('street')?.reset();
                    this.form.get('city')?.reset();
                    this.form.get('postalCode')?.reset();
                    return;
                }

                this.form.get('isManualAddressRequired')?.setValue(false);
                this.sanitizedWidgetUrl = this.sanitizer.bypassSecurityTrustResourceUrl(widgetUrl);
                this.dialogVisible = true;
            },
            error: (error) => {
                console.error('Při načítání widgetu doručení došlo k chybě:', error);
            }
        });
    }

    onDialogDragEnd() {
        this.form.get('deliveryMethod')?.reset();
        this.form.get('street')?.reset();
        this.form.get('city')?.reset();
        this.form.get('postalCode')?.reset();
        this.form.get('isManualAddressRequired')?.reset();

        this.selectedPlaceName = '';
    }

    getDeliveryPrice(deliveryMethod: DeliveryMethodResponse) {
        if (deliveryMethod.price !== 0) {
            if (deliveryMethod.isFreeForOrderAbove === false)
                return true;

            return deliveryMethod.isFreeForOrderAbove! && deliveryMethod.freeForOrderAbove! > this.cartTotal;
        }
        return false;
    }
}
