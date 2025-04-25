package com.ecommerce.attribute;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface AttributeRepository extends JpaRepository<Attribute, Long> {

    /**
     * Counts the number of products whose IDs are included in the provided set.
     * This query is useful when checking the number of products in a specific category or based on certain attributes.
     *
     * @param ids a set of product IDs to count.
     * @return the number of products matching the given set of IDs.
     */
    @Query("SELECT COUNT(a) FROM Attribute a WHERE a.id IN :ids")
    int countByIds(@Param("ids") Set<Long> ids);

}
