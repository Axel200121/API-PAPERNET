package api.papaer.net.utils.filters;

import api.papaer.net.entities.InventoryMovementEntity;
import api.papaer.net.utils.StatusInventoryMovementType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class InventoryMovementSpecification {
    public static Specification<InventoryMovementEntity> withFilters(String type, String idProduct, String idUser, Date dateFrom, Date dateTo) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (type != null && !type.isEmpty()) {
                predicates.add(cb.equal(root.get("type"), StatusInventoryMovementType.valueOf(type)));
            }

            if (idProduct != null && !idProduct.isEmpty()) {
                predicates.add(cb.equal(root.get("product").get("id"), idProduct));
            }

            if (idUser != null && !idUser.isEmpty()) {
                predicates.add(cb.equal(root.get("user").get("id"), idUser));
            }

            if (dateFrom != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), dateFrom));
            }

            if (dateTo != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), dateTo));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
