package eshop.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import eshop.backend.request.AttributeRequest;
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
@Table(name = "attribute")
public class Attribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @OneToMany(mappedBy = "attribute", cascade = CascadeType.REMOVE)
    private Set<AttributeValue> values;

    @JsonIgnore
    @ManyToMany
    @JoinTable(
            name = "product_attributes",
            joinColumns = @JoinColumn(name = "attribute_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> products;

    public Attribute(AttributeRequest request) {
        this.id = request.id();
        this.name = request.name();
    }
}
