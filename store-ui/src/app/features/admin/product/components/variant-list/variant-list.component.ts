import {Component, Input, OnInit} from '@angular/core';
import {VariantResponse} from "../../../../../services/models/variant/variant-response";
import {FormGroup, FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Divider} from "primeng/divider";
import {TableModule} from "primeng/table";
import {Tag} from "primeng/tag";
import {IconField} from "primeng/iconfield";
import {CustomCurrencyPipe} from "../../../../../shared/pipes/CustomCurrencyPipe";
import {Button} from "primeng/button";
import {InputText} from "primeng/inputtext";
import {Dialog} from "primeng/dialog";
import {AttributeResponse} from "../../../../../services/models/attribute/attribute-response";
import {InputGroup} from "primeng/inputgroup";
import {InputGroupAddon} from "primeng/inputgroupaddon";
import {InputNumber} from "primeng/inputnumber";
import {Select} from "primeng/select";
import {ToggleButton} from "primeng/togglebutton";
import {AttributeValueResponse} from "../../../../../services/models/attribute/attribute-value-response";
import {VariantService} from "../../../../../services/services/variant.service";
import { VariantRequest } from '../../../../../services/models/variant/variant-request';
import {forkJoin, Observable, of, tap} from "rxjs";
import {ToastService} from "../../../../../shared/services/toast.service";

@Component({
    selector: 'app-variant-list',
    imports: [
        Divider,
        TableModule,
        Tag,
        IconField,
        CustomCurrencyPipe,
        Button,
        InputText,
        ReactiveFormsModule,
        Dialog,
        FormsModule,
        InputGroup,
        InputGroupAddon,
        InputNumber,
        Select,
        ToggleButton
    ],
    templateUrl: './variant-list.component.html',
    standalone: true,
    styleUrl: './variant-list.component.scss'
})
export class VariantListComponent implements OnInit {
    @Input() form!: FormGroup;

    storedVariants: VariantResponse[] = [];
    editDialogVisible = false;
    editingVariant: VariantResponse = {};
    variantData: VariantResponse = {
        sku: '',
        quantity: 0,
        quantityUnlimited: false,
        basePrice: 0,
        discountedPrice: 0,
        attributeValues: []
    };

    constructor(
        private variantService: VariantService,
        private toastService: ToastService
    ) {}

    ngOnInit(): void {
        this.editingVariant = this.variantData;
    }

    setStoredVariants(variants: VariantResponse[]) {
        this.storedVariants = variants;
    }

    openDialog(variant: VariantResponse) {
        this.editDialogVisible = true;
        this.editingVariant = {...variant};
    }

    async addVariant() {
        const variantsCount = (this.form.get('variants')?.value).length;
        const attributesCount = (this.form.get('attributes')?.value).length;
        const availableVariants = variantsCount * attributesCount;

        if (availableVariants < variantsCount) {
            await this.toastService.showErrorToast('Chyba', 'Počet variant je maximální, přidej případně atribut.');
            return;
        }

        this.editDialogVisible = true;
        this.editingVariant = {...this.variantData};
    }

    saveVariant() {
        const currentVariants = this.form.get('variants')?.value;

        if (this.editingVariant.id) {
            const index = currentVariants.findIndex((v: VariantResponse) => v.id === this.editingVariant.id);
            if (index > -1) {
                currentVariants[index] = {...this.editingVariant};
            }
        } else {
            this.editingVariant.id = Math.max(0, ...currentVariants.map((v: VariantResponse) => v.id ?? 0)) + 1;
            currentVariants.push({...this.editingVariant});
        }
        this.editDialogVisible = false;
        this.form.get('variants')?.setValue(currentVariants);
    }

    deleteVariant(variant: VariantResponse) {
        const currentVariants = this.form.get('variants')?.value;
        const updatedVariants = currentVariants.filter(((v: VariantResponse) => v.id !== variant.id));

        this.form.get('variants')?.setValue(updatedVariants);
    }

    getAttributeValue(variant: VariantResponse, attribute: AttributeResponse): string {
        if (!variant.attributeValues || typeof variant.attributeValues !== 'object')
            return "Chybí";

        const attributeValue = variant.attributeValues[attribute.id!];
        return attributeValue ? attributeValue.value! : "Chybí";
    }

    getAttributeValues(attributeId: number) {
        const attributes = this.form.get('attributes')?.value;
        return attributes.find((a: AttributeValueResponse) => a.id === attributeId).values || [];
    }

    manageVariants(productId: number): Observable<any>  {
        const oldVariants = this.storedVariants;
        const newVariants = this.form.get('variants')?.value;

        const convertedVariants: VariantRequest[] = []; // Seznam pro převedené varianty

        newVariants.forEach((variant: VariantResponse) => {
            const attributeValueIds = variant.attributeValues
                ? Object.values(variant.attributeValues).map((attributeValue: any) => attributeValue.id)
                : [];

            const variantRequest: VariantRequest = {
                id: variant.id,
                productId: productId,
                sku: variant.sku,
                basePrice: variant.basePrice,
                discountedPrice: variant.discountedPrice,
                quantity: variant.quantity,
                quantityUnlimited: variant.quantityUnlimited,
                attributeValueIds: attributeValueIds
            };
            convertedVariants.push(variantRequest);
        });

        const oldIds = new Set(oldVariants.map(o => o.id));
        const newIds = new Set(convertedVariants.map((v: VariantRequest) => v.id));

        const toCreate = convertedVariants.filter((v: VariantRequest) => !v.id || !oldIds.has(v.id));
        const toUpdate = convertedVariants.filter((v: VariantRequest) => oldIds.has(v.id));
        const toDelete = oldVariants.filter(o => !newIds.has(o.id));

        const createObservables = toCreate.map((variant: VariantRequest) => {
            return this.variantService.createVariant(variant);
        });

        const updateObservables = toUpdate.map((variant: VariantRequest) => {
            return this.variantService.updateVariant(variant.id!, variant);
        });

        const deleteObservables = toDelete.map(variant => {
            return this.variantService.deleteVariantById(variant.id!)
        });

        const observables = [...createObservables, ...updateObservables, ...deleteObservables];

        if (observables.length <= 0) return of(null);

        return forkJoin({
            created: createObservables.length ? forkJoin(createObservables) : of([]),
            updated: updateObservables.length ? forkJoin(updateObservables) : of([]),
            deleted: deleteObservables.length ? forkJoin(deleteObservables) : of([]),
        }).pipe(
            tap({
                next: async () => {
                    await this.toastService.showSuccessToast('Úspěch', 'Varianty byly úspěšně uloženy.');
                },
                error: async (error) => {
                    console.error('Failed to process variants', error);
                    await this.toastService.showErrorToast('Chyba', error.error.detail);
                }
            })
        );
    }
}
