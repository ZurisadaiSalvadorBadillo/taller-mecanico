package com.taller.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "talleres")
public class Workshop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 160)
    private String name;

    @Column(nullable = false, length = 120)
    private String state;

    @Column(nullable = false, length = 10)
    private String postalCode;

    @Column(nullable = false, length = 120)
    private String municipality;

    @Column(nullable = false, length = 120)
    private String neighborhood;

    @Column(nullable = false, length = 120)
    private String locality;

    @Column(nullable = false, length = 160)
    private String street;

    @Column(nullable = false, length = 180)
    private String businessName;

    @Column(nullable = false, length = 25)
    private String phone;

    @Column(nullable = false, unique = true, length = 13)
    private String rfc;

    @Column(nullable = false, unique = true, length = 160)
    private String email;

    @Column(nullable = false, length = 255)
    private String photoPath;

    @Column(nullable = false, length = 120)
    private String photoContentType;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
    public String getMunicipality() { return municipality; }
    public String getNeighborhood() { return neighborhood; }
    public String getLocality() { return locality; }
    public String getStreet() { return street; }
    public String getBusinessName() { return businessName; }
    public String getPhone() { return phone; }
    public String getRfc() { return rfc; }
    public String getEmail() { return email; }
    public String getPhotoPath() { return photoPath; }
    public String getPhotoContentType() { return photoContentType; }
    public Instant getCreatedAt() { return createdAt; }
    public void setName(String name) { this.name = name; }
    public void setState(String state) { this.state = state; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public void setMunicipality(String municipality) { this.municipality = municipality; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }
    public void setLocality(String locality) { this.locality = locality; }
    public void setStreet(String street) { this.street = street; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setRfc(String rfc) { this.rfc = rfc; }
    public void setEmail(String email) { this.email = email; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
    public void setPhotoContentType(String photoContentType) { this.photoContentType = photoContentType; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
