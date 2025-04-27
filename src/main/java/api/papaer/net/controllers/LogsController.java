package api.papaer.net.controllers;


import api.papaer.net.dtos.AuditLogDto;
import api.papaer.net.dtos.ShoppingDto;
import api.papaer.net.services.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("api/paper/logs")
public class LogsController {

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping("/get-alls")
    public ResponseEntity<Page<AuditLogDto>> executeGetListLogs(
            @RequestParam(required = false) String idLog,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String idUser,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        Page<AuditLogDto> result = auditLogService.executeListLogs(idLog, module, action, idUser, startDate, endDate, page, size);
        return ResponseEntity.ok(result);
    }
}
