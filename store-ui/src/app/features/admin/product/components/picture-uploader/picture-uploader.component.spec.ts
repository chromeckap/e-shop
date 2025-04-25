import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PictureUploaderComponent } from './picture-uploader.component';
import { ToastService } from '../../../../../shared/services/toast.service';
import { ProductService } from '../../../../../services/services/product.service';
import { ReactiveFormsModule, FormGroup, FormControl } from '@angular/forms';
import {provideHttpClient} from "@angular/common/http";

describe('PictureUploaderComponent', () => {
    let component: PictureUploaderComponent;
    let fixture: ComponentFixture<PictureUploaderComponent>;
    let toastServiceSpy: jasmine.SpyObj<ToastService>;
    let productServiceSpy: jasmine.SpyObj<ProductService>;

    beforeEach(async () => {
        toastServiceSpy = jasmine.createSpyObj('ToastService', ['showErrorToast', 'showSuccessToast']);
        productServiceSpy = jasmine.createSpyObj('ProductService', ['uploadProductImages', 'getImage']);

        await TestBed.configureTestingModule({
            imports: [PictureUploaderComponent, ReactiveFormsModule],
            providers: [
                { provide: ToastService, useValue: toastServiceSpy },
                { provide: ProductService, useValue: productServiceSpy },
                provideHttpClient(),
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(PictureUploaderComponent);
        component = fixture.componentInstance;

        // Mock form
        component.form = new FormGroup({
            id: new FormControl(123),
            imagePaths: new FormControl([])
        });

        fixture.detectChanges();
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    describe('isValidFileType', () => {
        it('should return true for image type', () => {
            const file = new File(['dummy'], 'image.png', { type: 'image/png' });
            expect(component.isValidFileType(file)).toBeTrue();
        });

        it('should return false for non-image type', () => {
            const file = new File(['dummy'], 'text.txt', { type: 'text/plain' });
            expect(component.isValidFileType(file)).toBeFalse();
        });
    });

    describe('onSelectedFiles', () => {
        it('should add valid file to pictures array', async () => {
            const file = new File(['dummy'], 'image.png', { type: 'image/png' });
            const event = { files: [file] };

            await component.onSelectedFiles(event);

            expect(component.pictures.length).toBe(1);
            expect(component.pictures[0].name).toBe('image.png');
        });

        it('should skip invalid file types', async () => {
            const file = new File(['dummy'], 'file.txt', { type: 'text/plain' });
            const event = { files: [file] };

            await component.onSelectedFiles(event);

            expect(component.pictures.length).toBe(0);
            expect(toastServiceSpy.showErrorToast).toHaveBeenCalled();
        });
    });

    describe('onRemoveFile', () => {
        it('should remove file from array and call removeFileCallback', () => {
            const file = {
                name: 'test.png',
                size: 100,
                isExisting: false,
                objectURL: 'blob://some-url'
            };

            component.pictures = [file];
            const event = new Event('click');
            const callback = jasmine.createSpy('removeFileCallback');

            spyOn(URL, 'revokeObjectURL');

            component.onRemoveFile(event, file, callback, 0);

            expect(component.pictures.length).toBe(0);
            expect(callback).toHaveBeenCalledWith(event, 0);
            expect(URL.revokeObjectURL).toHaveBeenCalledWith('blob://some-url');
        });
    });

    describe('getUniqueFileName', () => {
        it('should return unique name if duplicate exists', () => {
            component.pictures = [{ name: 'image.png', size: 0, isExisting: true }];
            const uniqueName = component.getUniqueFileName('image.png');
            expect(uniqueName).toBe('image (1).png');
        });

        it('should return original name if no duplicates', () => {
            component.pictures = [];
            const uniqueName = component.getUniqueFileName('image.png');
            expect(uniqueName).toBe('image.png');
        });
    });
});
