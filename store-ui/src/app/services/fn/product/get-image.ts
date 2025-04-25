export function getImage(imageUrl: string, id: number, fileName: string): string {
    return imageUrl +
        getImage.PATH
            .replace('{id}', id.toString())
            .replace('{fileName}', fileName);

}

getImage.PATH = '/api/v1/products/{id}/images/{fileName}';
