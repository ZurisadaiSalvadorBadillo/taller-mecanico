package com.taller.auth.service;
import com.taller.auth.dto.VehicleRequest;
import com.taller.auth.dto.VehicleResponse;
import com.taller.auth.model.Client;
import com.taller.auth.model.Vehicle;
import com.taller.auth.repository.ClientRepository;
import com.taller.auth.repository.VehicleRepository;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service public class VehicleService {
    private final VehicleRepository vehicles; private final ClientRepository clients;
    public VehicleService(VehicleRepository vehicles, ClientRepository clients) { this.vehicles = vehicles; this.clients = clients; }
    @Transactional public VehicleResponse create(Long clientId, VehicleRequest request) {
        Client client = clients.findById(clientId).orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado."));
        if (!"ACTIVO".equals(client.getStatus())) throw new IllegalArgumentException("Solo se pueden asociar vehiculos a clientes activos.");
        String vin = request.vin().trim().toUpperCase(Locale.ROOT), plate = request.plate().trim().toUpperCase(Locale.ROOT);
        if (vehicles.existsByVin(vin) || vehicles.existsByPlate(plate)) throw new IllegalArgumentException("El VIN o la placa ya estan registrados.");
        Vehicle vehicle = new Vehicle(); vehicle.setClient(client); vehicle.setVin(vin); vehicle.setPlate(plate); vehicle.setBrand(request.brand().trim()); vehicle.setModel(request.model().trim()); vehicle.setYear(request.year()); vehicle.setVersion(request.version().trim()); vehicle.setMileage(request.mileage()); vehicle.setSerialNumber(request.serialNumber() == null ? null : request.serialNumber().trim());
        return toResponse(vehicles.save(vehicle));
    }
    @Transactional(readOnly = true) public List<VehicleResponse> list(Long clientId) { return vehicles.findByClientIdOrderByBrandAscModelAsc(clientId).stream().map(this::toResponse).toList(); }
    private VehicleResponse toResponse(Vehicle v) { return new VehicleResponse(v.getId(), v.getVin(), v.getPlate(), v.getBrand(), v.getModel(), v.getYear(), v.getVersion(), v.getMileage(), v.getSerialNumber()); }
}
