package eshop.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eshop.backend.request.AttributeValueRequest;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "attribute_value")
public class AttributeValue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String value;

    @ManyToOne
    @JoinColumn(name = "attribute_id")
    private Attribute attribute;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "variant_attribute_values",
            joinColumns = @JoinColumn(name = "attribute_value_id"),
            inverseJoinColumns = @JoinColumn(name = "variant_id")
    )
    private Set<Variant> variants;

    public AttributeValue(AttributeValueRequest request) {
        this.id = request.id();
        this.value = request.value();
    }
}
