package api.papaer.net.utils;

public enum StatusProduct {
    ACTIVE, //El producto está disponible para la venta o compra.
    INACTIVE, // El producto está desactivado temporalmente, no se puede vender ni comprar.
    OUT_OF_STOCK, // El producto está fuera de stock, pero aún sigue existiendo en el sistema.
    PENDING_REPLENISHMENT, // El producto está disponible para la venta, pero su stock está limitado y se espera una reposición.
    DISCOUNTED, // El producto está en una promoción o descuento especial.
    WITHDRAWN, // El producto ha sido retirado permanentemente del inventario y ya no está disponible.
    IN_PRODUCTION, // El producto está en proceso de ser fabricado o reabastecido.
    IN_TRANSIT // El producto está siendo enviado o está en camino, pero aún no está disponible en el inventario.
}
