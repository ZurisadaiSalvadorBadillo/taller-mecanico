package com.taller.auth.service;

import com.taller.auth.dto.WorkshopRegistrationRequest;
import com.taller.auth.dto.WorkshopSummaryResponse;
import com.taller.auth.model.Workshop;
import com.taller.auth.repository.WorkshopRepository;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkshopRegistrationFacade {
    private final WorkshopRepository workshopRepository;
    private final WorkshopPhotoStorage photoStorage;

    public WorkshopRegistrationFacade(WorkshopRepository workshopRepository, WorkshopPhotoStorage photoStorage) {
        this.workshopRepository = workshopRepository;
        this.photoStorage = photoStorage;
    }

    @Transactional
    public WorkshopSummaryResponse register(WorkshopRegistrationRequest request) {
        String name = request.getName().trim();
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        String rfc = request.getRfc().trim().toUpperCase(Locale.ROOT);
        if (workshopRepository.existsWithDuplicateIdentity(name, email, rfc)) {
            throw new DuplicateWorkshopException();
        }

        WorkshopPhotoStorage.StoredPhoto photo = photoStorage.store(request.getPhoto());
        try {
            Workshop workshop = new Workshop();
            workshop.setName(name);
            workshop.setState(request.getState().trim());
            workshop.setPostalCode(request.getPostalCode());
            workshop.setMunicipality(request.getMunicipality().trim());
            workshop.setNeighborhood(request.getNeighborhood().trim());
            workshop.setLocality(request.getLocality().trim());
            workshop.setStreet(request.getStreet().trim());
            workshop.setBusinessName(request.getBusinessName().trim());
            workshop.setPhone(request.getPhone().replaceAll("[^0-9+]", ""));
            workshop.setRfc(rfc);
            workshop.setEmail(email);
            workshop.setPhotoPath(photo.filename());
            workshop.setPhotoContentType(photo.contentType());
            workshop.setCreatedAt(Instant.now());
            Workshop saved = workshopRepository.save(workshop);
            return new WorkshopSummaryResponse(saved.getId(), saved.getName());
        } catch (RuntimeException exception) {
            photoStorage.deleteQuietly(photo.filename());
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public List<WorkshopSummaryResponse> list() {
        return workshopRepository.findAllByOrderByNameAsc().stream()
                .map(workshop -> new WorkshopSummaryResponse(workshop.getId(), workshop.getName()))
                .toList();
    }
}
