import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AttributeListComponent } from './attribute-list.component';
import { AttributeService } from '../../../../../services/services/attribute.service';
import { ToastService } from '../../../../../shared/services/toast.service';
import { ConfirmationService } from 'primeng/api';
import { of, throwError } from 'rxjs';
import { AttributeResponse } from '../../../../../services/models/attribute/attribute-response';

describe('AttributeListComponent', () => {
    let component: AttributeListComponent;
    let fixture: ComponentFixture<AttributeListComponent>;
    let attributeServiceMock: any;
    let toastServiceMock: any;
    let confirmationServiceMock: any;
    let routerMock: any;

    const mockAttributes: AttributeResponse[] = [
        { id: 1, name: 'Barva', values: [] },
        { id: 2, name: 'Velikost', values: [] }
    ];

    beforeEach(async () => {
        attributeServiceMock = {
            getAllAttributes: jasmine.createSpy().and.returnValue(of(mockAttributes)),
            deleteAttributeById: jasmine.createSpy().and.returnValue(of(null))
        };

        toastServiceMock = {
            showSuccessToast: jasmine.createSpy().and.returnValue(Promise.resolve()),
            showErrorToast: jasmine.createSpy().and.returnValue(Promise.resolve())
        };

        confirmationServiceMock = {
            confirm: jasmine.createSpy()
        };

        routerMock = {
            navigate: jasmine.createSpy().and.returnValue(Promise.resolve())
        };

        await TestBed.configureTestingModule({
            imports: [AttributeListComponent],
            providers: [
                { provide: AttributeService, useValue: attributeServiceMock },
                { provide: ToastService, useValue: toastServiceMock },
                { provide: ConfirmationService, useValue: confirmationServiceMock },
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(AttributeListComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create component', () => {
        expect(component).toBeTruthy();
    });

    it('should load attributes on init', () => {
        expect(attributeServiceMock.getAllAttributes).toHaveBeenCalled();
        expect(component.attributes).toEqual(mockAttributes);
    });

    it('should navigate to create page', () => {
        component.createAttribute();
        expect(routerMock.navigate);
    });

    it('should navigate to edit page with attribute id', () => {
        const attribute = { id: 5, name: 'Materiál', values: [] };
        component.editAttribute(attribute);
        expect(routerMock.navigate);
    });

    it('should call confirmation dialog when deleting single attribute', () => {
        const attribute = { id: 3, name: 'Šířka', values: [] };
        component.deleteAttribute(attribute);
        expect(confirmationServiceMock.confirm);
    });

    it('should call confirmation dialog when deleting selected attributes', () => {
        component.selectedAttributes = [mockAttributes[0]];
        component.deleteSelectedAttributes();
        expect(confirmationServiceMock.confirm);
    });

    it('should apply global filter to table', () => {
        const fakeEvent = { target: { value: 'test' } } as unknown as Event;
        const fakeTable = { filterGlobal: jasmine.createSpy() };
        component.onGlobalFilter(fakeTable as any, fakeEvent);
        expect(fakeTable.filterGlobal).toHaveBeenCalledWith('test', 'contains');
    });

    it('should handle deleteAttribute service error', () => {
        const errorResponse = { error: { detail: 'Chyba' } };
        attributeServiceMock.deleteAttributeById.and.returnValue(throwError(() => errorResponse));
        confirmationServiceMock.confirm.and.callFake(({ accept }: any) => accept());

        const attribute = { id: 1, name: 'Barva', values: [] };
        component.deleteAttribute(attribute);

        expect(attributeServiceMock.deleteAttributeById);
    });
});
