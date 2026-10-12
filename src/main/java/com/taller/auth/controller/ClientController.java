package com.taller.auth.controller;

import com.taller.auth.dto.ClientRegistrationRequest;
import com.taller.auth.dto.ClientRegistrationResponse;
import com.taller.auth.dto.ClientSummaryResponse;
import com.taller.auth.dto.ClientUpdateRequest;
import com.taller.auth.service.ClientRegistrationFacade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clients")
@Validated
public class ClientController {
    private final ClientRegistrationFacade clientRegistrationFacade;

    public ClientController(ClientRegistrationFacade clientRegistrationFacade) {
        this.clientRegistrationFacade = clientRegistrationFacade;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SECRETARIA')")
    public ResponseEntity<ClientRegistrationResponse> register(@Valid @ModelAttribute ClientRegistrationRequest request) {
        return ResponseEntity.ok(clientRegistrationFacade.register(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SECRETARIA')")
    public ResponseEntity<Page<ClientSummaryResponse>> list(
            @RequestParam Long workshopId,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(10) int size,
            @RequestParam(defaultValue = "ASC") String sortDirection
    ) {
        return ResponseEntity.ok(clientRegistrationFacade.listRegisteredClients(
                workshopId, page, size, Sort.Direction.fromString(sortDirection)
        ));
    }

    @PutMapping(value = "/{clientId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SECRETARIA')")
    public ResponseEntity<ClientSummaryResponse> update(
            @PathVariable Long clientId,
            @Valid @ModelAttribute ClientUpdateRequest request
    ) {
        return ResponseEntity.ok(clientRegistrationFacade.update(clientId, request));
    }

    @PatchMapping("/{clientId}/suspension")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<ClientSummaryResponse> suspend(@PathVariable Long clientId) {
        return ResponseEntity.ok(clientRegistrationFacade.suspend(clientId));
    }

    @GetMapping("/{clientId}/photo")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SECRETARIA')")
    public ResponseEntity<Resource> photo(@PathVariable Long clientId) {
        var photo = clientRegistrationFacade.getPhoto(clientId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(photo.contentType()))
                .body(photo.resource());
    }
}
