import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ParentSelectComponent } from './parent-select.component';
import { ReactiveFormsModule, FormBuilder, FormGroup } from '@angular/forms';
import { TreeSelect } from 'primeng/treeselect';
import { By } from '@angular/platform-browser';
import {CategoryOverviewResponse} from "../../../../../services/models/category/category-overview-response";

describe('ParentSelectComponent', () => {
    let component: ParentSelectComponent;
    let fixture: ComponentFixture<ParentSelectComponent>;
    let formBuilder: FormBuilder;
    let form: FormGroup;

    const mockCategories: any = [
        {
            key: '1',
            label: 'Elektronika',
            children: [
                { key: '1-1', label: 'Mobily' },
                { key: '1-2', label: 'Notebooky' }
            ]
        },
        {
            key: '2',
            label: 'Nábytek'
        }
    ];


    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [ParentSelectComponent, ReactiveFormsModule]
        }).compileComponents();

        fixture = TestBed.createComponent(ParentSelectComponent);
        component = fixture.componentInstance;

        formBuilder = TestBed.inject(FormBuilder);
        form = formBuilder.group({
            parent: [null]
        });

        component.form = form;
        component.categories = mockCategories;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should pass categories to TreeSelect', () => {
        const treeSelect: TreeSelect = fixture.debugElement.query(By.directive(TreeSelect)).componentInstance;
        expect(treeSelect.options).toEqual(mockCategories);
    });

    it('should set TreeSelect input attributes correctly', () => {
        const treeSelect: TreeSelect = fixture.debugElement.query(By.directive(TreeSelect)).componentInstance;
        expect(treeSelect.filter).toBeTrue();
        expect(treeSelect.showClear).toBeTrue();
        expect(treeSelect.placeholder).toBe('Vyberte nadřazenou kategorii');
        expect(treeSelect.emptyMessage).toBe('Nejsou dostupné žádné kategorie.');
    });
});
