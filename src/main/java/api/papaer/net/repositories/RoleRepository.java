package api.papaer.net.repositories;

import api.papaer.net.entities.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RoleRepository extends JpaRepository<RoleEntity,String >, JpaSpecificationExecutor<RoleEntity> {

    Page<RoleEntity> findAll(Pageable pageable);
}
