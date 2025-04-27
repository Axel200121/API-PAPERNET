package api.papaer.net.mappers;

import api.papaer.net.dtos.AuditLogDto;
import api.papaer.net.entities.AuditLogEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {

    @Autowired
    private ModelMapper modelMapper;

    public AuditLogDto convertToDto(AuditLogEntity auditLogEntity){
        return this.modelMapper.map(auditLogEntity, AuditLogDto.class);
    }

    public AuditLogEntity convertToEntity(AuditLogDto auditLogDto){
        return this.modelMapper.map(auditLogDto, AuditLogEntity.class);
    }
}
