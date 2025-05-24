package api.papaer.net.repositories;

import api.papaer.net.entities.InventoryMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InventoryMovementRepository extends JpaRepository<InventoryMovementEntity,String>, JpaSpecificationExecutor<InventoryMovementEntity> {

}
