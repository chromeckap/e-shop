import { Component } from '@angular/core';

@Component({
    selector: 'admin-footer',
    imports: [],
    templateUrl: './admin.footer.html',
    standalone: true
})
export class AdminFooter {
    currentYear = new Date().getFullYear();
}
