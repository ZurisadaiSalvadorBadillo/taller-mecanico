package com.taller.auth.controller;

import com.taller.auth.dto.WorkshopRegistrationRequest;
import com.taller.auth.dto.WorkshopSummaryResponse;
import com.taller.auth.service.WorkshopRegistrationFacade;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workshops")
public class WorkshopController {
    private final WorkshopRegistrationFacade workshopRegistrationFacade;

    public WorkshopController(WorkshopRegistrationFacade workshopRegistrationFacade) {
        this.workshopRegistrationFacade = workshopRegistrationFacade;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SECRETARIA')")
    public ResponseEntity<WorkshopSummaryResponse> register(@Valid @ModelAttribute WorkshopRegistrationRequest request) {
        return ResponseEntity.ok(workshopRegistrationFacade.register(request));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'SECRETARIA')")
    public ResponseEntity<List<WorkshopSummaryResponse>> list() {
        return ResponseEntity.ok(workshopRegistrationFacade.list());
    }
}
