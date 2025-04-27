package api.papaer.net.utils.logs;


import api.papaer.net.dtos.AuditLogDto;
import api.papaer.net.entities.UserEntity;
import api.papaer.net.mappers.AuditLogMapper;
import api.papaer.net.repositories.UserRepository;
import api.papaer.net.services.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class LogsInsert {


    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private UserRepository userRepository;

    public void saveLog(AuditLogDto auditLogDto){
        // Primero intentamos obtener el usuario autenticado por email
        UserEntity user = this.userRepository.findByEmail(getCurrentUserEmail()).orElse(null);

        // Si no encontramos el usuario por email, intentamos con entityId
        if (user == null && auditLogDto.getEntityId() != null) {
            user = this.userRepository.findById(auditLogDto.getEntityId()).orElse(null);
        }


        // Ahora seteamos el usuario en el DTO
        auditLogDto.setUser(user);

        // Añadimos la IP y el User-Agent
        auditLogDto.setIpAddress(getClientIpAddress());
        auditLogDto.setUserAgent(getUserAgent());

        // Finalmente, guardamos el log
        this.auditLogService.executeSaveLog(auditLogDto);
    }

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getName(); // normalemente el username (email)
    }


    private String getClientIpAddress() {
        HttpServletRequest request = getCurrentHttpRequest();
        if (request == null) {
            return "UNKNOWN";
        }
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty()) {
            ipAddress = request.getRemoteAddr();
        } else {
            // Si hay múltiples IPs (proxy), tomar la primera
            ipAddress = ipAddress.split(",")[0];
        }
        return ipAddress;
    }

    private String getUserAgent() {
        HttpServletRequest request = getCurrentHttpRequest();
        if (request == null) {
            return "UNKNOWN";
        }
        return request.getHeader("User-Agent");
    }

    private HttpServletRequest getCurrentHttpRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            return ((ServletRequestAttributes) requestAttributes).getRequest();
        }
        return null;
    }
}
