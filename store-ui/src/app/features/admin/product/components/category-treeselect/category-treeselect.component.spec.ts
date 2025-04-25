import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { CategoryTreeSelectComponent } from './category-treeselect.component';
import { CategoryService } from '../../../../../services/services/category.service';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Divider } from 'primeng/divider';
import { TreeTableModule } from 'primeng/treetable';
import { of, Subject } from 'rxjs';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';
import { Component, ViewChild } from '@angular/core';
import { CategoryOverviewResponse } from '../../../../../services/models/category/category-overview-response';
import { TreeNode } from 'primeng/api';
import { RefreshService } from '../../services/refresh.service';

// Create a test host component to provide the form
@Component({
    selector: 'app-test-host',
    standalone: true,
    imports: [CategoryTreeSelectComponent, ReactiveFormsModule],
    template: `
    <app-category-treeselect [form]="form"></app-category-treeselect>
  `
})
class TestHostComponent {
    @ViewChild(CategoryTreeSelectComponent) categoryTreeSelectComponent!: CategoryTreeSelectComponent;
    form!: FormGroup;

    constructor(private fb: FormBuilder) {
        this.createForm();
    }

    createForm() {
        this.form = this.fb.group({
            categoryIds: [[]]
        });
    }
}

describe('CategoryTreeSelectComponent', () => {
    let hostComponent: TestHostComponent;
    let hostFixture: ComponentFixture<TestHostComponent>;
    let component: CategoryTreeSelectComponent;
    let categoryServiceSpy: jasmine.SpyObj<CategoryService>;
    let refreshServiceSpy: jasmine.SpyObj<RefreshService>;
    let refreshSubject: Subject<void>;

    // Mock data
    const mockCategories: any = [
        {
            key: '1',
            name: 'Electronics',
            children: [
                {
                    id: '11',
                    name: 'Phones',
                    children: [
                        {
                            key: '111',
                            name: 'Smartphones',
                            children: []
                        },
                        {
                            key: '112',
                            name: 'Feature Phones',
                            children: []
                        }
                    ]
                },
                {
                    key: '12',
                    name: 'Computers',
                    children: []
                }
            ]
        },
        {
            key: '2',
            name: 'Clothing',
            children: [
                {
                    key: '21',
                    name: 'Men',
                    children: []
                },
                {
                    key: '22',
                    name: 'Women',
                    children: []
                }
            ]
        }
    ];

    beforeEach(async () => {
        // Create spy for CategoryService
        const categorySpy = jasmine.createSpyObj('CategoryService', ['getAllCategories']);

        // Create refresh service with subject
        refreshSubject = new Subject<void>();
        const refreshSpy = jasmine.createSpyObj('RefreshService', [], {
            refresh$: refreshSubject.asObservable()
        });

        await TestBed.configureTestingModule({
            imports: [
                ReactiveFormsModule,
                NoopAnimationsModule,
                Divider,
                TreeTableModule,
                TestHostComponent,
                CategoryTreeSelectComponent
            ],
            providers: [
                { provide: CategoryService, useValue: categorySpy },
                { provide: RefreshService, useValue: refreshSpy }
            ]
        }).compileComponents();

        categoryServiceSpy = TestBed.inject(CategoryService) as jasmine.SpyObj<CategoryService>;
        refreshServiceSpy = TestBed.inject(RefreshService) as jasmine.SpyObj<RefreshService>;

        categoryServiceSpy.getAllCategories.and.returnValue(of(mockCategories));

        hostFixture = TestBed.createComponent(TestHostComponent);
        hostComponent = hostFixture.componentInstance;
        hostFixture.detectChanges();
        component = hostComponent.categoryTreeSelectComponent;
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should load all categories on init', fakeAsync(() => {
        component.ngOnInit();
        tick();

        expect(categoryServiceSpy.getAllCategories).toHaveBeenCalled();
        expect(component.categories).toEqual(mockCategories);
        expect(component.categoriesTree.length).toBe(2); // Two top-level categories
    }));

    it('should receive form from parent component', () => {
        expect(component.form).toBeTruthy();
        expect(component.form.get('categoryIds')).toBeTruthy();
    });

    it('should render TreeTable component', () => {
        const treeTable = hostFixture.debugElement.query(By.css('p-treeTable'));
        expect(treeTable).toBeTruthy();
    });

    it('should transform categories into tree nodes correctly', fakeAsync(() => {
        component.ngOnInit();
        tick();

        // Test the structure of the transformed categories
        expect(component.categoriesTree.length).toBe(2);

        // Check first top-level node
        const firstNode = component.categoriesTree[0];
        expect(firstNode.children?.length).toBe(2);

        // Check a nested node (Phones)
        const phonesNode = firstNode.children?.[0];
        expect(phonesNode?.data.id).toBe('11');
        expect(phonesNode?.data.name).toBe('Phones');
        expect(phonesNode?.children?.length).toBe(2);

        // Check leaf node (Smartphones)
        const smartphonesNode = phonesNode?.children?.[0];
        expect(smartphonesNode?.children).toBeUndefined();
    }));

    it('should have correct keys for the tree nodes', fakeAsync(() => {
        component.ngOnInit();
        tick();

        // Check keys format for top-level nodes
        expect(component.categoriesTree[0].key).toBe('0');
        expect(component.categoriesTree[1].key).toBe('1');

        // Check keys format for nested nodes (should be parent key + index)
        const firstLevelChildren = component.categoriesTree[0].children;
        expect(firstLevelChildren?.[0].key).toBe('0-0');
        expect(firstLevelChildren?.[1].key).toBe('0-1');

        // Check deeper nested node
        const secondLevelChildren = firstLevelChildren?.[0].children;
        expect(secondLevelChildren?.[0].key).toBe('0-0-0');
    }));

    it('should refresh tree when refresh$ emits', fakeAsync(() => {
        component.ngOnInit();
        tick();

        // Spy on transformCategories method
        spyOn(component, 'transformCategories').and.callThrough();

        // Emit refresh event
        refreshSubject.next();
        tick();

        expect(component.transformCategories).toHaveBeenCalledWith(component.categories);
    }));

    it('should clean up subscriptions on destroy', fakeAsync(() => {
        component.ngOnInit();
        tick();

        spyOn(component['destroy$'], 'next');
        spyOn(component['destroy$'], 'complete');

        component.ngOnDestroy();

        expect(component['destroy$'].next).toHaveBeenCalled();
        expect(component['destroy$'].complete).toHaveBeenCalled();
    }));

    it('should get selected category IDs correctly', fakeAsync(() => {
        component.ngOnInit();
        tick();

        // Set some selections
        component.selectionKeys = {
            '0': { checked: true },        // Electronics
            '0-0': { checked: true },      // Phones
            '0-0-0': { checked: true },    // Smartphones
            '0-0-1': { checked: true },    // Feature Phones
            '0-1': { checked: true },      // Computers
            '1-0': { checked: true }       // Men
        };

        // Get selected IDs
        const selectedIds = component.getSelectedCategoryIds();

        expect(selectedIds.length).toBe(6);
    }));

    it('should handle partial checked nodes in getSelectedCategoryIds', fakeAsync(() => {
        component.ngOnInit();
        tick();

        // Set some selections with partialChecked
        component.selectionKeys = {
            '0': { partialChecked: true },  // Electronics (partly checked)
            '0-0': { checked: true },       // Phones (fully checked)
            '1': { partialChecked: true },  // Clothing (partly checked)
            '1-1': { checked: true }        // Women (fully checked)
        };

        // Get selected IDs
        const selectedIds = component.getSelectedCategoryIds();

        expect(selectedIds.length).toBe(4);
    }));

    it('should handle empty selection in getSelectedCategoryIds', fakeAsync(() => {
        component.ngOnInit();
        tick();

        // Empty selection
        component.selectionKeys = {};

        // Get selected IDs
        const selectedIds = component.getSelectedCategoryIds();

        // Should return empty array
        expect(selectedIds.length).toBe(0);
    }));
});
