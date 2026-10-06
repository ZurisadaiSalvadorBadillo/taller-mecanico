package com.taller.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public class WorkshopRegistrationRequest {
    private static final String PHONE_PATTERN = "^[0-9+() -]{7,25}$";
    private static final String RFC_PATTERN = "^[A-Z&Ñ]{3,4}[0-9]{2}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])[A-Z0-9]{3}$";

    @NotBlank @Size(max = 160) private String name;
    @NotBlank @Size(max = 120) private String state;
    @NotBlank @Pattern(regexp = "^\\d{5}$", message = "El codigo postal debe tener 5 digitos") private String postalCode;
    @NotBlank @Size(max = 120) private String municipality;
    @NotBlank @Size(max = 120) private String neighborhood;
    @NotBlank @Size(max = 160) private String street;
    @NotBlank @Size(max = 180) private String businessName;
    @NotBlank @Pattern(regexp = PHONE_PATTERN, message = "El telefono tiene un formato invalido") private String phone;
    @NotBlank @Pattern(regexp = RFC_PATTERN, message = "El RFC no tiene un formato valido") private String rfc;
    @NotBlank @Email @Size(max = 160) private String email;
    @NotNull private MultipartFile photo;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getMunicipality() { return municipality; }
    public void setMunicipality(String municipality) { this.municipality = municipality; }
    public String getNeighborhood() { return neighborhood; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getRfc() { return rfc; }
    public void setRfc(String rfc) { this.rfc = rfc; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public MultipartFile getPhoto() { return photo; }
    public void setPhoto(MultipartFile photo) { this.photo = photo; }
}
