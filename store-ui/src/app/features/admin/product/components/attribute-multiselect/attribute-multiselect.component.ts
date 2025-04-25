import {Component, Input, OnInit} from '@angular/core';
import {Divider} from "primeng/divider";
import {FormGroup, ReactiveFormsModule} from "@angular/forms";
import {AttributeResponse} from "../../../../../services/models/attribute/attribute-response";
import {AttributeService} from "../../../../../services/services/attribute.service";
import {MultiSelect} from "primeng/multiselect";
import {VariantResponse} from "../../../../../services/models/variant/variant-response";

@Component({
    selector: 'app-attribute-multiselect',
    imports: [
        Divider,
        ReactiveFormsModule,
        MultiSelect
    ],
    templateUrl: './attribute-multiselect.component.html',
    standalone: true,
    styleUrl: './attribute-multiselect.component.scss'
})
export class AttributeMultiSelectComponent implements OnInit {
    @Input() form!: FormGroup;
    attributes: AttributeResponse[] = [];

    constructor(
        private attributeService: AttributeService
    ) {}

    ngOnInit(): void {
        this.getAllAttributes();
    }

    private getAllAttributes() {
        this.attributeService.getAllAttributes().subscribe({
            next: (attributes) => {
                this.attributes = attributes;
            }
        });
    }

    updateVariantValues() {
        const currentAttributes = this.form.controls['attributes'].value;
        const currentVariants = this.form.controls['variants'].value;

        const currentAttributeIds = currentAttributes.map((attr: AttributeResponse) => attr.id);

        currentVariants.forEach((variant: VariantResponse) => {

            variant.attributeValues = Object.fromEntries(
                Object.values(variant.attributeValues || {}).filter((attrValue: any) =>
                    currentAttributeIds.includes(attrValue.attributeId)
                ).map(attrValue => [attrValue.attributeId, attrValue])
            );
        });
        this.form.controls['variants'].setValue(currentVariants);
    }
}
