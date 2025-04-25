package com.ecommerce.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Finds all top-level categories (those without a parent).
     *
     * @return a list of categories without parents.
     */
    List<Category> findByParentIsNull();

    /**
     * Counts the categories by their IDs.
     * If the IDs set is empty, returns 0 to avoid unnecessary queries.
     *
     * @param ids a set of category IDs to count.
     * @return the count of categories that match the provided IDs.
     */
    @Query("SELECT COUNT(c) FROM Category c WHERE c.id IN :ids")
    int countCategoriesByIds(@Param("ids") Set<Long> ids);
}
