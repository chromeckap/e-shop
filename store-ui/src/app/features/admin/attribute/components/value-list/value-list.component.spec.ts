import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ValueListComponent } from './value-list.component';
import { FormsModule } from '@angular/forms';

describe('ValueListComponent', () => {
    let component: ValueListComponent;
    let fixture: ComponentFixture<ValueListComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [ValueListComponent, FormsModule],
        });

        fixture = TestBed.createComponent(ValueListComponent);
        component = fixture.componentInstance;
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    it('should emit updated list when creating new value', () => {
        component.attributeValues = [{ id: 1, value: 'Test 1' }];
        component.newValue = 'New Value';
        spyOn(component.attributeValuesChange, 'emit');

        component.createValue();

        expect(component.attributeValuesChange.emit).toHaveBeenCalledWith([
            { id: 1, value: 'Test 1' },
            { id: 2, value: 'New Value' },
        ]);
        expect(component.newValue).toBe('');
    });

    it('should not create value if input is empty or whitespace', () => {
        spyOn(component.attributeValuesChange, 'emit');
        component.newValue = '   ';
        component.createValue();

        expect(component.attributeValuesChange.emit).not.toHaveBeenCalled();
    });

    it('should open dialog with correct data', () => {
        const value = { id: 42, value: 'Some Value' };
        component.openDialog(value);

        expect(component.editDialogVisible).toBeTrue();
        expect(component.editingAttributeValue).toEqual(value);
        expect(component.editingValue).toBe('Some Value');
    });

    it('should save updated value and close dialog', () => {
        component.attributeValues = [
            { id: 1, value: 'One' },
            { id: 2, value: 'Two' },
        ];
        component.editingAttributeValue = { id: 2, value: 'Two' };
        component.editingValue = 'Updated Two';
        spyOn(component.attributeValuesChange, 'emit');

        component.saveValue();

        expect(component.attributeValuesChange.emit).toHaveBeenCalledWith([
            { id: 1, value: 'One' },
            { id: 2, value: 'Updated Two' },
        ]);
        expect(component.editDialogVisible).toBeFalse();
    });

    it('should delete a value', () => {
        component.attributeValues = [
            { id: 1, value: 'One' },
            { id: 2, value: 'Two' },
        ];
        spyOn(component.attributeValuesChange, 'emit');

        component.deleteValue({ id: 1, value: 'One' });

        expect(component.attributeValuesChange.emit).toHaveBeenCalledWith([
            { id: 2, value: 'Two' },
        ]);
    });
});
