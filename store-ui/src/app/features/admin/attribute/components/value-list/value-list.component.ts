import {Component, EventEmitter, Input, Output} from '@angular/core';
import {AttributeValueResponse} from "../../../../../services/models/attribute/attribute-value-response";
import {Divider} from "primeng/divider";
import {TableModule} from "primeng/table";
import {Button} from "primeng/button";
import {InputText} from "primeng/inputtext";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Dialog} from "primeng/dialog";

@Component({
    selector: 'app-value-list',
    imports: [
        Divider,
        TableModule,
        Button,
        InputText,
        ReactiveFormsModule,
        FormsModule,
        Dialog
    ],
    templateUrl: './value-list.component.html',
    standalone: true,
    styleUrl: './value-list.component.scss'
})
export class ValueListComponent {
    @Input() attributeValues: Array<AttributeValueResponse> = [];
    @Output() attributeValuesChange = new EventEmitter<Array<AttributeValueResponse>>();

    newValue = '';

    editDialogVisible = false;
    editingValue = '';
    editingAttributeValue: AttributeValueResponse | null = null;

    openDialog(attributeValue: AttributeValueResponse) {
        this.editDialogVisible = true;
        this.editingAttributeValue = attributeValue;
        this.editingValue = attributeValue.value!;
    }

    createValue() {
        if (this.newValue.trim()) {
            const highestId = Math.max(0, ...this.attributeValues.map(value => value.id!));
            const updatedValues = [...this.attributeValues, { id: highestId + 1, value: this.newValue }];
            this.attributeValuesChange.emit(updatedValues);
            this.newValue = '';
        }
    }

    saveValue() {
        if (this.editingAttributeValue) {
            const updatedValues = this.attributeValues.map(value => {
                if (value.id === this.editingAttributeValue?.id) {
                    return { ...value, value: this.editingValue };
                }
                return value;
            });
            this.attributeValuesChange.emit(updatedValues);
        }
        this.editDialogVisible = false;
    }

    deleteValue(value: AttributeValueResponse) {
        const updatedValues = this.attributeValues.filter(v => v.id !== value.id);
        this.attributeValuesChange.emit(updatedValues);
    }

}
