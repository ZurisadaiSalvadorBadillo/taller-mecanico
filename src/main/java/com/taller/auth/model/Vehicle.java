package com.taller.auth.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "vehiculos")
public class Vehicle {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "cliente_id", nullable = false)
    private Client client;
    @Column(nullable = false, unique = true, length = 17) private String vin;
    @Column(nullable = false, unique = true, length = 15) private String plate;
    @Column(nullable = false, length = 80) private String brand;
    @Column(nullable = false, length = 100) private String model;
    @Column(nullable = false) private Integer year;
    @Column(nullable = false, length = 100) private String version;
    private Integer mileage;
    @Column(length = 80) private String serialNumber;
    public Long getId() { return id; }
    public Client getClient() { return client; }
    public String getVin() { return vin; }
    public String getPlate() { return plate; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public Integer getYear() { return year; }
    public String getVersion() { return version; }
    public Integer getMileage() { return mileage; }
    public String getSerialNumber() { return serialNumber; }
    public void setClient(Client client) { this.client = client; }
    public void setVin(String vin) { this.vin = vin; }
    public void setPlate(String plate) { this.plate = plate; }
    public void setBrand(String brand) { this.brand = brand; }
    public void setModel(String model) { this.model = model; }
    public void setYear(Integer year) { this.year = year; }
    public void setVersion(String version) { this.version = version; }
    public void setMileage(Integer mileage) { this.mileage = mileage; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
}
