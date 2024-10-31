package eshop.backend.repository;

import eshop.backend.model.Attribute;
import eshop.backend.model.AttributeValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttributeValueRepository extends JpaRepository<AttributeValue, Long> {
    List<AttributeValue> findByAttribute(Attribute attribute);
}
