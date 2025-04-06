package api.papaer.net.repositories;

import api.papaer.net.entities.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<CategoryEntity, String> {

    Page<CategoryEntity> findAll(Pageable pageable);
}
