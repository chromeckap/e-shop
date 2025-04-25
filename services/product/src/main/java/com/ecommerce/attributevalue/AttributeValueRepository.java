package com.ecommerce.attributevalue;

import com.ecommerce.attribute.Attribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AttributeValueRepository extends JpaRepository<AttributeValue, Long> {

    /**
     * Retrieves all AttributeValue entries related to a specific Attribute.
     *
     * @param attribute the attribute for which to retrieve associated attribute values.
     * @return a list of attribute values for the specified attribute.
     */
    List<AttributeValue> findByAttribute(Attribute attribute);

    /**
     * Checks if an AttributeValue exists by its ID.
     *
     * @param id the ID of the AttributeValue.
     * @param attribute the attribute for AttributeValue.
     * @return true if the AttributeValue exists, otherwise false.
     */
    boolean existsAttributeValueByIdAndAttribute(Long id, Attribute attribute);

    /**
     * Counts the number of products whose IDs are included in the provided set.
     * This query can be useful for counting products associated with certain attributes.
     *
     * @param ids a set of value IDs to count.
     * @return the number of values that match the given set of IDs.
     */
    @Query("SELECT COUNT(v) FROM AttributeValue v WHERE v.id IN :ids")
    int countByIds(@Param("ids") Set<Long> ids);

}
