package api.papaer.net.repositories;

import api.papaer.net.entities.ShoppingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShoppingRepository extends JpaRepository<ShoppingEntity,String> {

    //Page<ShoppingEntity> findAll(Pageable pageable);

}
