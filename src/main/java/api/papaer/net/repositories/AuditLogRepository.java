package api.papaer.net.repositories;

import api.papaer.net.entities.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity,String>, JpaSpecificationExecutor<AuditLogEntity> {
}
