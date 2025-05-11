package api.papaer.net.utils.filters;

import api.papaer.net.entities.UserEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<UserEntity> withFilter(String idUser, String status, String idRole){

        return (root, query, cb) ->{
            List<Predicate> predicates  = new ArrayList<>();

            if (idUser != null && !idUser.isEmpty())
                predicates.add(cb.equal(root.get("user").get("id"), idUser));

            if (status != null && !status.isEmpty())
                predicates.add(cb.equal(root.get("status"), status));

            if (idRole != null && !idRole.isEmpty())
                predicates.add(cb.equal(root.get("role").get("id"), idRole));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

}
