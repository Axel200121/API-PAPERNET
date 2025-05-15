package api.papaer.net.repositories;

import api.papaer.net.entities.CustomerEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerEntity,String>, JpaSpecificationExecutor<CustomerEntity> {
    Optional<CustomerEntity> findByEmail(String email);
}
