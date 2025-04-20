package api.papaer.net.repositories;

import api.papaer.net.entities.ItemShoppingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemShoppingRepository extends JpaRepository<ItemShoppingEntity,String> {

    Optional<List<ItemShoppingEntity>>  findByShoppingId(String idShopping);
    void deleteByShoppingId(String idShopping);


}
