package api.papaer.net.utils.filters;

import api.papaer.net.entities.AuditLogEntity;
import api.papaer.net.utils.StatusAuditLog;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogsSpecification {

    public static Specification<AuditLogEntity> withFilter(String idLog, String module, String action, String idUser, Date startDate, Date endDate){
        return (root, query, cb)->{
            List<Predicate> predicates = new ArrayList<>();


            if (idLog != null && !idLog.isEmpty())
                predicates.add(cb.equal(root.get("id"), idLog));

            if (idUser != null && !idUser.isEmpty())
                predicates.add(cb.equal(root.get("user").get("id"), idUser));

            if (module != null && !module.isEmpty())
                predicates.add(cb.equal(root.get("module"),module));

            if (action != null && !action.isEmpty())
                predicates.add(cb.equal(root.get("action"), StatusAuditLog.valueOf(action)));

            if (startDate != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("date"), startDate));

            if (endDate != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("date"), endDate));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


}
