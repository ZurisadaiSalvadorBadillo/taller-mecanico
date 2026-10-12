package com.taller.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.web.multipart.MultipartFile;

public class ClientRegistrationRequest {
    public static final String PHONE = "^[0-9+() -]{7,25}$";
    public static final String CURP = "^[A-Z]{4}[0-9]{6}[HM][A-Z]{5}[A-Z0-9][0-9]$";
    public static final String RFC = "^[A-Z&Ñ]{3,4}[0-9]{2}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])[A-Z0-9]{3}$";
    @NotBlank @Size(max = 80) private String firstName;
    @NotBlank @Size(max = 80) private String paternalSurname;
    @NotBlank @Size(max = 80) private String maternalSurname;
    @NotBlank @Pattern(regexp = CURP, message = "La CURP no tiene un formato valido") private String curp;
    @NotBlank @Pattern(regexp = RFC, message = "El RFC no tiene un formato valido") private String rfc;
    @NotNull @Past private LocalDate birthDate;
    @NotNull @Min(0) @Max(130) private Integer age;
    @NotBlank @Email @Size(max = 160) private String contactEmail;
    @NotBlank @Pattern(regexp = PHONE, message = "El telefono de contacto tiene un formato invalido") private String contactMobile;
    @NotBlank @Pattern(regexp = PHONE, message = "El telefono personal tiene un formato invalido") private String personalMobile;
    @NotBlank @Pattern(regexp = PHONE, message = "El telefono de trabajo tiene un formato invalido") private String workPhone;
    @NotNull private MultipartFile photo;
    @NotBlank @Size(max = 160) private String street;
    @NotBlank @Size(max = 120) private String locality;
    @NotBlank @Size(max = 120) private String municipality;
    @NotBlank @Size(max = 120) private String state;
    @NotBlank @Pattern(regexp = "^\\d{5}$", message = "El codigo postal debe tener 5 digitos") private String postalCode;
    @NotNull private Long workshopId;
    public String getFirstName() { return firstName; } public void setFirstName(String value) { firstName = value; }
    public String getPaternalSurname() { return paternalSurname; } public void setPaternalSurname(String value) { paternalSurname = value; }
    public String getMaternalSurname() { return maternalSurname; } public void setMaternalSurname(String value) { maternalSurname = value; }
    public String getCurp() { return curp; } public void setCurp(String value) { curp = value; }
    public String getRfc() { return rfc; } public void setRfc(String value) { rfc = value; }
    public LocalDate getBirthDate() { return birthDate; } public void setBirthDate(LocalDate value) { birthDate = value; }
    public Integer getAge() { return age; } public void setAge(Integer value) { age = value; }
    public String getContactEmail() { return contactEmail; } public void setContactEmail(String value) { contactEmail = value; }
    public String getContactMobile() { return contactMobile; } public void setContactMobile(String value) { contactMobile = value; }
    public String getPersonalMobile() { return personalMobile; } public void setPersonalMobile(String value) { personalMobile = value; }
    public String getWorkPhone() { return workPhone; } public void setWorkPhone(String value) { workPhone = value; }
    public MultipartFile getPhoto() { return photo; } public void setPhoto(MultipartFile value) { photo = value; }
    public String getStreet() { return street; } public void setStreet(String value) { street = value; }
    public String getLocality() { return locality; } public void setLocality(String value) { locality = value; }
    public String getMunicipality() { return municipality; } public void setMunicipality(String value) { municipality = value; }
    public String getState() { return state; } public void setState(String value) { state = value; }
    public String getPostalCode() { return postalCode; } public void setPostalCode(String value) { postalCode = value; }
    public Long getWorkshopId() { return workshopId; } public void setWorkshopId(Long value) { workshopId = value; }
}
