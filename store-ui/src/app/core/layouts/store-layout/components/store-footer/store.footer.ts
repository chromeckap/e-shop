import { Component } from '@angular/core';

@Component({
    selector: 'store-footer',
    imports: [],
    templateUrl: './store.footer.html',
    standalone: true
})
export class StoreFooter {
    currentYear = new Date().getFullYear();
}
