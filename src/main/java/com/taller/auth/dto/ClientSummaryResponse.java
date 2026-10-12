package com.taller.auth.dto;

public record ClientSummaryResponse(Long id, String firstName, String paternalSurname, String maternalSurname,
        String fullName, String curp, String rfc, String birthDate, Integer age, String contactEmail,
        String contactMobile, String personalMobile, String workPhone, String street, String locality,
        String municipality, String state, String postalCode, Long workshopId, String workshopName,
        String status, String photoUrl) { }
