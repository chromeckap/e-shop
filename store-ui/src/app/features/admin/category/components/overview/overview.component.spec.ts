import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OverviewComponent } from './overview.component';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { By } from '@angular/platform-browser';

describe('OverviewComponent', () => {
    let component: OverviewComponent;
    let fixture: ComponentFixture<OverviewComponent>;
    let formBuilder: FormBuilder;
    let form: FormGroup;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [OverviewComponent, ReactiveFormsModule]
        }).compileComponents();

        fixture = TestBed.createComponent(OverviewComponent);
        component = fixture.componentInstance;

        formBuilder = TestBed.inject(FormBuilder);
        form = formBuilder.group({
            id: [{ value: '123', disabled: true }],
            name: ['', Validators.required],
            description: ['']
        });

        component.form = form;
        fixture.detectChanges();
    });

    it('should bind form values to inputs', () => {
        form.patchValue({
            name: 'Nábytek',
            description: 'Kategorie s nábytkem'
        });

        fixture.detectChanges();

        const nameInput = fixture.debugElement.query(By.css('input[id="name"]')).nativeElement;
        const descTextarea = fixture.debugElement.query(By.css('textarea[id="description"]')).nativeElement;

        expect(nameInput.value).toBe('Nábytek');
        expect(descTextarea.value).toBe('Kategorie s nábytkem');
    });

    it('should show validation error when name is invalid and touched', () => {
        const nameControl = form.controls['name'];
        nameControl.markAsTouched();
        nameControl.setValue('');
        fixture.detectChanges();

        const errorEl = fixture.debugElement.query(By.css('span.text-red-500'));
        expect(errorEl).toBeTruthy();
        expect(errorEl.nativeElement.textContent).toContain('Název kategorie je povinný.');
    });
});
