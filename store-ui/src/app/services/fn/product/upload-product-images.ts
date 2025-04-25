import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { from, Observable } from 'rxjs';
import { filter, map, switchMap } from 'rxjs/operators';
import { StrictHttpResponse } from '../../strict-http-response';
import { RequestBuilder } from '../../request-builder';
import { ImageFile } from "../../models/picture/ImageFile";

export function uploadProductImages(http: HttpClient, rootUrl: string, id: number, imageFiles: ImageFile[], context?: HttpContext): Observable<StrictHttpResponse<void>> {

    return from(prepareFormData(imageFiles)).pipe(
        switchMap(formData => {
            const rb = new RequestBuilder(rootUrl, uploadProductImages.PATH.replace('{id}', id.toString()), 'post');

            rb.body(formData);

            return http.request(
                rb.build({ responseType: 'json', accept: 'application/json', context })
            ).pipe(
                filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
                map((r: HttpResponse<any>) => {
                    return r as StrictHttpResponse<void>;
                })
            );
        })
    );
}

async function prepareFormData(imageFiles: ImageFile[]): Promise<FormData> {
    const formData = new FormData();

    for (const file of imageFiles) {
        try {
            if (file.isExisting && file.filePromise) {
                const fileObj = await file.filePromise;
                formData.append('file', fileObj);
            } else if (!file.isExisting && file.file) {
                formData.append('file', file.file);
            }
        } catch (error) {
            console.error(`Error processing file ${file.name}:`, error);
        }
    }

    return formData;
}

uploadProductImages.PATH = '/api/v1/products/{id}/images';
