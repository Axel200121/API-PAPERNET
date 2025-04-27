package api.papaer.net.services;

import api.papaer.net.dtos.AuditLogDto;
import org.springframework.data.domain.Page;

import java.util.Date;

public interface AuditLogService {

    void executeSaveLog(AuditLogDto auditLogDto);
    Page<AuditLogDto> executeListLogs(String idLog, String module, String action, String idUser, Date startDate, Date endDate, int size, int page);

}
