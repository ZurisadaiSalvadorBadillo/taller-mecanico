package com.taller.auth.dto;

public record WorkshopSummaryResponse(Long id, String name, String businessName, String phone, String email,
        String rfc, String street, String neighborhood, String locality, String municipality, String state,
        String postalCode) { }
