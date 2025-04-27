package api.papaer.net.utils.filters;

import api.papaer.net.entities.ShoppingEntity;
import api.papaer.net.utils.StatusShopping;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShoppingSpecification {

    public static Specification<ShoppingEntity> withFilter(String idShopping, String idUser, String idProvider, String status, Date startDate, Date endDate){
        return (root, query, cb)->{
            List<Predicate> predicates = new ArrayList<>();


            if (idShopping != null && !idShopping.isEmpty())
                predicates.add(cb.equal(root.get("shopping").get("id"), idShopping));

            if (idUser != null && !idUser.isEmpty())
                predicates.add(cb.equal(root.get("user").get("id"), idUser));

            if (idProvider != null && !idProvider.isEmpty())
                predicates.add(cb.equal(root.get("provider").get("id"),idProvider));

            if (status != null && !status.isEmpty())
                predicates.add(cb.equal(root.get("status"), StatusShopping.valueOf(status)));

            if (startDate != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDate));

            if (endDate != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDate));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
