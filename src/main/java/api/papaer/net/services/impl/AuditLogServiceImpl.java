package api.papaer.net.services.impl;

import api.papaer.net.dtos.AuditLogDto;
import api.papaer.net.entities.AuditLogEntity;
import api.papaer.net.mappers.AuditLogMapper;
import api.papaer.net.repositories.AuditLogRepository;
import api.papaer.net.services.AuditLogService;
import api.papaer.net.utils.filters.LogsSpecification;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogServiceImpl.class);


    @Autowired
    private AuditLogRepository  auditLogRepository;

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Override
    public void executeSaveLog(AuditLogDto auditLogDto) {
        AuditLogEntity save = this.auditLogMapper.convertToEntity(auditLogDto);
        LOGGER.info("DATA EN EL SERVICE {}", save);
        this.auditLogRepository.save(this.auditLogMapper.convertToEntity(auditLogDto));
    }

    @Override
    public Page<AuditLogDto> executeListLogs(String idLog, String module, String action, String idUser, Date startDate, Date endDate, int size, int page) {
        try {

            Pageable pageable = PageRequest.of(page, size);
            Specification<AuditLogEntity> spec = LogsSpecification.withFilter(idLog, module, action, idUser, startDate, endDate);

            Page<AuditLogEntity> listLogs =this.auditLogRepository.findAll(spec,pageable);

            if (listLogs.isEmpty())
                throw new BadRequestException("No hay trazabilidad registrada");

            return listLogs.map(auditLogMapper::convertToDto);

        } catch (Exception exception){
            throw  new RuntimeException("Error inesperado"+exception.getMessage());
        }
    }

}
