package com.taller.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.web.multipart.MultipartFile;

public class ClientRegistrationRequest {
    private static final String PHONE_PATTERN = "^[0-9+() -]{7,25}$";

    @NotBlank @Size(max = 160) private String fullName;
    @NotBlank @Size(max = 160) private String alternateContactName;
    @NotNull @Past private LocalDate birthDate;
    @NotBlank @Pattern(regexp = PHONE_PATTERN, message = "El telefono personal tiene un formato invalido") private String personalPhone;
    @NotBlank @Pattern(regexp = PHONE_PATTERN, message = "El telefono de trabajo tiene un formato invalido") private String workPhone;
    @NotBlank @Email @Size(max = 160) private String personalEmail;
    @NotBlank @Email @Size(max = 160) private String workEmail;
    @NotNull private MultipartFile photo;
    @NotBlank @Size(max = 160) private String street;
    @NotBlank @Size(max = 120) private String neighborhood;
    @NotBlank @Size(max = 120) private String municipality;
    @NotBlank @Size(max = 120) private String state;
    @NotBlank @Pattern(regexp = "^\\d{5}$", message = "El codigo postal debe tener 5 digitos") private String postalCode;
    @NotNull(message = "Selecciona el taller al que pertenece el cliente") private Long workshopId;

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getAlternateContactName() { return alternateContactName; }
    public void setAlternateContactName(String alternateContactName) { this.alternateContactName = alternateContactName; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public String getPersonalPhone() { return personalPhone; }
    public void setPersonalPhone(String personalPhone) { this.personalPhone = personalPhone; }
    public String getWorkPhone() { return workPhone; }
    public void setWorkPhone(String workPhone) { this.workPhone = workPhone; }
    public String getPersonalEmail() { return personalEmail; }
    public void setPersonalEmail(String personalEmail) { this.personalEmail = personalEmail; }
    public String getWorkEmail() { return workEmail; }
    public void setWorkEmail(String workEmail) { this.workEmail = workEmail; }
    public MultipartFile getPhoto() { return photo; }
    public void setPhoto(MultipartFile photo) { this.photo = photo; }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getNeighborhood() { return neighborhood; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }
    public String getMunicipality() { return municipality; }
    public void setMunicipality(String municipality) { this.municipality = municipality; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public Long getWorkshopId() { return workshopId; }
    public void setWorkshopId(Long workshopId) { this.workshopId = workshopId; }
}
