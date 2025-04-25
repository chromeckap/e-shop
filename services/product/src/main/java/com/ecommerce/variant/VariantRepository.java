package com.ecommerce.variant;

import com.ecommerce.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface VariantRepository extends JpaRepository<Variant, Long> {

    /**
     * Retrieves all variants associated with a specific product.
     *
     * @param product the product to find variants for.
     * @return a list of variants associated with the given product.
     */
    List<Variant> findAllByProduct(Product product);

    /**
     * Counts the number of variants based on a set of variant IDs.
     * This is useful for determining how many variants exist in a given set.
     *
     * @param ids the set of variant IDs to count.
     * @return the number of variants that match the given set of IDs.
     */
    @Query("SELECT COUNT(v) FROM Variant v WHERE v.id IN :ids")
    int countByIds(@Param("ids") Set<Long> ids);
}
