package com.ecommerce.attribute;

import com.ecommerce.attributevalue.AttributeValue;
import com.ecommerce.product.Product;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AttributeTest {

    @Test
    void constructor_EmptyConstructor_CreatesInstance() {
        // Act
        Attribute attribute = new Attribute();

        // Assert
        assertNotNull(attribute);
        assertNull(attribute.getId());
        assertNull(attribute.getName());
        assertNotNull(attribute.getValues());
        assertTrue(attribute.getValues().isEmpty());
        assertNotNull(attribute.getProducts());
        assertTrue(attribute.getProducts().isEmpty());
    }

    @Test
    void constructor_AllArgsConstructor_CreatesInstance() {
        // Arrange
        Long id = 1L;
        String name = "Color";
        List<AttributeValue> values = new ArrayList<>();
        List<Product> products = new ArrayList<>();

        // Act
        Attribute attribute = new Attribute(id, name, values, products);

        // Assert
        assertNotNull(attribute);
        assertEquals(id, attribute.getId());
        assertEquals(name, attribute.getName());
        assertEquals(values, attribute.getValues());
        assertEquals(products, attribute.getProducts());
    }

    @Test
    void builder_WithAllProperties_CreatesInstance() {
        // Arrange
        Long id = 1L;
        String name = "Color";
        List<AttributeValue> values = new ArrayList<>();
        List<Product> products = new ArrayList<>();

        // Act
        Attribute attribute = Attribute.builder()
                .id(id)
                .name(name)
                .values(values)
                .products(products)
                .build();

        // Assert
        assertNotNull(attribute);
        assertEquals(id, attribute.getId());
        assertEquals(name, attribute.getName());
        assertEquals(values, attribute.getValues());
        assertEquals(products, attribute.getProducts());
    }

    @Test
    void builder_WithNoProperties_CreatesInstanceWithDefaultCollections() {
        // Act
        Attribute attribute = Attribute.builder().build();

        // Assert
        assertNotNull(attribute);
        assertNull(attribute.getId());
        assertNull(attribute.getName());
        assertNotNull(attribute.getValues());
        assertTrue(attribute.getValues().isEmpty());
        assertNotNull(attribute.getProducts());
        assertTrue(attribute.getProducts().isEmpty());
    }

    @Test
    void builder_WithOnlyIdAndName_CreatesInstanceWithDefaultCollections() {
        // Arrange
        Long id = 1L;
        String name = "Color";

        // Act
        Attribute attribute = Attribute.builder()
                .id(id)
                .name(name)
                .build();

        // Assert
        assertNotNull(attribute);
        assertEquals(id, attribute.getId());
        assertEquals(name, attribute.getName());
        assertNotNull(attribute.getValues());
        assertTrue(attribute.getValues().isEmpty());
        assertNotNull(attribute.getProducts());
        assertTrue(attribute.getProducts().isEmpty());
    }

    @Test
    void settersAndGetters_WorkCorrectly() {
        // Arrange
        Attribute attribute = new Attribute();
        Long id = 1L;
        String name = "Color";
        List<AttributeValue> values = new ArrayList<>();
        List<Product> products = new ArrayList<>();

        // Act
        attribute.setId(id);
        attribute.setName(name);
        attribute.setValues(values);
        attribute.setProducts(products);

        // Assert
        assertEquals(id, attribute.getId());
        assertEquals(name, attribute.getName());
        assertEquals(values, attribute.getValues());
        assertEquals(products, attribute.getProducts());
    }

    @Test
    void equals_WithSameInstance_ReturnsTrue() {
        // Arrange
        Attribute attribute = Attribute.builder()
                .id(1L)
                .name("Color")
                .build();

        // Act & Assert
        assertEquals(attribute, attribute);
    }

    @Test
    void equals_WithEqualId_ReturnsTrue() {
        // Arrange
        Attribute attribute1 = Attribute.builder()
                .id(1L)
                .name("Color")
                .build();

        Attribute attribute2 = Attribute.builder()
                .id(1L)
                .name("Size") // Different name, but same ID
                .build();

        // Act & Assert
        assertEquals(attribute1, attribute2);
        assertEquals(attribute1.hashCode(), attribute2.hashCode());
    }

    @Test
    void equals_WithDifferentId_ReturnsFalse() {
        // Arrange
        Attribute attribute1 = Attribute.builder()
                .id(1L)
                .name("Color")
                .build();

        Attribute attribute2 = Attribute.builder()
                .id(2L)
                .name("Color") // Same name, but different ID
                .build();

        // Act & Assert
        assertNotEquals(attribute1, attribute2);
    }

    @Test
    void equals_WithOneNullId_ReturnsFalse() {
        // Arrange
        Attribute attribute1 = Attribute.builder()
                .id(1L)
                .name("Color")
                .build();

        Attribute attribute2 = Attribute.builder()
                .id(null)
                .name("Color")
                .build();

        // Act & Assert
        assertNotEquals(attribute1, attribute2);
        assertNotEquals(attribute2, attribute1);
    }

    @Test
    void equals_WithDifferentType_ReturnsFalse() {
        // Arrange
        Attribute attribute = Attribute.builder()
                .id(1L)
                .name("Color")
                .build();

        // Act & Assert
        assertNotEquals(attribute, "Not an attribute");
    }

    @Test
    void equals_WithNull_ReturnsFalse() {
        // Arrange
        Attribute attribute = Attribute.builder()
                .id(1L)
                .name("Color")
                .build();

        // Act & Assert
        assertNotEquals(attribute, null);
    }

    @Test
    void toString_ContainsAllFields() {
        // Arrange
        Attribute attribute = Attribute.builder()
                .id(1L)
                .name("Color")
                .build();

        // Act
        String toString = attribute.toString();

        // Assert
        assertTrue(toString.contains("id=1"));
        assertTrue(toString.contains("name=Color"));
        assertTrue(toString.contains("values=[]"));
        assertTrue(toString.contains("products=[]"));
    }

    @Test
    void addAttributeValue_AddsValueCorrectly() {
        // Arrange
        Attribute attribute = Attribute.builder()
                .id(1L)
                .name("Color")
                .build();

        AttributeValue attributeValue = new AttributeValue();
        attributeValue.setId(1L);
        attributeValue.setValue("Red");

        // Act
        // Manually adding the value to test the relationship
        attribute.getValues().add(attributeValue);
        attributeValue.setAttribute(attribute);

        // Assert
        assertEquals(1, attribute.getValues().size());
        assertEquals(attributeValue, attribute.getValues().getFirst());
        assertEquals(attribute, attributeValue.getAttribute());
    }

    @Test
    void addProduct_AddsProductCorrectly() {
        // Arrange
        Attribute attribute = Attribute.builder()
                .id(1L)
                .name("Color")
                .build();

        Product product = new Product();
        // Assuming Product has a similar structure with attributes list

        // Act
        // Manually adding the product to test the relationship
        attribute.getProducts().add(product);

        // Assert
        assertEquals(1, attribute.getProducts().size());
        assertEquals(product, attribute.getProducts().getFirst());
    }
}