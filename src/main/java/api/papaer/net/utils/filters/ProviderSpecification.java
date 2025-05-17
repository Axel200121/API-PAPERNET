package api.papaer.net.utils.filters;

import api.papaer.net.entities.ProviderEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProviderSpecification {
    public static Specification<ProviderEntity> withFilter(String idProvider, String status){
        return(root, query, cb)->{
            List<Predicate> predicates = new ArrayList<>();

            if (idProvider != null && !idProvider.isEmpty())
                predicates.add(cb.equal(root.get("id"), idProvider));

            if (status != null && !status.isEmpty())
                predicates.add(cb.equal(root.get("status"), status));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
