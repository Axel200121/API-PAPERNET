package api.papaer.net.utils.filters;

import api.papaer.net.entities.PermissionEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PermissionSpecificationPermission {

    public static Specification<PermissionEntity> withFilter(String idPermission, String status){

        return (root, query, cb) ->{
            List<Predicate> predicates  = new ArrayList<>();

            if (idPermission != null && !idPermission.isEmpty())
                predicates.add(cb.equal(root.get("id"), idPermission));

            if (status != null && !status.isEmpty())
                predicates.add(cb.equal(root.get("status"), status));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    }
