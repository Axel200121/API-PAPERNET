package api.papaer.net.dtos;

import api.papaer.net.utils.StatusRegister;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Date;

public class ProviderDto {

    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    @NotNull(message = "El nombre es obligatorio")
    private String name;


    @Size(max = 200, message = "La dirección no debe superar los 200 caracteres")
    private String address;

    @NotBlank(message = "El numero telefonico es obligatorio")
    @NotNull(message = "El numero telefonico es obligatorio")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "El teléfono debe contener entre 10 y 15 dígitos numéricos")
    private String phone;

    @NotBlank(message = "El correo electronico es obligatorio")
    @NotNull(message = "El correo electronico es obligatorio")
    private String email;

    @NotNull(message = "El status es obligatorio")
    private StatusRegister status;

    private Date createdAt;

    private Date updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public StatusRegister getStatus() {
        return status;
    }

    public void setStatus(StatusRegister status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
