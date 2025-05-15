package api.papaer.net.utils.filters;

import api.papaer.net.entities.CustomerEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CustomerSpecification {

    public static Specification<CustomerEntity> withFilter(String idCustomer, String status){
        return(root, query, cb) ->{
            List<Predicate> predicates = new ArrayList<>();

            if (idCustomer != null && !idCustomer.isEmpty())
                predicates.add(cb.equal(root.get("id"), idCustomer));

            if (status != null && !status.isEmpty())
                predicates.add(cb.equal(root.get("status"), status));

            return  cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
