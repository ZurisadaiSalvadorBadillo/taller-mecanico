# Planeacion de la fase inicial

## Sistema de Taller Mecanico

**Version:** 1.0

**Estado del documento:** Base tecnica de la primera fase

**Alcance:** Fundamentos de acceso seguro, persistencia de usuarios y cliente web inicial.

## 1. Objetivo de la fase

La primera fase establece la base tecnica del Sistema de Taller Mecanico. Su objetivo es permitir el acceso de usuarios mediante registro, inicio de sesion, recuperacion de contrasena y autorizacion por roles, con persistencia en una base de datos relacional. Tambien se incluye una interfaz web inicial para consumir dichos flujos y presentar el espacio de trabajo posterior al acceso.

Esta fase no implementa todavia la operacion completa de un taller: las ordenes de reparacion, vehiculos, inventario, agenda, notificaciones y reportes se muestran como opciones de interfaz y alcance futuro, pero no cuentan con entidades, repositorios ni servicios operativos en el backend.

## 2. Modulos desarrollados y estado

| Modulo | Estado | Datos y entidades manejados | Descripcion tecnica |
| --- | --- | --- | --- |
| Registro de clientes | Terminado a nivel de codigo fuente | Solicitud publica: nombre, correo y contrasena. Entidad `User`/tabla `usuarios`; rol `CLIENTE`. | Expone `POST /api/auth/register`. Valida los datos de entrada y la unicidad del correo. El registro publico asigna siempre el rol `CLIENTE`; no permite que un visitante se otorgue privilegios superiores. La contrasena se almacena mediante BCrypt. |
| Inicio de sesion | Terminado a nivel de codigo fuente | Correo, contrasena, identificador, nombre y rol del usuario; token JWT de respuesta. | Expone `POST /api/auth/login`. Spring Security autentica las credenciales contra la persistencia y, si son validas, el servicio genera un JWT firmado que contiene el correo como sujeto, el rol, el nombre y la fecha de expiracion. |
| Recuperacion y restablecimiento de contrasena | Terminado para entorno local | Correo, codigo temporal de seis digitos, fecha de expiracion, hash del codigo y nueva contrasena. Campos `resetCodeHash`, `resetCodeExpiresAt` y `passwordChangedAt` de `usuarios`. | Expone `POST /api/auth/forgot-password` y `POST /api/auth/reset-password`. Genera un codigo criptograficamente aleatorio, conserva solo su hash BCrypt y lo invalida despues de 15 minutos o tras usarlo. |
| Autorizacion basada en roles | Terminado a nivel de codigo fuente | Enumeracion `Role`: `GERENTE`, `MECANICO` y `CLIENTE`; autoridades `ROLE_*` de Spring Security. | Define reglas centralizadas para limitar los recursos de gerente, mecanico y cliente. El gerente puede crear usuarios internos; mecanicos y clientes solo acceden a las rutas habilitadas para su rol. |
| Seguridad de API | Terminado a nivel de codigo fuente | Encabezado `Authorization: Bearer <JWT>`, origen permitido de CORS y secreto de firma JWT. | Usa Spring Security en modo sin sesion, filtro JWT por solicitud, BCrypt con factor 12, validacion de solicitudes y respuestas de error controladas. CORS se limita al origen configurado del frontend. |
| Persistencia y conexion a base de datos | Terminado a nivel de codigo fuente | Base `taller_mecanico` y tabla `usuarios`; entidad JPA `User`; repositorio `UserRepository`. | Spring Data JPA gestiona la persistencia. El script SQL crea el esquema inicial y Hibernate puede actualizar la estructura durante el desarrollo mediante `ddl-auto=update`. |
| Gestion interna de usuarios | Terminado a nivel de codigo fuente | Nombre, correo, contrasena y rol. | Expone `POST /api/users`, protegido para el rol `GERENTE`. Permite registrar cuentas de gerente, mecanico o cliente desde una sesion autorizada. |
| Cliente web de acceso | Terminado como interfaz de fase inicial | Formulario de registro, inicio de sesion, recuperacion, token y datos de sesion en almacenamiento local del navegador. | Implementado con Vue, Vite, Axios y Bootstrap. Consume la API REST y conserva la sesion del usuario en `localStorage` para mostrar el panel inicial. |
| Panel inicial y catalogo de opciones | Terminado como prototipo visual | Datos de sesion y datos demostrativos de ordenes y contadores. | Presenta opciones futuras de ordenes, vehiculos, usuarios, reportes, inventario, agenda y notificaciones. Los valores de ordenes y contadores son estaticos; no representan una gestion real persistida. |

### 2.1 Entidad persistida en esta fase

La unica entidad de dominio persistida es `User`, mapeada a la tabla `usuarios`.

| Campo | Tipo de base de datos | Proposito |
| --- | --- | --- |
| `id` | `BIGINT` | Identificador autoincremental del usuario. |
| `name` | `VARCHAR(120)` | Nombre de la persona usuaria. |
| `email` | `VARCHAR(160)` unico | Identificador de acceso y dato de contacto. |
| `password_hash` | `VARCHAR(255)` | Hash BCrypt de la contrasena; no se almacena la contrasena en texto plano. |
| `role` | `ENUM` | Rol `GERENTE`, `MECANICO` o `CLIENTE`. |
| `enabled` | `BOOLEAN` | Estado de habilitacion de la cuenta. |
| `reset_code_hash` | `VARCHAR(255)` nullable | Hash del codigo de recuperacion. |
| `reset_code_expires_at` | `DATETIME(6)` nullable | Vigencia del codigo de recuperacion. |
| `password_changed_at` | `DATETIME(6)` nullable | Fecha del ultimo cambio de contrasena. |

No existen aun entidades persistidas para orden de reparacion, cliente como perfil independiente, vehiculo, diagnostico, mano de obra, refaccion, cita, proveedor, inventario, pago o reporte. Estas entidades forman parte de las siguientes fases funcionales.

## 3. Arquitectura del backend

El backend se encuentra en `backend/` y usa Spring Boot 3.3.5 con Java 21. Sigue una arquitectura por capas, organizada bajo el paquete base `com.taller.auth`.

```text
backend/src/main/java/com/taller/auth/
├── AuthApplication.java        Punto de entrada de Spring Boot
├── config/                    Configuracion de seguridad y datos de demostracion
├── controller/                Recursos HTTP REST y manejo de errores
├── dto/                       Contratos de entrada y salida de la API
├── model/                     Entidades JPA y enumeraciones de dominio
├── repository/                Acceso a datos con Spring Data JPA
├── security/                  UserDetails, JWT y filtro de autenticacion
└── service/                   Reglas de negocio de autenticacion y usuarios
```

### 3.1 Capa de controladores

- `AuthController` publica el registro, inicio de sesion, recuperacion y restablecimiento de contrasena bajo `/api/auth`.
- `UserController` publica la creacion interna de usuarios en `/api/users`; la autorizacion corresponde al rol `GERENTE`.
- `DashboardController` contiene rutas de comprobacion de permisos para los tres perfiles. Sus respuestas son informativas y no implementan aun consultas reales de ordenes.
- `ApiExceptionHandler` transforma errores de autenticacion, validacion y reglas de negocio en respuestas HTTP consistentes.

### 3.2 Capa de servicios

- `AuthService` realiza la autenticacion delegando en `AuthenticationManager`, emite JWT, crea codigos temporales y restablece contrasenas.
- `UserService` valida que el correo no exista, cifra contrasenas y registra usuarios. Distingue el registro publico de clientes de la alta interna realizada por el gerente.

### 3.3 Persistencia

- `User` representa la cuenta de acceso y se relaciona con la tabla `usuarios`.
- `Role` es la enumeracion de los perfiles de autorizacion.
- `UserRepository` extiende `JpaRepository<User, Long>` e incorpora busqueda y validacion de existencia por correo.
- `DataSeeder` inserta, si no existen, las cuentas demostrativas de gerente, mecanico y cliente al iniciar la aplicacion.

### 3.4 Seguridad, JWT y Spring Security

`SecurityConfig` define una cadena de filtros sin estado (`STATELESS`). Las rutas `/api/auth/**` son publicas. El resto de las rutas requiere JWT valido y se controla mediante las siguientes reglas:

| Recurso | Roles autorizados |
| --- | --- |
| `POST /api/users` | `GERENTE` |
| `/api/manager/**` | `GERENTE` |
| `/api/mechanic/**` | `GERENTE`, `MECANICO` |
| `/api/client/**` | `GERENTE`, `CLIENTE` |
| Cualquier otra ruta no publica | Usuario autenticado |

`JwtService` firma los tokens con una clave HMAC configurada, incorpora el correo, nombre y rol, y aplica la vigencia indicada en `app.jwt.expiration-minutes`. `JwtAuthenticationFilter` inspecciona el encabezado Bearer de cada solicitud, valida el token y carga las autoridades del usuario en el contexto de seguridad de Spring.

Las contrasenas y codigos de recuperacion se procesan con `BCryptPasswordEncoder` con factor 12. La API no maneja sesiones de servidor ni almacena contrasenas en texto plano.

### 3.5 Endpoints disponibles

| Metodo | Ruta | Finalidad | Acceso |
| --- | --- | --- | --- |
| `POST` | `/api/auth/register` | Registra un cliente nuevo. | Publico |
| `POST` | `/api/auth/login` | Valida credenciales y emite JWT. | Publico |
| `POST` | `/api/auth/forgot-password` | Genera un codigo temporal de recuperacion. | Publico |
| `POST` | `/api/auth/reset-password` | Valida codigo y cambia la contrasena. | Publico |
| `POST` | `/api/users` | Crea un usuario interno con rol seleccionado. | Gerente |
| `GET` | `/api/manager/dashboard` | Comprueba acceso de gerente. | Gerente |
| `GET` | `/api/mechanic/orders` | Comprueba acceso de mecanico. | Gerente o mecanico |
| `GET` | `/api/client/status` | Comprueba acceso de cliente. | Gerente o cliente |

## 4. Configuracion de base de datos

### 4.1 Ubicacion exacta de las credenciales

Las credenciales de conexion se encuentran en el archivo:

`/home/zurisadai/Documentos/ChatGPT/Taller mecanico/backend/src/main/resources/application.properties`

Las propiedades que forman la configuracion de conexion son:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/taller_mecanico?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=<contrasena-configurada-en-el-archivo>
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

El archivo de estructura inicial de la base de datos es:

`/home/zurisadai/Documentos/ChatGPT/Taller mecanico/backend/database.sql`

El controlador `mysql-connector-j` es compatible con MySQL y, para este esquema, con MariaDB. La configuracion actual usa `spring.jpa.hibernate.ddl-auto=update`, adecuada para desarrollo. En produccion se recomienda aplicar el script o una herramienta de migraciones y cambiar esta propiedad a `validate`.

**Nota de seguridad:** la contrasena actual esta escrita en el archivo de propiedades y no debe permanecer en el control de versiones ni reutilizarse en produccion. Debe sustituirse por una cuenta de aplicacion de privilegios limitados y una configuracion externa protegida.

## 5. Guia de publicacion

### 5.1 Software recomendado

| Componente | Recomendacion |
| --- | --- |
| Sistema operativo | Ubuntu Server 24.04 LTS o Debian 12. |
| Runtime backend | OpenJDK 21 LTS o Eclipse Temurin 21. |
| Compilacion backend | Apache Maven 3.9 o superior. |
| Base de datos | MariaDB 10.11 LTS o MySQL 8.0 o superior. |
| Runtime y compilacion frontend | Node.js 20 LTS o 22 LTS y npm. |
| Servidor web y proxy inverso | Nginx. |
| Servicio de aplicacion | systemd. |
| HTTPS | Certbot con el modulo de Nginx y un dominio publico. |

### 5.2 Preparacion del servidor

En un servidor Debian o Ubuntu, instalar las dependencias principales:

```bash
sudo apt update
sudo apt install -y openjdk-21-jdk maven mariadb-server nginx nodejs npm
```

Comprobar las versiones antes de publicar:

```bash
java -version
mvn -version
node --version
npm --version
nginx -v
```

### 5.3 Creacion de la base de datos productiva

Crear una cuenta exclusiva para la aplicacion; no utilizar `root` desde el backend:

```sql
CREATE DATABASE taller_mecanico CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'taller_app'@'localhost' IDENTIFIED BY 'CAMBIAR_POR_UN_SECRETO_FUERTE';
GRANT SELECT, INSERT, UPDATE, DELETE, CREATE, ALTER, INDEX, REFERENCES ON taller_mecanico.* TO 'taller_app'@'localhost';
FLUSH PRIVILEGES;
```

Aplicar el esquema inicial desde la raiz del repositorio:

```bash
mariadb -u taller_app -p taller_mecanico < backend/database.sql
```

Una vez aplicado el esquema, se recomienda definir `spring.jpa.hibernate.ddl-auto=validate` en la configuracion de produccion. Para siguientes cambios estructurales debe incorporarse una herramienta de migraciones, por ejemplo Flyway o Liquibase.

### 5.4 Configuracion externa y publicacion del backend

1. Copiar el backend a una ruta de aplicacion, por ejemplo `/opt/taller-mecanico/backend`.
2. Crear el directorio de configuracion y el usuario de servicio:

```bash
sudo useradd --system --no-create-home --shell /usr/sbin/nologin taller
sudo install -d -o taller -g taller /etc/taller-mecanico
```

3. Crear `/etc/taller-mecanico/application.properties` con valores de produccion. No copiar la contrasena ni el secreto JWT al repositorio:

```properties
server.port=8080
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/taller_mecanico?useSSL=true&serverTimezone=UTC
spring.datasource.username=taller_app
spring.datasource.password=CAMBIAR_POR_UN_SECRETO_FUERTE
spring.jpa.hibernate.ddl-auto=validate
app.jwt.secret=REEMPLAZAR_POR_UN_SECRETO_ALEATORIO_DE_AL_MENOS_32_CARACTERES
app.jwt.expiration-minutes=60
app.frontend.origin=https://taller.ejemplo.com
```

4. Compilar el artefacto ejecutable:

```bash
cd /opt/taller-mecanico/backend
mvn clean package
```

El resultado esperado es `target/auth-0.0.1-SNAPSHOT.jar`.

5. Crear el servicio `/etc/systemd/system/taller-backend.service`:

```ini
[Unit]
Description=Backend del Sistema de Taller Mecanico
After=network.target mariadb.service

[Service]
Type=simple
User=taller
Group=taller
WorkingDirectory=/opt/taller-mecanico/backend
ExecStart=/usr/bin/java -jar /opt/taller-mecanico/backend/target/auth-0.0.1-SNAPSHOT.jar --spring.config.additional-location=file:/etc/taller-mecanico/
SuccessExitStatus=143
Restart=on-failure
RestartSec=5

[Install]
WantedBy=multi-user.target
```

6. Activar y revisar el servicio:

```bash
sudo systemctl daemon-reload
sudo systemctl enable --now taller-backend
sudo systemctl status taller-backend
sudo journalctl -u taller-backend -f
```

El puerto `8080` debe quedar accesible solo desde `localhost`; Nginx sera el punto publico de entrada.

### 5.5 Compilacion y publicacion del frontend

1. En `frontend/`, crear el archivo `.env.production` con una ruta relativa para que navegador, Nginx y API compartan el mismo origen:

```properties
VITE_API_URL=/api
```

2. Instalar dependencias y generar los archivos estaticos:

```bash
cd /opt/taller-mecanico/frontend
npm ci
npm run build
```

3. Publicar el contenido generado:

```bash
sudo install -d -o www-data -g www-data /var/www/taller-mecanico
sudo cp -r dist/. /var/www/taller-mecanico/
```

### 5.6 Configuracion de Nginx

Crear `/etc/nginx/sites-available/taller-mecanico` y reemplazar `taller.ejemplo.com` por el dominio real:

```nginx
server {
    listen 80;
    server_name taller.ejemplo.com;

    root /var/www/taller-mecanico;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Habilitar la configuracion y comprobar su sintaxis:

```bash
sudo ln -s /etc/nginx/sites-available/taller-mecanico /etc/nginx/sites-enabled/taller-mecanico
sudo nginx -t
sudo systemctl reload nginx
```

Para habilitar HTTPS con un dominio que ya apunte al servidor:

```bash
sudo apt install -y certbot python3-certbot-nginx
sudo certbot --nginx -d taller.ejemplo.com
```

Al usar `/api` como URL del frontend, la aplicacion funciona en el mismo origen que Nginx. Por ello, `app.frontend.origin` del backend debe contener exactamente la URL HTTPS publica, sin una barra final.

## 6. Condiciones pendientes antes de produccion

Los siguientes puntos no forman parte funcional terminada de esta primera fase y deben atenderse antes de una publicacion real:

- El codigo de recuperacion se devuelve en la respuesta de desarrollo y se muestra en pantalla. En produccion debe eliminarse `developmentResetCode` de la respuesta y enviarse el codigo por correo, SMS o un proveedor de mensajeria verificado.
- `frontend/src/services/localAuth.js` es un respaldo de demostracion para usar la interfaz sin backend. Almacena datos en `localStorage` y aplica una codificacion reversible, por lo que no es un mecanismo de autenticacion seguro. Debe excluirse del flujo de produccion para que toda autenticacion se realice contra la API.
- La clave JWT y la contrasena de la base de datos deben administrarse fuera del repositorio mediante secretos del sistema, un gestor de secretos o variables de entorno protegidas.
- Aun no existen segundo factor, autenticacion biometrica, rotacion obligatoria semanal de contrasena, limite de intentos, bloqueo temporal, auditoria de accesos, envio real de notificaciones ni politicas de respaldo y restauracion.
- Deben implementarse pruebas automatizadas de integracion para registro, inicio de sesion, expiracion de JWT, recuperacion de contrasena y restriccion por rol.
- Deben desarrollarse las entidades y reglas de negocio de ordenes, vehiculos y demas modulos operativos antes de considerar terminada la plataforma de gestion de taller.

## 7. Criterio de cierre de la fase inicial

La fase inicial se considera terminada a nivel de codigo fuente cuando se dispone de una API Spring Boot con persistencia de cuentas, autenticacion JWT, cifrado BCrypt, recuperacion temporal de contrasena y autorizacion por roles, junto con un frontend Vue que permite registro e inicio de sesion. La puesta en produccion queda condicionada a aplicar la configuracion externa, retirar los mecanismos de demostracion y ejecutar pruebas de integracion y seguridad en el entorno objetivo.
