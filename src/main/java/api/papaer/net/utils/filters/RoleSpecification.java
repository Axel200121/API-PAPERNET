package api.papaer.net.utils.filters;

import api.papaer.net.entities.RoleEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class RoleSpecification {
    public static Specification<RoleEntity> withFilter(String idRole, String status){

        return (root, query, cb) ->{
            List<Predicate> predicates  = new ArrayList<>();

            if (idRole != null && !idRole.isEmpty())
                predicates.add(cb.equal(root.get("id"), idRole));

            if (status != null && !status.isEmpty())
                predicates.add(cb.equal(root.get("status"), status));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
