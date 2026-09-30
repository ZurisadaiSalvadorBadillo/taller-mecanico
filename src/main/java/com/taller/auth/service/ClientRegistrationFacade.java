package com.taller.auth.service;

import com.taller.auth.dto.ClientRegistrationRequest;
import com.taller.auth.dto.ClientRegistrationResponse;
import com.taller.auth.dto.ClientSummaryResponse;
import com.taller.auth.model.Client;
import com.taller.auth.repository.ClientRepository;
import java.time.Instant;
import java.util.Locale;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientRegistrationFacade {
    private final ClientRepository clientRepository;
    private final ClientPhotoStorage photoStorage;

    public ClientRegistrationFacade(ClientRepository clientRepository, ClientPhotoStorage photoStorage) {
        this.clientRepository = clientRepository;
        this.photoStorage = photoStorage;
    }

    @Transactional
    public ClientRegistrationResponse register(ClientRegistrationRequest request) {
        String personalEmail = normalizeEmail(request.getPersonalEmail());
        String workEmail = normalizeEmail(request.getWorkEmail());
        String personalPhone = normalizePhone(request.getPersonalPhone());
        String workPhone = normalizePhone(request.getWorkPhone());

        if (clientRepository.existsWithAnyContact(personalEmail, workEmail, personalPhone, workPhone)) {
            throw new DuplicateClientException();
        }

        ClientPhotoStorage.StoredPhoto photo = photoStorage.store(request.getPhoto());
        try {
            Client client = new Client();
            client.setFullName(request.getFullName().trim());
            client.setAlternateContactName(request.getAlternateContactName().trim());
            client.setBirthDate(request.getBirthDate());
            client.setPersonalPhone(personalPhone);
            client.setWorkPhone(workPhone);
            client.setPersonalEmail(personalEmail);
            client.setWorkEmail(workEmail);
            client.setPhotoPath(photo.filename());
            client.setPhotoContentType(photo.contentType());
            client.setStreet(request.getStreet().trim());
            client.setNeighborhood(request.getNeighborhood().trim());
            client.setMunicipality(request.getMunicipality().trim());
            client.setState(request.getState().trim());
            client.setPostalCode(request.getPostalCode());
            client.setCreatedAt(Instant.now());

            Client saved = clientRepository.save(client);
            return new ClientRegistrationResponse(saved.getId(), "Cliente registrado correctamente.");
        } catch (RuntimeException exception) {
            photoStorage.deleteQuietly(photo.filename());
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public List<ClientSummaryResponse> listRegisteredClients() {
        return clientRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(client -> new ClientSummaryResponse(
                        client.getId(),
                        client.getFullName(),
                        client.getAlternateContactName(),
                        client.getBirthDate().toString(),
                        client.getPersonalPhone(),
                        client.getWorkPhone(),
                        client.getPersonalEmail(),
                        client.getWorkEmail(),
                        client.getStreet(),
                        client.getNeighborhood(),
                        client.getMunicipality(),
                        client.getState(),
                        client.getPostalCode(),
                        "/clients/" + client.getId() + "/photo"
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public ClientPhotoStorage.StoredClientPhoto getPhoto(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado."));
        return new ClientPhotoStorage.StoredClientPhoto(
                photoStorage.load(client.getPhotoPath()),
                client.getPhotoContentType()
        );
    }

    private String normalizeEmail(String value) {
        return value.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizePhone(String value) {
        return value.replaceAll("[^0-9+]", "");
    }
}
