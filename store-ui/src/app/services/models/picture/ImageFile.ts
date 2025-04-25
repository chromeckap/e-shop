export interface ImageFile {
    name: string;
    size: number;
    objectURL?: string;
    isExisting?: boolean;
    file?: File;
    filePromise?: Promise<File>;
}
