package com.taller.auth.repository;
import com.taller.auth.model.Vehicle;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface VehicleRepository extends JpaRepository<Vehicle, Long> { List<Vehicle> findByClientIdOrderByBrandAscModelAsc(Long clientId); boolean existsByVin(String vin); boolean existsByPlate(String plate); }
