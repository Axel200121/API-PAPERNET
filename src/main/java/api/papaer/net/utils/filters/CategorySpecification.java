package api.papaer.net.utils.filters;

import api.papaer.net.entities.CategoryEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CategorySpecification {

    public static Specification<CategoryEntity> withFilter(String idCategory, String status){
        return(root, query, cb)->{
            List<Predicate> predicates = new ArrayList<>();

            if (idCategory != null && !idCategory.isEmpty())
                predicates.add(cb.equal(root.get("id"), idCategory));

            if (status != null && !status.isEmpty())
                predicates.add(cb.equal(root.get("status"), status));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
