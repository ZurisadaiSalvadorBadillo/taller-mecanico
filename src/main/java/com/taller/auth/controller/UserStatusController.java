package com.taller.auth.controller;

import com.taller.auth.dto.UserStatusRequest;
import com.taller.auth.dto.UserStatusResponse;
import com.taller.auth.service.UserStatusService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/user-statuses") @PreAuthorize("hasRole('ADMINISTRADOR')")
public class UserStatusController {
    private final UserStatusService service;
    public UserStatusController(UserStatusService service) { this.service = service; }
    @GetMapping public ResponseEntity<List<UserStatusResponse>> list() { return ResponseEntity.ok(service.list()); }
    @PostMapping public ResponseEntity<UserStatusResponse> create(@Valid @RequestBody UserStatusRequest request) { return ResponseEntity.ok(service.create(request)); }
    @PutMapping("/{id}") public ResponseEntity<UserStatusResponse> update(@PathVariable Long id, @Valid @RequestBody UserStatusRequest request) { return ResponseEntity.ok(service.update(id, request)); }
    @DeleteMapping("/{id}") public ResponseEntity<Void> delete(@PathVariable Long id) { service.delete(id); return ResponseEntity.noContent().build(); }
}
