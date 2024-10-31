package eshop.backend.repository;

import eshop.backend.model.Category;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("select c from Category c where c.name=:name and c.parent.name=:parentCategoryName")
    Category findCategoryByNameAndParent(
            @Param("name") String name,
            @Param("parentCategoryName") String parent
    );

    @EntityGraph(attributePaths = "products")
    Optional<Category> findById(Long id);

}