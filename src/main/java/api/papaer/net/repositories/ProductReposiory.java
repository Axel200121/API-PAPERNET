package api.papaer.net.repositories;

import api.papaer.net.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProductReposiory extends JpaRepository<ProductEntity,String>, JpaSpecificationExecutor<ProductEntity> {

    Optional<ProductEntity> findByCodeProduct(String codeProduct);
}
