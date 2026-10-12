package com.taller.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "clientes")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String fullName;

    @Column(nullable = false, length = 160)
    private String alternateContactName;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false, unique = true, length = 25)
    private String personalPhone;

    @Column(nullable = false, unique = true, length = 25)
    private String workPhone;

    @Column(nullable = false, unique = true, length = 160)
    private String personalEmail;

    @Column(nullable = false, unique = true, length = 160)
    private String workEmail;

    @Column(nullable = false, length = 255)
    private String photoPath;

    @Column(nullable = false, length = 120)
    private String photoContentType;

    @Column(nullable = false, length = 160)
    private String street;

    @Column(nullable = false, length = 120)
    private String neighborhood;

    @Column(nullable = false, length = 120)
    private String municipality;

    @Column(nullable = false, length = 120)
    private String state;

    @Column(nullable = false, length = 10)
    private String postalCode;

    @Column(nullable = false, length = 80) private String firstName;
    @Column(nullable = false, length = 80) private String paternalSurname;
    @Column(nullable = false, length = 80) private String maternalSurname;
    @Column(nullable = false, unique = true, length = 18) private String curp;
    @Column(nullable = false, unique = true, length = 13) private String rfc;
    @Column(nullable = false) private Integer age;
    @Column(nullable = false, length = 160) private String contactEmail;
    @Column(nullable = false, length = 25) private String contactMobile;
    @Column(nullable = false, length = 25) private String personalMobile;
    @Column(nullable = false, length = 120) private String locality;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "taller_id", nullable = false)
    private Workshop workshop;

    @Column(nullable = false, length = 20)
    private String status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "status_usuario_id", nullable = false)
    private UserStatus userStatus;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getAlternateContactName() { return alternateContactName; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getPersonalPhone() { return personalPhone; }
    public String getWorkPhone() { return workPhone; }
    public String getPersonalEmail() { return personalEmail; }
    public String getWorkEmail() { return workEmail; }
    public String getPhotoPath() { return photoPath; }
    public String getPhotoContentType() { return photoContentType; }
    public String getStreet() { return street; }
    public String getNeighborhood() { return neighborhood; }
    public String getMunicipality() { return municipality; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
    public String getFirstName() { return firstName; }
    public String getPaternalSurname() { return paternalSurname; }
    public String getMaternalSurname() { return maternalSurname; }
    public String getCurp() { return curp; }
    public String getRfc() { return rfc; }
    public Integer getAge() { return age; }
    public String getContactEmail() { return contactEmail; }
    public String getContactMobile() { return contactMobile; }
    public String getPersonalMobile() { return personalMobile; }
    public String getLocality() { return locality; }
    public Workshop getWorkshop() { return workshop; }
    public String getStatus() { return status; }
    public UserStatus getUserStatus() { return userStatus; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setAlternateContactName(String alternateContactName) { this.alternateContactName = alternateContactName; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public void setPersonalPhone(String personalPhone) { this.personalPhone = personalPhone; }
    public void setWorkPhone(String workPhone) { this.workPhone = workPhone; }
    public void setPersonalEmail(String personalEmail) { this.personalEmail = personalEmail; }
    public void setWorkEmail(String workEmail) { this.workEmail = workEmail; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
    public void setPhotoContentType(String photoContentType) { this.photoContentType = photoContentType; }
    public void setStreet(String street) { this.street = street; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }
    public void setMunicipality(String municipality) { this.municipality = municipality; }
    public void setState(String state) { this.state = state; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setPaternalSurname(String paternalSurname) { this.paternalSurname = paternalSurname; }
    public void setMaternalSurname(String maternalSurname) { this.maternalSurname = maternalSurname; }
    public void setCurp(String curp) { this.curp = curp; }
    public void setRfc(String rfc) { this.rfc = rfc; }
    public void setAge(Integer age) { this.age = age; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public void setContactMobile(String contactMobile) { this.contactMobile = contactMobile; }
    public void setPersonalMobile(String personalMobile) { this.personalMobile = personalMobile; }
    public void setLocality(String locality) { this.locality = locality; }
    public void setWorkshop(Workshop workshop) { this.workshop = workshop; }
    public void setStatus(String status) { this.status = status; }
    public void setUserStatus(UserStatus userStatus) { this.userStatus = userStatus; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
