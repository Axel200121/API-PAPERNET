package api.papaer.net.utils;

public enum StatusUser {

    ACTIVE, //"El usuario está habilitado para acceder al sistema."
    INACTIVE, //"El usuario no tiene acceso al sistema."
    SUSPENDED, //"El usuario está suspendido temporalmente."
    PENDING_ACTIVATION, //"El usuario está pendiente de activación, necesita completar su registro."
    DELETED //"El usuario ha sido eliminado permanentemente del sistema."

}
