package api.papaer.net.utils;

public enum StatusCustomer {

    ACTIVE, // El cliente está habilitado y puede realizar compras.
    INACTIVE, // El cliente no puede realizar compras o interactuar con el sistema.
    SUSPENDED, // El cliente está suspendido temporalmente.
    PENDING_VERIFICATION, // El cliente está pendiente de verificación.
    DELETED, // El cliente ha sido eliminado del sistema.
    BLOCKED, // El cliente tiene restricciones para realizar ciertas acciones.


}
