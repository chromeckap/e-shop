package eshop.backend.repository;

import eshop.backend.model.ProductVariantSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductVariantSnapshotRepository extends JpaRepository<ProductVariantSnapshot, Long> {
    Optional<ProductVariantSnapshot> findByProductIdAndVariantId(Long productId, Long variantId);
}
