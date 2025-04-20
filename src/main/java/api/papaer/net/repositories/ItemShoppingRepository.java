package api.papaer.net.repositories;

import api.papaer.net.entities.ItemShoppingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemShoppingRepository extends JpaRepository<ItemShoppingEntity,String> {

    //Page<ItemShoppingEntity> findall(Pageable pageable);
}
