#!/bin/bash

echo "Iniciando el Taller Mecánico..."

if [ -z "${DB_PASSWORD:-}" ]; then
  read -rsp "Contrasena de MariaDB: " DB_PASSWORD
  echo
fi

if [ -z "${JWT_SECRET:-}" ]; then
  read -rsp "Secreto JWT (minimo 32 caracteres): " JWT_SECRET
  echo
fi

export DB_PASSWORD JWT_SECRET

# 1. Iniciar el Backend
echo "Levantando el backend..."
(
  cd backend
  mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
) &

# Guardamos el PID del backend
BACKEND_PID=$!

# 2. Iniciar el Frontend
echo "Levantando el frontend..."
(
  cd frontend
  VITE_API_URL=http://localhost:8081/api npm run dev -- --host 0.0.0.0 --port 5174
) &
FRONTEND_PID=$!

echo "¡Listo! Los servicios se están ejecutando."
echo "Frontend: http://localhost:5174/"
echo "Backend: http://localhost:8081/"
echo "Presiona Ctrl+C en cualquier momento para detener los procesos."

# Esperar a que el usuario cierre el script
wait
