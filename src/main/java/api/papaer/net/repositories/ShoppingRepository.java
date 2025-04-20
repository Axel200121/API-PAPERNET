package api.papaer.net.repositories;

import api.papaer.net.entities.ShoppingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ShoppingRepository extends JpaRepository<ShoppingEntity,String>, JpaSpecificationExecutor<ShoppingEntity> {

    @Query("SELECT TO_CHAR(s.date, 'YYYY-MM') as mes, SUM(s.total) FROM ShoppingEntity s GROUP BY mes ORDER BY mes")
    List<Object[]> findTotalByMonth();

    @Query("SELECT s.provider.name, COUNT(s) FROM ShoppingEntity s WHERE s.provider IS NOT NULL GROUP BY s.provider.name")
    List<Object[]> findShoppingByProvider();

    @Query("SELECT s.status, COUNT(s) FROM ShoppingEntity s GROUP BY s.status")
    List<Object[]> findShoppingByStatus();

}
