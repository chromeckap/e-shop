package eshop.backend.repository;

import eshop.backend.model.Product;
import eshop.backend.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    ProductImage findByIdAndProduct(Long id, Product product);
    Set<Long> findAllIdsByProduct(Product product);
}
