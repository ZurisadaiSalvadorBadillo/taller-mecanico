package com.taller.auth.service;

import com.taller.auth.dto.UserStatusRequest;
import com.taller.auth.dto.UserStatusResponse;
import com.taller.auth.model.UserStatus;
import com.taller.auth.repository.UserStatusRepository;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserStatusService {
    private final UserStatusRepository repository;
    public UserStatusService(UserStatusRepository repository) { this.repository = repository; }
    @Transactional(readOnly = true) public List<UserStatusResponse> list() { return repository.findByValueNotOrderByValueAsc("ELIMINADO").stream().map(this::toResponse).toList(); }
    @Transactional public UserStatusResponse create(UserStatusRequest request) { return toResponse(repository.save(build(new UserStatus(), request))); }
    @Transactional public UserStatusResponse update(Long id, UserStatusRequest request) { return toResponse(repository.save(build(find(id), request))); }
    @Transactional public void delete(Long id) { UserStatus status = find(id); status.setValue("ELIMINADO"); status.setDescription("Registro eliminado logicamente."); repository.save(status); }
    @Transactional(readOnly = true) public UserStatus required(String value) { return repository.findByValue(value).orElseThrow(() -> new IllegalArgumentException("El estatus " + value + " no esta disponible.")); }
    private UserStatus find(Long id) { return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Estatus no encontrado.")); }
    private UserStatus build(UserStatus target, UserStatusRequest request) { target.setValue(request.value().trim().toUpperCase(Locale.ROOT)); target.setDescription(request.description().trim()); target.setActualizado(Instant.now()); return target; }
    private UserStatusResponse toResponse(UserStatus value) { return new UserStatusResponse(value.getId(), value.getValue(), value.getDescription(), value.getActualizado().toString()); }
}
