package api.papaer.net.utils.filters;

import api.papaer.net.entities.CategoryEntity;
import api.papaer.net.entities.ProductEntity;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProductSpecification {
    public static Specification<ProductEntity> withFilter(String idProduct, String idCategory, String idProvider, String status){
        return(root, query, cb)->{
            List<Predicate> predicates = new ArrayList<>();

            if (idProduct != null && !idProduct.isEmpty())
                predicates.add(cb.equal(root.get("id"), idProduct));

            if (idCategory != null && !idCategory.isEmpty())
                predicates.add(cb.equal(root.get("category").get("id"), idCategory));

            if (idProvider != null && !idProvider.isEmpty())
                predicates.add(cb.equal(root.get("provider").get("id"), idProvider));

            if (status != null && !status.isEmpty())
                predicates.add(cb.equal(root.get("status"), status));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
