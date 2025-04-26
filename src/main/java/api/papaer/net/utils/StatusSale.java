package api.papaer.net.utils;

public enum StatusSale {
    PENDING,    // Order created but not yet paid
    PAID,       // Payment received
    PROCESSING, // Order is being prepared
    SHIPPED,    // Order has been shipped to the customer
    DELIVERED,  // Order has been delivered to the customer
    CANCELED,   // Order was canceled before shipping
    RETURNED    // Order was returned by the customer
}
