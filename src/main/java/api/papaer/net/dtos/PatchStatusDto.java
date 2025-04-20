package api.papaer.net.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PatchStatusDto {

    @NotNull(message = "Status es obligatorio")
    @NotBlank(message = "Status es obligatorio")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
