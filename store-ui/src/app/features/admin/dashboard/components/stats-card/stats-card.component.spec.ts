import { ComponentFixture, TestBed } from '@angular/core/testing';
import { StatsCardComponent } from './stats-card.component';
import { By } from '@angular/platform-browser';

describe('StatsCardComponent', () => {
    let component: StatsCardComponent;
    let fixture: ComponentFixture<StatsCardComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [StatsCardComponent],
        }).compileComponents();

        fixture = TestBed.createComponent(StatsCardComponent);
        component = fixture.componentInstance;
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    describe('Input properties', () => {
        it('should set default values for inputs', () => {
            expect(component.title).toBe('');
            expect(component.value).toBe('');
            expect(component.icon).toBe('');
            expect(component.iconColor).toBe('');
            expect(component.highlightText).toBe('');
        });

        it('should display the title', () => {
            component.title = 'Test Title';
            fixture.detectChanges();

            const titleElement = fixture.debugElement.query(By.css('.text-muted-color.font-medium.mb-4'));
            expect(titleElement.nativeElement.textContent).toBe('Test Title');
        });

        it('should display the value', () => {
            component.value = '42';
            fixture.detectChanges();

            const valueElement = fixture.debugElement.query(By.css('.text-surface-900.dark\\:text-surface-0.font-medium.text-xl'));
            expect(valueElement.nativeElement.textContent).toBe('42');
        });

        it('should display the highlight text', () => {
            component.highlightText = '+12%';
            fixture.detectChanges();

            const highlightElement = fixture.debugElement.query(By.css('.text-primary.font-medium'));
            expect(highlightElement.nativeElement.textContent).toBe('+12% ');
        });
    });

    describe('Computed CSS classes', () => {
        it('should compute correct iconClass', () => {
            component.icon = 'pi-chart-line';
            component.iconColor = 'blue';

            expect(component.iconClass).toBe('pi-chart-line text-blue-500');
        });

        it('should compute correct iconBgClass', () => {
            component.iconColor = 'green';

            expect(component.iconBgClass).toBe('bg-green-100');
        });

        it('should apply iconClass to the icon element', () => {
            component.icon = 'pi-users';
            component.iconColor = 'purple';
            fixture.detectChanges();

            const iconElement = fixture.debugElement.query(By.css('.pi'));
            expect(iconElement.nativeElement.className).toContain('pi-users');
            expect(iconElement.nativeElement.className).toContain('text-purple-500');
        });

        it('should apply iconBgClass to the icon container', () => {
            component.iconColor = 'yellow';
            fixture.detectChanges();

            const iconContainer = fixture.debugElement.query(By.css('.flex.items-center.justify-center.rounded-border'));
            expect(iconContainer.nativeElement.className).toContain('bg-yellow-100');
        });

        it('should handle empty iconColor', () => {
            component.iconColor = '';
            fixture.detectChanges();

            expect(component.iconClass).toBe(' text--500');
            expect(component.iconBgClass).toBe('bg--100');
        });
    });

    describe('Layout structure', () => {
        it('should have the correct grid structure', () => {
            fixture.detectChanges();

            const container = fixture.debugElement.query(By.css('.col-span-12.lg\\:col-span-6.xl\\:col-span-4'));
            expect(container).toBeTruthy();
        });

        it('should have a card with content', () => {
            fixture.detectChanges();

            const card = fixture.debugElement.query(By.css('.card'));
            expect(card).toBeTruthy();
        });
    });
});
