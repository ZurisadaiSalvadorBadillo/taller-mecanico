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
    private final UserStatusService userStatusService;

    public ClientRegistrationFacade(
            ClientRepository clientRepository,
            WorkshopRepository workshopRepository,
            ClientPhotoStorage photoStorage,
            UserStatusService userStatusService
    ) {
        this.clientRepository = clientRepository;
        this.workshopRepository = workshopRepository;
        this.photoStorage = photoStorage;
        this.userStatusService = userStatusService;
    }

    @Transactional
    public ClientRegistrationResponse register(ClientRegistrationRequest request) {
        String personalEmail = normalizeEmail(request.getContactEmail());
        String workEmail = personalEmail;
        String personalPhone = normalizePhone(request.getPersonalMobile());
        String workPhone = normalizePhone(request.getWorkPhone());
        if (clientRepository.existsByFirstNameIgnoreCaseAndPaternalSurnameIgnoreCaseAndMaternalSurnameIgnoreCase(
                request.getFirstName(), request.getPaternalSurname(), request.getMaternalSurname())) {
            throw new DuplicateClientException();
        }
        Workshop workshop = findWorkshop(request.getWorkshopId());

        ClientPhotoStorage.StoredPhoto photo = photoStorage.store(request.getPhoto());
        try {
            Client client = new Client();
            applyNormalized(client, request);
            client.setBirthDate(request.getBirthDate());
            client.setPersonalPhone(personalPhone);
            client.setWorkPhone(workPhone);
            client.setPersonalEmail(personalEmail);
            client.setWorkEmail(workEmail);
            client.setPhotoPath(photo.filename());
            client.setPhotoContentType(photo.contentType());
            client.setStreet(request.getStreet().trim());
            client.setNeighborhood(request.getLocality().trim());
            client.setMunicipality(request.getMunicipality().trim());
            client.setState(request.getState().trim());
            client.setPostalCode(request.getPostalCode());
            client.setWorkshop(workshop);
            client.setStatus("ACTIVO");
            client.setUserStatus(userStatusService.required("ACTIVO"));
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
        String personalEmail = normalizeEmail(request.getContactEmail());
        String workEmail = personalEmail;
        String personalPhone = normalizePhone(request.getPersonalMobile());
        String workPhone = normalizePhone(request.getWorkPhone());
        if (clientRepository.existsByFullNameExcludingClient(clientId, request.getFirstName(), request.getPaternalSurname(), request.getMaternalSurname())) {
            throw new DuplicateClientException();
        }

        ClientPhotoStorage.StoredPhoto replacementPhoto = null;
        String previousPhoto = client.getPhotoPath();
        if (request.getPhoto() != null && !request.getPhoto().isEmpty()) {
            replacementPhoto = photoStorage.store(request.getPhoto());
        }
        try {
            applyNormalized(client, request);
            client.setBirthDate(request.getBirthDate());
            client.setPersonalPhone(personalPhone);
            client.setWorkPhone(workPhone);
            client.setPersonalEmail(personalEmail);
            client.setWorkEmail(workEmail);
            client.setStreet(request.getStreet().trim());
            client.setNeighborhood(request.getLocality().trim());
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
        if ("SUSPENDIDO".equals(client.getStatus())) throw new IllegalArgumentException("El cliente ya se encuentra suspendido.");
        client.setStatus("SUSPENDIDO");
        client.setUserStatus(userStatusService.required("SUSPENDIDO"));
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
                client.getFirstName(),
                client.getPaternalSurname(),
                client.getMaternalSurname(),
                client.getFullName(),
                client.getCurp(),
                client.getRfc(),
                client.getBirthDate().toString(),
                client.getAge(),
                client.getContactEmail(),
                client.getContactMobile(),
                client.getPersonalMobile(),
                client.getWorkPhone(),
                client.getStreet(),
                client.getLocality(),
                client.getMunicipality(),
                client.getState(),
                client.getPostalCode(),
                client.getWorkshop().getId(),
                client.getWorkshop().getName(),
                client.getStatus(),
                "/clients/" + client.getId() + "/photo"
        );
    }

    private void applyNormalized(Client client, ClientRegistrationRequest request) {
        String firstName = titleCase(request.getFirstName());
        String paternal = titleCase(request.getPaternalSurname());
        String maternal = titleCase(request.getMaternalSurname());
        client.setFirstName(firstName); client.setPaternalSurname(paternal); client.setMaternalSurname(maternal);
        client.setFullName(firstName + " " + paternal + " " + maternal);
        client.setAlternateContactName(request.getContactEmail().trim());
        client.setCurp(request.getCurp().trim().toUpperCase(Locale.ROOT)); client.setRfc(request.getRfc().trim().toUpperCase(Locale.ROOT));
        client.setAge(request.getAge()); client.setContactEmail(normalizeEmail(request.getContactEmail()));
        client.setContactMobile(normalizePhone(request.getContactMobile())); client.setPersonalMobile(normalizePhone(request.getPersonalMobile()));
        client.setLocality(titleCase(request.getLocality())); client.setNeighborhood(titleCase(request.getLocality()));
        client.setStreet(titleCase(request.getStreet())); client.setMunicipality(titleCase(request.getMunicipality())); client.setState(titleCase(request.getState()));
    }

    private String titleCase(String value) { return java.util.Arrays.stream(value.trim().toLowerCase(Locale.ROOT).split("\\s+")).filter(part -> !part.isBlank()).map(part -> Character.toUpperCase(part.charAt(0)) + part.substring(1)).collect(java.util.stream.Collectors.joining(" ")); }
}
