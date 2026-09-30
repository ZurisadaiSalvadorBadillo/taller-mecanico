package com.taller.auth.dto;

public record ClientSummaryResponse(
        Long id,
        String fullName,
        String alternateContactName,
        String birthDate,
        String personalPhone,
        String workPhone,
        String personalEmail,
        String workEmail,
        String street,
        String neighborhood,
        String municipality,
        String state,
        String postalCode,
        String photoUrl
) {
}
