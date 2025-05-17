package api.papaer.net.repositories;

import api.papaer.net.entities.ProviderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface ProviderRepository extends JpaRepository<ProviderEntity,String>, JpaSpecificationExecutor<ProviderEntity> {

    Optional<ProviderEntity> findByEmail(String email);
}
