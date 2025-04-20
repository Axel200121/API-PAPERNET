package api.papaer.net.repositories;

import api.papaer.net.entities.ShoppingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ShoppingRepository extends JpaRepository<ShoppingEntity,String>, JpaSpecificationExecutor<ShoppingEntity> {

    //Page<ShoppingEntity> findAll(Pageable pageable);

}
