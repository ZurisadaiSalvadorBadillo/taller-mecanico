package com.taller.auth.controller;
import com.taller.auth.dto.VehicleRequest;
import com.taller.auth.dto.VehicleResponse;
import com.taller.auth.service.VehicleService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController @RequestMapping("/api/clients/{clientId}/vehicles") @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SECRETARIA')")
public class VehicleController {
    private final VehicleService service; public VehicleController(VehicleService service) { this.service = service; }
    @GetMapping public ResponseEntity<List<VehicleResponse>> list(@PathVariable Long clientId) { return ResponseEntity.ok(service.list(clientId)); }
    @PostMapping public ResponseEntity<VehicleResponse> create(@PathVariable Long clientId, @Valid @RequestBody VehicleRequest request) { return ResponseEntity.ok(service.create(clientId, request)); }
}
