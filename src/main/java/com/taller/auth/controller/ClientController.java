package com.taller.auth.controller;

import com.taller.auth.dto.ClientRegistrationRequest;
import com.taller.auth.dto.ClientRegistrationResponse;
import com.taller.auth.dto.ClientSummaryResponse;
import com.taller.auth.service.ClientRegistrationFacade;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
    private final ClientRegistrationFacade clientRegistrationFacade;

    public ClientController(ClientRegistrationFacade clientRegistrationFacade) {
        this.clientRegistrationFacade = clientRegistrationFacade;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ClientRegistrationResponse> register(@Valid @ModelAttribute ClientRegistrationRequest request) {
        return ResponseEntity.ok(clientRegistrationFacade.register(request));
    }

    @GetMapping
    public ResponseEntity<List<ClientSummaryResponse>> list() {
        return ResponseEntity.ok(clientRegistrationFacade.listRegisteredClients());
    }

    @GetMapping("/{clientId}/photo")
    public ResponseEntity<Resource> photo(@PathVariable Long clientId) {
        var photo = clientRegistrationFacade.getPhoto(clientId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(photo.contentType()))
                .body(photo.resource());
    }
}
