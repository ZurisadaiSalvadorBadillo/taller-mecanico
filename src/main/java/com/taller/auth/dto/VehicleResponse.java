package com.taller.auth.dto;
public record VehicleResponse(Long id, String vin, String plate, String brand, String model, Integer year, String version, Integer mileage, String serialNumber) { }
