# Plataforma de ordenes para taller mecanico

Base inicial para una plataforma de gestion de ordenes de reparacion con Spring Boot, MySQL, Vue y Bootstrap.

## Pilares incluidos

- Seguridad: API REST protegida con Spring Security, CORS limitado al frontend y JWT.
- Autenticacion: login con correo y contrasena, contrasenas cifradas con BCrypt y recuperacion con codigo temporal.
- Autorizacion: roles `GERENTE`, `MECANICO` y `CLIENTE`.

## Base de datos MySQL

1. Crea la base manualmente con:

```sql
SOURCE backend/database.sql;
```

2. Ajusta las credenciales en `backend/src/main/resources/application.properties`:

```properties
spring.datasource.username=root
spring.datasource.password=tu_contrasena
```

Spring Boot tambien puede crear/actualizar la tabla automaticamente por `spring.jpa.hibernate.ddl-auto=update`.

## Ejecutar backend

Se requiere Java 21 y Maven:

```bash
cd backend
mvn spring-boot:run
```

La API queda en:

```text
http://localhost:8080/api
```

Usuarios demo creados al iniciar:

- `gerente@taller.com` / `Gerente123`
- `mecanico@taller.com` / `Mecanico123`
- `cliente@taller.com` / `Cliente123`

## Ejecutar frontend

Se requiere Node.js:

```bash
cd frontend
npm install
npm run dev
```

La aplicacion queda en:

```text
http://localhost:5173
```

## Endpoints principales

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/forgot-password`
- `POST /api/auth/reset-password`
- `POST /api/users` solo para `GERENTE`
- `GET /api/manager/dashboard` solo para `GERENTE`
- `GET /api/mechanic/orders` para `GERENTE` y `MECANICO`
- `GET /api/client/status` para `GERENTE` y `CLIENTE`

Nota: el codigo de recuperacion se devuelve al frontend solo para pruebas locales. En produccion debe enviarse por correo o SMS y nunca mostrarse en pantalla.
