package com.ecommerce.productimage;


import com.ecommerce.product.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Table(name = "product_image")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String imagePath;

    private Integer uploadOrder;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

}
