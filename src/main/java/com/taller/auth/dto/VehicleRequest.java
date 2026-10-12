package com.taller.auth.dto;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
public record VehicleRequest(
        @NotBlank @Pattern(regexp = "^[A-HJ-NPR-Z0-9]{17}$", message = "El VIN debe tener 17 caracteres validos") String vin,
        @NotBlank @Pattern(regexp = "^[A-Z0-9-]{5,15}$", message = "La placa no tiene un formato valido") String plate,
        @NotBlank @Size(max = 80) String brand, @NotBlank @Size(max = 100) String model,
        @Min(1886) @Max(2100) Integer year, @NotBlank @Size(max = 100) String version,
        @Min(0) Integer mileage, @Size(max = 80) String serialNumber) { }
