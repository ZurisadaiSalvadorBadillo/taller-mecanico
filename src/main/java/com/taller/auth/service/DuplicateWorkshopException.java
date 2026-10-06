package com.taller.auth.service;

public class DuplicateWorkshopException extends RuntimeException {
    public DuplicateWorkshopException() {
        super("Ya existe un taller con el mismo nombre, RFC o correo.");
    }
}
