package api.papaer.net.repositories;

import api.papaer.net.entities.RoleEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity,String > {

    Page<RoleEntity> findAll(Pageable pageable);
}
