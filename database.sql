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
