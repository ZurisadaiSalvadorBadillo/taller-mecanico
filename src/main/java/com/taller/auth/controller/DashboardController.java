package com.taller.auth.controller;

import com.taller.auth.dto.MessageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class DashboardController {
    @GetMapping("/manager/dashboard")
    public MessageResponse managerDashboard() {
        return new MessageResponse("Panel de gerente: acceso completo a ordenes, usuarios y reportes.");
    }

    @GetMapping("/mechanic/orders")
    public MessageResponse mechanicOrders() {
        return new MessageResponse("Panel de mecanico: actualizar estatus de reparaciones.");
    }

    @GetMapping("/client/status")
    public MessageResponse clientStatus() {
        return new MessageResponse("Panel de cliente: visualizar el estatus del vehiculo.");
    }
}
