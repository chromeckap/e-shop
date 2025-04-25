import {ComponentFixture, TestBed} from '@angular/core/testing';
import {CategoryNavComponent} from './category-nav.component';
import {NO_ERRORS_SCHEMA} from '@angular/core';
import {CategoryResponse} from '../../../../../services/models/category/category-response';
import {By} from '@angular/platform-browser';

describe('CategoryNavComponent', () => {
    let component: CategoryNavComponent;
    let fixture: ComponentFixture<CategoryNavComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            imports: [CategoryNavComponent],
            schemas: [NO_ERRORS_SCHEMA] // To ignore PrimeNG component errors
        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(CategoryNavComponent);
        component = fixture.componentInstance;
        // Initialize with empty values
        component.category = {};
        component.childCategories = [];
        fixture.detectChanges();
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    it('should display category name when provided', () => {
        // Arrange
        const testCategory: CategoryResponse = { name: 'Test Category' };
        component.category = testCategory;

        // Act
        fixture.detectChanges();

        // Assert
        const titleElement = fixture.debugElement.query(By.css('h3'));
        expect(titleElement).toBeTruthy();
        expect(titleElement.nativeElement.textContent).toBe('Test Category');
    });

    it('should not display description when not provided', () => {
        // Arrange
        const testCategory: CategoryResponse = { name: 'Test Category' };
        component.category = testCategory;

        // Act
        fixture.detectChanges();

        // Assert
        const descriptionElement = fixture.debugElement.query(By.css('span'));
        expect(descriptionElement).toBeFalsy();
    });

    it('should display description when provided', () => {
        // Arrange
        const testCategory: CategoryResponse = {
            name: 'Test Category',
            description: 'This is a test description'
        };
        component.category = testCategory;

        // Act
        fixture.detectChanges();

        // Assert
        const descriptionElement = fixture.debugElement.query(By.css('span'));
        expect(descriptionElement).toBeTruthy();
        expect(descriptionElement.nativeElement.textContent).toBe('This is a test description');
    });

    it('should not display menubar when no child categories are provided', () => {
        // Arrange
        component.childCategories = [];

        // Act
        fixture.detectChanges();

        // Assert
        const menubarElement = fixture.debugElement.query(By.css('p-menubar'));
        expect(menubarElement).toBeFalsy();
    });

    it('should apply gap-2 class when description is provided', () => {
        // Arrange
        component.category = {
            name: 'Test Category',
            description: 'This is a test description'
        };
        component.childCategories = [];

        // Act
        fixture.detectChanges();

        // Assert
        const containerElement = fixture.debugElement.query(By.css('.flex.flex-col.items-center'));
        expect(containerElement.classes['gap-2']).toBeTruthy();
    });

    it('should not apply gap-2 class when no description and no child categories', () => {
        // Arrange
        component.category = {name: 'Test Category'};
        component.childCategories = [];

        // Act
        fixture.detectChanges();

        // Assert
        const containerElement = fixture.debugElement.query(By.css('.flex.flex-col.items-center'));
        expect(containerElement.classes['gap-2']).toBeFalsy();
    });

    it('should apply mb-6 class to the toolbar', () => {
        // Act
        fixture.detectChanges();

        // Assert
        const toolbarElement = fixture.debugElement.query(By.css('p-toolbar'));
        expect(toolbarElement).toBeTruthy();
        expect(toolbarElement.attributes['styleClass']).toBe('mb-6');
    });
});
