package com.ecommerce.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Retrieves visible products belonging to a specific category.
     *
     * @param specification the specifications to be filtered.
     * @return a list of visible products for the given category.
     */
    List<Product> findAll(Specification<Product> specification);

    @Query("""
        SELECT product FROM Product product
        WHERE :categoryId MEMBER OF product.categoryIds
        AND product.isVisible = true
      """)
    List<Product> findAllVisibleByCategory(
            @Param("categoryId") Long categoryId
    );

    /**
     * Retrieves visible products whose names are similar to the provided search query.
     * The similarity is calculated using a database function (e.g., PostgreSQL's `similarity` function).
     *
     * @param query the search query to compare against product names.
     * @param similarity the minimum similarity threshold for matching products.
     * @param pageable pagination information.
     * @return a page of visible products sorted by similarity to the query.
     */
    @Query("""
        SELECT product FROM Product product
        WHERE FUNCTION('similarity', product.name, :query) > :similarity
        AND product.isVisible = true
        ORDER BY FUNCTION('similarity', product.name, :query) DESC
       """)
    Page<Product> findAllVisibleBySimilarity(
            @Param("query") String query,
            @Param("similarity") double similarity,
            Pageable pageable
    );

    /**
     * Counts the number of products based on a set of IDs.
     * This can be useful for determining how many products exist in a specific list of IDs.
     *
     * @param ids a set of product IDs to count.
     * @return the number of products that match the given set of IDs.
     */
    @Query("SELECT COUNT(p) FROM Product p WHERE p.id IN :ids")
    int countByIds(@Param("ids") Set<Long> ids);
}
