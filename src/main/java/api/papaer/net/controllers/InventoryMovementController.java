package api.papaer.net.controllers;

import api.papaer.net.services.InventoryMovementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/paper/inventory-movements")
public class InventoryMovementController {

    @Autowired
    private InventoryMovementService inventoryMovementService;


}
