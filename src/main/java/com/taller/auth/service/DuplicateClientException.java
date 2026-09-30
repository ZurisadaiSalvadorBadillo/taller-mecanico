package com.taller.auth.service;

public class DuplicateClientException extends RuntimeException {
    public DuplicateClientException() {
        super("Ya existe un cliente con alguno de los correos o telefonos proporcionados.");
    }
}
