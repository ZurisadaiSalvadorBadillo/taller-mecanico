CREATE DATABASE IF NOT EXISTS taller_mecanico
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE taller_mecanico;

CREATE TABLE IF NOT EXISTS usuarios (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(120) NOT NULL,
  email VARCHAR(160) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  role ENUM('GERENTE', 'MECANICO', 'CLIENTE') NOT NULL,
  enabled BOOLEAN NOT NULL DEFAULT TRUE,
  reset_code_hash VARCHAR(255) NULL,
  reset_code_expires_at DATETIME(6) NULL,
  password_changed_at DATETIME(6) NULL,
  PRIMARY KEY (id)
);

ALTER TABLE usuarios
  MODIFY role ENUM('GERENTE', 'RECEPCIONISTA', 'MECANICO', 'CLIENTE') NOT NULL;

CREATE TABLE IF NOT EXISTS clientes (
  id BIGINT NOT NULL AUTO_INCREMENT,
  full_name VARCHAR(160) NOT NULL,
  alternate_contact_name VARCHAR(160) NOT NULL,
  birth_date DATE NOT NULL,
  personal_phone VARCHAR(25) NOT NULL UNIQUE,
  work_phone VARCHAR(25) NOT NULL UNIQUE,
  personal_email VARCHAR(160) NOT NULL UNIQUE,
  work_email VARCHAR(160) NOT NULL UNIQUE,
  photo_path VARCHAR(255) NOT NULL,
  photo_content_type VARCHAR(120) NOT NULL,
  street VARCHAR(160) NOT NULL,
  neighborhood VARCHAR(120) NOT NULL,
  municipality VARCHAR(120) NOT NULL,
  state VARCHAR(120) NOT NULL,
  postal_code VARCHAR(10) NOT NULL,
  created_at DATETIME(6) NOT NULL,
  PRIMARY KEY (id)
);
