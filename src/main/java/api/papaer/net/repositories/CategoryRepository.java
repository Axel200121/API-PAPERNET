package api.papaer.net.repositories;

import api.papaer.net.entities.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CategoryRepository extends JpaRepository<CategoryEntity, String>, JpaSpecificationExecutor<CategoryEntity> {

}
