import {Component, Input, OnDestroy, ViewChild} from '@angular/core';
import {FileUpload} from "primeng/fileupload";
import {Button} from "primeng/button";
import {Tag} from "primeng/tag";
import {ImageFile} from "../../../../../services/models/picture/ImageFile";
import {ProductService} from "../../../../../services/services/product.service";
import {Observable, of, Subject, tap} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {FormGroup} from "@angular/forms";
import {ToastService} from "../../../../../shared/services/toast.service";

@Component({
    selector: 'app-picture-uploader',
    imports: [
        FileUpload,
        Button,
        Tag
    ],
    templateUrl: './picture-uploader.component.html',
    standalone: true,
    styleUrl: './picture-uploader.component.scss'
})
export class PictureUploaderComponent implements OnDestroy {
    @Input() form!: FormGroup;

    @ViewChild('fileUpload') fileUpload!: FileUpload;

    pictures: ImageFile[] = [];
    readonly acceptedFileTypes: string = 'image/*';
    readonly maxFileSize: number = 1048576; // 1MB

    private destroy$ = new Subject<void>();
    private imageLoadingMap = new Map<string, boolean>();

    constructor(
        private productService: ProductService,
        private toastService: ToastService
    ) {}

    ngOnDestroy(): void {
        this.destroy$.next();
        this.destroy$.complete();

        this.pictures.forEach(file => {
            if (file.objectURL) {
                URL.revokeObjectURL(file.objectURL);
            }
        });
    }

    loadExistingFiles(): void {
        const imagePaths = this.form.get('imagePaths')?.value;

        if (!imagePaths) return;

        this.pictures = imagePaths.map((name: any) => ({
            name,
            size: 0,
            isExisting: true
        }));

        const fetchPromises = this.pictures.map(file => this.lazyLoadFile(file));

        Promise.all(fetchPromises)
            .catch(async error => {
                console.error('Error loading existing files:', error);
                await this.toastService.showErrorToast('Chyba', 'Chyba při načítání existujících souborů.');

            })
            .finally(() => {
            });
    }

    async lazyLoadFile(file: ImageFile): Promise<void> {
        this.imageLoadingMap.set(file.name, true);

        if (!file.filePromise) {
            file.filePromise = this.fetchFile(file.name)
                .finally(() => {
                    this.imageLoadingMap.set(file.name, false);
                });
        }

        return file.filePromise.then(fetchedFile => {
            file.size = fetchedFile.size;
            return;
        });
    }

    async fetchFile(fileName: string): Promise<File> {
        try {
            const url = this.getImageUrl(fileName);
            const response = await fetch(url);

            if (!response.ok) {
                console.error(`Failed to fetch image: ${response.status} ${response.statusText}`);
            }

            const blob = await response.blob();
            return new File([blob], fileName, { type: blob.type });
        } catch (error) {
            console.error(`Error fetching file ${fileName}:`, error);
            throw error;
        }
    }

    async onSelectedFiles(event: any): Promise<void> {
        const newFiles = event.files;
        if (!newFiles?.length) return;

        for (let file of newFiles) {
            if (!this.isValidFileType(file)) {
                await this.toastService.showErrorToast('Chyba', `Soubor "${file.name}" není podporovaný formát obrázku.`);
                continue;
            }

            if (file.size > this.maxFileSize) {
                await this.toastService.showErrorToast('Chyba',
                    `Soubor "${file.name}" překračuje maximální povolenou velikost (${this.formatSize(this.maxFileSize)}).`
                );
                continue;
            }

            const uniqueName = this.getUniqueFileName(file.name);
            let newFile: File;

            if (uniqueName !== file.name) {
                newFile = new File([file], uniqueName, {type: file.type});
            } else {
                newFile = file;
            }

            const newImageFile: ImageFile = {
                name: uniqueName,
                size: file.size,
                objectURL: URL.createObjectURL(newFile),
                isExisting: false,
                file: newFile
            };

            this.pictures.push(newImageFile);
        }

    }

    isValidFileType(file: File): boolean {
        if (this.acceptedFileTypes === 'image/*') {
            return file.type.startsWith('image/');
        }

        const acceptedTypes = this.acceptedFileTypes.split(',').map(type => type.trim().toLowerCase());

        const fileExtension = '.' + file.name.split('.').pop()?.toLowerCase();
        return acceptedTypes.some(type => {
            if (type.startsWith('.')) {
                return fileExtension === type;
            } else {
                return file.type.match(new RegExp(type.replace('*', '.*')));
            }
        });
    }

    getImageSrc(file: ImageFile): string {
        if (file.objectURL) {
            return file.objectURL;
        }

        if (file.isExisting) {
            if (!file.filePromise) {
                this.lazyLoadFile(file)
                    .then();
            }

            return this.getImageUrl(file.name);
        }

        return '';
    }

    uploadFiles(productId: number): Observable<any> {
        if (this.pictures.length <= 0) return of(null);

        return this.productService.uploadProductImages(productId, this.pictures)
            .pipe(
                tap({
                    next: async () => {
                        await this.toastService.showSuccessToast('Úspěch', 'Obrázky byly úspěšně nahrány.');
                        this.pictures.forEach(file => file.isExisting = true);
                        this.fileUpload.clear();
                    },
                    error: async (error) => {
                        console.error('Chyba při nahrávání souborů:', error);
                        await this.toastService.showErrorToast('Chyba', error.error.detail);
                    }
                }),
                takeUntil(this.destroy$)
            );
    }

    onRemoveFile(event: Event, file: ImageFile, removeFileCallback: Function, index: number): void {
        event.stopPropagation();

        if (index < 0 || index >= this.pictures.length) {
            console.error('Invalid file index for removal:', index);
            return;
        }

        if (file.objectURL) {
            URL.revokeObjectURL(file.objectURL);
        }

        this.pictures.splice(index, 1);

        if (typeof removeFileCallback === 'function') {
            removeFileCallback(event, index);
        }

    }

    getImageUrl(fileName: string): string {
        const productId = this.form.get('id')?.value;
        return productId ? this.productService.getImage(productId, fileName) : '';
    }

    formatSize(bytes: number): string {
        if (!bytes || isNaN(bytes)) return 'N/A';

        const units = ['B', 'KB', 'MB', 'GB', 'TB'];
        let index = 0;

        while (bytes >= 1024 && index < units.length - 1) {
            bytes /= 1024;
            index++;
        }

        return `${bytes.toFixed(1)} ${units[index]}`;
    }

    getUniqueFileName(fileName: string): string {
        const lastDotIndex = fileName.lastIndexOf('.');
        const baseName = lastDotIndex !== -1 ? fileName.substring(0, lastDotIndex) : fileName;
        const extension = lastDotIndex !== -1 ? fileName.substring(lastDotIndex) : '';

        const regex = /^(.*?)(\(\d+\))$/;
        const match = baseName.match(regex);
        const nameWithoutNumber = match ? match[1].trim() : baseName;

        let counter = 0;
        let newName = fileName;

        while (this.pictures.some(file => file.name === newName)) {
            counter++;
            newName = `${nameWithoutNumber} (${counter})${extension}`;
        }

        return newName;
    }

    async onError(event: any): Promise<void> {
        const {error} = event;

        if (error) {
            if (error.type === 'filesize') {
                await this.toastService.showErrorToast('Chyba',
                    `Soubor "${error.file?.name}" překračuje maximální povolenou velikost (${this.formatSize(this.maxFileSize)}).`
                );
            } else if (error.type === 'filetype') {
                await this.toastService.showErrorToast('Chyba', `Soubor "${error.file?.name}" není podporovaný formát`);
            }
        }
    }

}
