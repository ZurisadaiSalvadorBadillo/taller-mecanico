package com.taller.auth.service;

import com.taller.auth.dto.ClientRegistrationRequest;
import com.taller.auth.dto.ClientRegistrationResponse;
import com.taller.auth.dto.ClientSummaryResponse;
import com.taller.auth.dto.ClientUpdateRequest;
import com.taller.auth.model.Client;
import com.taller.auth.model.Workshop;
import com.taller.auth.repository.ClientRepository;
import com.taller.auth.repository.WorkshopRepository;
import java.time.Instant;
import java.util.Locale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientRegistrationFacade {
    private final ClientRepository clientRepository;
    private final WorkshopRepository workshopRepository;
    private final ClientPhotoStorage photoStorage;

    public ClientRegistrationFacade(
            ClientRepository clientRepository,
            WorkshopRepository workshopRepository,
            ClientPhotoStorage photoStorage
    ) {
        this.clientRepository = clientRepository;
        this.workshopRepository = workshopRepository;
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
        Workshop workshop = findWorkshop(request.getWorkshopId());

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
            client.setWorkshop(workshop);
            client.setStatus("ACTIVO");
            client.setCreatedAt(Instant.now());

            Client saved = clientRepository.save(client);
            return new ClientRegistrationResponse(saved.getId(), "Cliente registrado correctamente.");
        } catch (RuntimeException exception) {
            photoStorage.deleteQuietly(photo.filename());
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public Page<ClientSummaryResponse> listRegisteredClients(Long workshopId, int page, int size, Sort.Direction sortDirection) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDirection, "fullName"));
        return clientRepository.findByWorkshopId(workshopId, pageRequest).map(this::toSummary);
    }

    @Transactional
    public ClientSummaryResponse update(Long clientId, ClientUpdateRequest request) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado."));
        String personalEmail = normalizeEmail(request.getPersonalEmail());
        String workEmail = normalizeEmail(request.getWorkEmail());
        String personalPhone = normalizePhone(request.getPersonalPhone());
        String workPhone = normalizePhone(request.getWorkPhone());
        if (clientRepository.existsWithAnyContactExcludingClient(
                clientId, personalEmail, workEmail, personalPhone, workPhone)) {
            throw new DuplicateClientException();
        }

        ClientPhotoStorage.StoredPhoto replacementPhoto = null;
        String previousPhoto = client.getPhotoPath();
        if (request.getPhoto() != null && !request.getPhoto().isEmpty()) {
            replacementPhoto = photoStorage.store(request.getPhoto());
        }
        try {
            client.setFullName(request.getFullName().trim());
            client.setAlternateContactName(request.getAlternateContactName().trim());
            client.setBirthDate(request.getBirthDate());
            client.setPersonalPhone(personalPhone);
            client.setWorkPhone(workPhone);
            client.setPersonalEmail(personalEmail);
            client.setWorkEmail(workEmail);
            client.setStreet(request.getStreet().trim());
            client.setNeighborhood(request.getNeighborhood().trim());
            client.setMunicipality(request.getMunicipality().trim());
            client.setState(request.getState().trim());
            client.setPostalCode(request.getPostalCode());
            client.setWorkshop(findWorkshop(request.getWorkshopId()));
            if (replacementPhoto != null) {
                client.setPhotoPath(replacementPhoto.filename());
                client.setPhotoContentType(replacementPhoto.contentType());
            }
            Client saved = clientRepository.save(client);
            if (replacementPhoto != null) photoStorage.deleteQuietly(previousPhoto);
            return toSummary(saved);
        } catch (RuntimeException exception) {
            if (replacementPhoto != null) photoStorage.deleteQuietly(replacementPhoto.filename());
            throw exception;
        }
    }

    @Transactional
    public ClientSummaryResponse suspend(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado."));
        client.setStatus("SUSPENDIDO");
        return toSummary(clientRepository.save(client));
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

    private Workshop findWorkshop(Long workshopId) {
        return workshopRepository.findById(workshopId)
                .orElseThrow(() -> new IllegalArgumentException("El taller seleccionado no existe."));
    }

    private ClientSummaryResponse toSummary(Client client) {
        return new ClientSummaryResponse(
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
                client.getWorkshop().getId(),
                client.getWorkshop().getName(),
                client.getStatus(),
                "/clients/" + client.getId() + "/photo"
        );
    }
}
