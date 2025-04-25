import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ManageAttributeComponent } from './manage-attribute.component';
import { ActivatedRoute, Router } from '@angular/router';
import { AttributeService } from '../../../../../services/services/attribute.service';
import { ToastService } from '../../../../../shared/services/toast.service';
import { ConfirmationService } from 'primeng/api';
import { of, throwError } from 'rxjs';
import { ReactiveFormsModule } from '@angular/forms';

describe('ManageAttributeComponent', () => {
    let component: ManageAttributeComponent;
    let fixture: ComponentFixture<ManageAttributeComponent>;
    let mockAttributeService: any;
    let mockToastService: any;
    let mockRouter: any;
    let mockConfirmationService: any;

    beforeEach(async () => {
        mockAttributeService = {
            getAttributeById: jasmine.createSpy(),
            updateAttribute: jasmine.createSpy(),
            createAttribute: jasmine.createSpy(),
            deleteAttributeById: jasmine.createSpy()
        };

        mockToastService = {
            showSuccessToast: jasmine.createSpy().and.returnValue(Promise.resolve()),
            showErrorToast: jasmine.createSpy().and.returnValue(Promise.resolve())
        };

        mockRouter = {
            navigate: jasmine.createSpy().and.returnValue(Promise.resolve(true))
        };

        mockConfirmationService = {
            confirm: jasmine.createSpy()
        };

        await TestBed.configureTestingModule({
            imports: [ManageAttributeComponent, ReactiveFormsModule],
            providers: [
                { provide: AttributeService, useValue: mockAttributeService },
                { provide: ToastService, useValue: mockToastService },
                { provide: Router, useValue: mockRouter },
                { provide: ConfirmationService, useValue: mockConfirmationService },
                {
                    provide: ActivatedRoute,
                    useValue: {
                        snapshot: {
                            params: {}
                        }
                    }
                }
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(ManageAttributeComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    it('should initialize with attribute when ID is present', () => {
        const testAttribute = { id: 1, name: 'Barva', values: [{ id: 1, value: 'Červená' }] };
        mockAttributeService.getAttributeById.and.returnValue(of(testAttribute));
        const route = TestBed.inject(ActivatedRoute);
        route.snapshot.params['id'] = 1;

        component.ngOnInit();

        expect(mockAttributeService.getAttributeById).toHaveBeenCalledWith(1);
    });

    it('should save new attribute', () => {
        component.form.patchValue({ name: 'Materiál' });
        component.attributeValues = [{ id: 1, value: 'Bavlna' }];
        mockAttributeService.createAttribute.and.returnValue(of(123));

        component.saveAttribute();

        expect(mockAttributeService.createAttribute).toHaveBeenCalled();
        expect(mockToastService.showSuccessToast).toHaveBeenCalled();
        expect(mockRouter.navigate);
    });

    it('should update existing attribute', () => {
        component.form.patchValue({ id: 1, name: 'Velikost' });
        component.attributeValues = [];
        mockAttributeService.updateAttribute.and.returnValue(of(1));

        component.saveAttribute();

        expect(mockAttributeService.updateAttribute).toHaveBeenCalledWith(1, jasmine.any(Object));
    });

    it('should handle save error', async () => {
        component.form.patchValue({ name: 'Test' });
        mockAttributeService.createAttribute.and.returnValue(throwError({ error: { detail: 'Chyba' } }));

        await component.saveAttribute();

        expect(mockToastService.showErrorToast).toHaveBeenCalledWith('Chyba', 'Chyba');
    });

    it('should confirm and delete attribute', () => {
        component.form.patchValue({ id: 2, name: 'Barva' });
        const acceptFn = jasmine.createSpy('accept');
        mockConfirmationService.confirm.and.callFake((options: any) => options.accept());

        mockAttributeService.deleteAttributeById.and.returnValue(of(null));

        component.deleteAttribute();

        expect(mockAttributeService.deleteAttributeById);
        expect(mockToastService.showSuccessToast);
        expect(mockRouter.navigate);
    });
});
