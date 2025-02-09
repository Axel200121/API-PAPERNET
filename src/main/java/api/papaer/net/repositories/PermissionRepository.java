package api.papaer.net.repositories;

import api.papaer.net.entities.PermissionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<PermissionEntity, String > {

    Page<PermissionEntity> findAll(Pageable pageable);
}
