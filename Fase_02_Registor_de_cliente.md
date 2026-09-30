# Fase 02 - Registro de Cliente

## 1. Propósito y alcance

Esta fase implementa el caso de uso **Registro de Cliente** del Sistema de Taller Mecánico. Su objetivo es permitir que personal previamente autenticado registre, valide y consulte clientes desde la plataforma.

El alcance se limita al registro de clientes. El inicio de sesión existente se documenta únicamente como condición de acceso al módulo. No se incorporan módulos de órdenes de reparación, vehículos, sucursales, inventario ni recuperación de contraseña.

## 2. Restricción de acceso

El acceso a las rutas de clientes requiere un JWT válido. La configuración vigente permite las operaciones de registro y consulta exclusivamente a los roles:

| Rol de negocio solicitado | Rol implementado | Permiso en el módulo |
|---|---|---|
| Administrador del sistema | `GERENTE` | Registrar y consultar clientes |
| Recepcionista | `RECEPCIONISTA` | Registrar y consultar clientes |
| Mecánico | `MECANICO` | Sin acceso al registro ni al listado |
| Cliente | `CLIENTE` | Sin acceso al registro ni al listado |

> En la versión actual, `GERENTE` representa el perfil administrativo solicitado. Si se requiere el nombre exacto `ADMINISTRADOR`, deberá agregarse o renombrarse como ajuste de catálogo de roles.

Las reglas se aplican en `SecurityConfig` mediante `hasAnyRole("GERENTE", "RECEPCIONISTA")` para `POST /api/clients` y `GET /api/clients`. El frontend oculta el acceso visual a usuarios sin esos roles, pero la autorización definitiva se aplica en el backend.

## 3. Datos capturados

| Grupo | Campo | Regla aplicada |
|---|---|---|
| Identificación | Nombre completo | Obligatorio; máximo 160 caracteres. |
| Contacto alternativo | Nombre de contacto alternativo | Obligatorio; máximo 160 caracteres. |
| Personales | Fecha de nacimiento | Obligatoria y debe corresponder a una fecha pasada. |
| Contacto | Teléfono personal | Obligatorio; 7 a 25 caracteres numéricos con `+`, paréntesis, espacios o guiones permitidos. |
| Contacto | Teléfono de trabajo | Obligatorio; aplica la misma validación de formato. |
| Contacto | Gmail personal | Obligatorio; formato de correo validado; máximo 160 caracteres. |
| Contacto | Gmail del trabajo | Obligatorio; formato de correo validado; máximo 160 caracteres. |
| Fotografía | Archivo de imagen | Obligatorio; máximo 7 MiB; el backend exige un tipo MIME `image/*` y guarda un nombre aleatorio. |
| Dirección | Calle | Obligatoria; máximo 160 caracteres. |
| Dirección | Colonia | Obligatoria; máximo 120 caracteres. |
| Dirección | Municipio | Obligatorio; máximo 120 caracteres. |
| Dirección | Estado | Obligatorio; máximo 120 caracteres. |
| Dirección | Código postal | Obligatorio; exactamente cinco dígitos. |

Los correos se normalizan a minúsculas y los teléfonos se comparan sin espacios, guiones ni paréntesis para detectar duplicados de forma consistente.

## 4. Arquitectura aplicada

El módulo conserva la arquitectura por capas de Spring Boot y Vue.js:

| Capa | Implementación | Responsabilidad |
|---|---|---|
| Vista | `frontend/src/views/ClientRegistrationForm.vue`, `frontend/src/views/RegisteredClientsView.vue` | Capturar información, validar lo esencial en navegador, mostrar confirmación y listar resultados. |
| Interfaz de aplicación | `frontend/src/services/api.js` | Configurar Axios y adjuntar el JWT almacenado en cada solicitud protegida. |
| Controlador | `ClientController` | Exponer los contratos REST `POST /api/clients` y `GET /api/clients`. |
| Fachada | `ClientRegistrationFacade` | Centralizar registro, normalización, validación de duplicados, almacenamiento de fotografía, persistencia y respuesta. |
| Persistencia | `ClientRepository` | Consultar y guardar la entidad `Client` con Spring Data JPA. |
| Infraestructura | `ClientPhotoStorage` y MariaDB/MySQL | Guardar archivo fotográfico y datos estructurados del cliente. |

### 4.1 Patrón Facade (Fachada)

El patrón **Facade** está implementado en `ClientRegistrationFacade`. `ClientController` no coordina directamente repositorio, archivos ni reglas de duplicidad: delega una sola operación a la fachada. Esto reduce el acoplamiento y ofrece una interfaz de aplicación única para la vista Vue.js mediante los endpoints REST.

En el frontend, el formulario consume la interfaz HTTP centralizada de `api.js`; actualmente la coordinación de `FormData` vive en `ClientRegistrationForm.vue`. Se recomienda extraerla posteriormente a un `clientRegistrationFacade.js` para que la vista quede aún más separada de los detalles de transporte, sin alterar el comportamiento actual.

### 4.2 Patrón Repository

El patrón **Repository** está implementado con `ClientRepository`, que extiende `JpaRepository<Client, Long>`. La fachada utiliza este contrato para comprobar contactos existentes, guardar clientes y obtener el listado ordenado por fecha de creación. Las consultas no se construyen en el controlador ni en la vista.

## 5. Inventario detallado de implementación

### 5.1 Frontend Vue.js

| Archivo o componente | Método, función, estado o evento | Descripción técnica | Estado |
|---|---|---|---|
| `frontend/src/services/api.js` | Instancia `api` | Cliente Axios con URL base definida por `VITE_API_URL` o `http://localhost:8080/api`. | Terminado |
| `frontend/src/services/api.js` | Interceptor de solicitud | Lee `taller_token` de `localStorage` e incluye `Authorization: Bearer <JWT>` cuando existe. | Terminado |
| `frontend/src/App.vue` | Estados `user` e `isAuthenticated` | Conservan el usuario autenticado y determinan si se muestra login o panel. | Terminado |
| `frontend/src/App.vue` | `handleLoggedIn(userData)` | Guarda la sesión de usuario recibida tras el login y abre el panel. | Terminado |
| `frontend/src/App.vue` | `logout()` | Elimina sesión y token locales; vuelve a mostrar la pantalla de inicio de sesión. | Terminado |
| `frontend/src/views/LoginView.vue` | Estados `username`, `email`, `role`, `password`, `confirmPassword`, `accessKey`, `loading`, `error`, `success` | Mantienen el acceso y el registro del personal autorizado. | Terminado |
| `frontend/src/views/LoginView.vue` | `login()` | Envía usuario, contraseña y palabra clave a `POST /auth/login`; al validarse conserva JWT y usuario. | Terminado |
| `frontend/src/views/LoginView.vue` | `register()` | Registra un Gerente o Recepcionista con usuario, correo, palabra clave y confirmación de contraseña. | Terminado |
| `frontend/src/views/DashboardView.vue` | Estado calculado `canRegisterClients` | Verifica que el rol recibido sea `GERENTE` o `RECEPCIONISTA` antes de habilitar la interfaz del módulo. | Terminado |
| `frontend/src/views/DashboardView.vue` | Estado `clientsVersion` | Contador reactivo usado para recargar el listado después de un registro exitoso. | Terminado |
| `frontend/src/views/DashboardView.vue` | Evento `registered` | Al recibirlo desde el formulario incrementa `clientsVersion` y actualiza la lista. | Terminado |
| `frontend/src/views/ClientRegistrationForm.vue` | Estado `form` | Objeto reactivo con los datos de identificación, contactos y dirección. | Terminado |
| `frontend/src/views/ClientRegistrationForm.vue` | Estado `photo` | Conserva el archivo seleccionado antes de enviarlo al backend. | Terminado |
| `frontend/src/views/ClientRegistrationForm.vue` | Estados `loading`, `error` y `success` | Controlan bloqueo temporal, mensajes de validación y confirmación de registro. | Terminado |
| `frontend/src/views/ClientRegistrationForm.vue` | `emptyForm()` | Devuelve la estructura inicial vacía del formulario después de un registro correcto. | Terminado |
| `frontend/src/views/ClientRegistrationForm.vue` | `onPhotoChange(event)` | Obtiene la fotografía seleccionada y rechaza en el navegador archivos mayores a 7 MiB. | Terminado |
| `frontend/src/views/ClientRegistrationForm.vue` | `registerClient()` | Valida la existencia de fotografía, construye `FormData`, ejecuta `POST /clients`, muestra el folio de confirmación, reinicia el formulario y emite `registered`. | Terminado |
| `frontend/src/views/RegisteredClientsView.vue` | Estados `clients`, `loading` y `error` | Conservan listado, estado de carga y errores de consulta. | Terminado |
| `frontend/src/views/RegisteredClientsView.vue` | `loadClients()` | Ejecuta `GET /clients` y actualiza la visualización de clientes autorizada. | Terminado |
| `frontend/src/views/RegisteredClientsView.vue` | Ciclo `onMounted` | Solicita el listado inicial al montar el componente. | Terminado |

### 5.2 Backend Spring Boot

| Archivo o clase | Método, función, contrato o regla | Descripción técnica | Estado |
|---|---|---|---|
| `SecurityConfig.java` | Regla `POST /api/clients` | Exige JWT con rol `GERENTE` o `RECEPCIONISTA` para crear clientes. | Terminado |
| `SecurityConfig.java` | Regla `GET /api/clients` | Exige JWT con rol `GERENTE` o `RECEPCIONISTA` para consultar el listado. | Terminado |
| `ClientController.java` | Constructor | Recibe `ClientRegistrationFacade` por inyección de dependencias. | Terminado |
| `ClientController.java` | `register(ClientRegistrationRequest)` | Atiende `POST /api/clients` en multipart, delega el caso de uso a la fachada y devuelve `201 Created`. | Terminado |
| `ClientController.java` | `list()` | Atiende `GET /api/clients` y devuelve resúmenes de clientes registrados. | Terminado |
| `ClientRegistrationFacade.java` | Constructor | Recibe `ClientRepository` y `ClientPhotoStorage`, dependencias necesarias para el proceso. | Terminado |
| `ClientRegistrationFacade.java` | `register(ClientRegistrationRequest)` | Normaliza contactos, comprueba duplicados, guarda fotografía, crea `Client`, persiste con Repository y devuelve confirmación. Si ocurre un error posterior al guardado de archivo, intenta eliminarlo. | Terminado |
| `ClientRegistrationFacade.java` | `listRegisteredClients()` | Obtiene clientes ordenados por fecha descendente y los transforma a `ClientSummaryResponse`. | Terminado |
| `ClientRegistrationFacade.java` | `normalizeEmail(String)` | Elimina espacios y convierte correo a minúsculas para comparación consistente. | Terminado |
| `ClientRegistrationFacade.java` | `normalizePhone(String)` | Conserva sólo dígitos y el prefijo `+` para comparación consistente de teléfonos. | Terminado |
| `ClientRepository.java` | `findAllByOrderByCreatedAtDesc()` | Consulta los clientes registrados en orden descendente de creación. | Terminado |
| `ClientRepository.java` | `existsWithAnyContact(...)` | Consulta si cualquiera de los dos correos o teléfonos coincide con datos de otro cliente; previene registros duplicados. | Terminado |
| `ClientRepository.java` | `save(Client)` heredado de `JpaRepository` | Persiste la entidad validada en la tabla `clientes`. | Terminado |
| `ClientPhotoStorage.java` | Constante `MAX_PHOTO_SIZE` | Define el límite de 7 MiB para fotografía. | Terminado |
| `ClientPhotoStorage.java` | Constructor | Lee `app.storage.client-photos-dir` para definir la carpeta de fotografías. | Terminado |
| `ClientPhotoStorage.java` | `store(MultipartFile)` | Verifica archivo no vacío, tamaño y MIME `image/*`; crea carpeta, genera nombre UUID y guarda el archivo. | Terminado |
| `ClientPhotoStorage.java` | `deleteQuietly(String)` | Elimina una fotografía almacenada cuando el flujo de registro falla después de guardarla. | Terminado |
| `ClientPhotoStorage.java` | `extensionOf(String)` | Obtiene una extensión segura del nombre original o usa `bin` cuando no es válida. | Terminado |
| `ClientPhotoStorage.java` | Registro `StoredPhoto` | Transporta nombre de archivo y tipo MIME del archivo almacenado. | Terminado |
| `Client.java` | Constructor sin argumentos | Requerido por JPA para materializar la entidad. | Terminado |
| `Client.java` | Getters `getId`, `getFullName`, `getPersonalPhone`, `getPersonalEmail`, `getMunicipality`, `getState` | Exponen los datos necesarios para la respuesta resumida del listado. | Terminado |
| `Client.java` | Setters de identidad y contacto | `setFullName`, `setAlternateContactName`, `setBirthDate`, `setPersonalPhone`, `setWorkPhone`, `setPersonalEmail`, `setWorkEmail` asignan los datos validados. | Terminado |
| `Client.java` | Setters de fotografía, dirección y auditoría | `setPhotoPath`, `setPhotoContentType`, `setStreet`, `setNeighborhood`, `setMunicipality`, `setState`, `setPostalCode`, `setCreatedAt` asignan metadatos de archivo, domicilio y fecha de creación. | Terminado |
| `ClientRegistrationRequest.java` | Getters y setters de campos multipart | Transportan los 13 campos solicitados desde `@ModelAttribute` y aplican validaciones Bean Validation. | Terminado |
| `ClientRegistrationResponse.java` | Registro de respuesta | Entrega `clientId` y mensaje de éxito al formulario. | Terminado |
| `ClientSummaryResponse.java` | Registro de respuesta | Entrega datos mínimos necesarios para la lista: identificador, nombre, teléfono personal, correo personal, municipio y estado. | Terminado |
| `DuplicateClientException.java` | Constructor | Representa el rechazo de una coincidencia de contacto existente. | Terminado |
| `ApiExceptionHandler.java` | Manejadores de duplicidad | Convierte `DuplicateClientException` y conflictos de integridad de datos en una respuesta HTTP `409 Conflict` entendible para la vista. | Terminado |
| `AuthService.java` | `login(...)` | Autentica correo y contraseña; al ser exitoso entrega el JWT que habilita el módulo de clientes. | Terminado |
| `AuthService.java` | `register(...)` | Valida puesto, duplicidad, contraseña y palabra clave; persiste el acceso con hashes BCrypt. | Terminado |

## 6. Entidades y persistencia

| Entidad o recurso | Ubicación lógica | Información almacenada |
|---|---|---|
| `Client` | Tabla `clientes` | Datos personales, contactos, dirección, ruta y MIME de fotografía, fecha de creación. |
| `User` | Tabla `usuarios` | Usuario, correo, contraseña y palabra clave cifradas con BCrypt, además del rol que habilita o restringe el módulo. |
| Fotografía | Carpeta indicada por `app.storage.client-photos-dir` | Archivo recibido con nombre UUID; la base conserva ruta y tipo MIME. |

La tabla `clientes` declara unicidad para teléfono personal, teléfono de trabajo, correo personal y correo de trabajo. Además, `ClientRepository.existsWithAnyContact` evita coincidencias cruzadas, por ejemplo, que un correo de trabajo coincida con el correo personal previamente guardado de otro cliente.

La configuración actual no contiene una entidad `Sucursal` o `Taller`. La fachada y el contrato del módulo están separados de la vista, por lo que se podrá añadir posteriormente una relación como `branch_id` o `workshop_id` en `Client`, una validación de pertenencia y filtros por sucursal, sin sustituir el flujo de registro.

## 7. Flujo ejecutado

1. Gerente o Recepcionista inicia sesión con usuario, contraseña y palabra clave.
2. El backend valida las credenciales y el frontend conserva el JWT.
3. El usuario autorizado completa todos los datos y selecciona una fotografía de hasta 7 MiB.
4. Vue valida la fotografía, crea un `FormData` y envía `POST /api/clients` con el JWT.
5. Spring Security valida token y rol; usuarios no autorizados no llegan al controlador.
6. La fachada valida formatos, normaliza contactos y consulta el Repository para evitar duplicados.
7. Si no existe duplicidad, se guarda la fotografía y luego el cliente en MariaDB/MySQL.
8. El backend responde `201 Created` y el formulario muestra confirmación de registro exitoso con el identificador generado.
9. El evento `registered` recarga la vista de clientes mediante `GET /api/clients`.

## 8. Estado de la fase

| Elemento | Estado | Observación |
|---|---|---|
| Autenticación previa con JWT | Terminado | Requisito de acceso al módulo. |
| Autorización para Gerente y Recepcionista | Terminado | Aplicada en frontend y forzada en Spring Security. |
| Captura de todos los campos solicitados | Terminado | Incluye dirección desglosada y fotografía. |
| Límite de fotografía de 7 MiB | Terminado | Validado en navegador y backend. |
| Validación de formatos | Terminado | Bean Validation en backend y validación de archivo en ambos extremos. |
| Prevención de datos de contacto duplicados | Terminado | Validación de aplicación más restricciones únicas de base de datos. |
| Guardado y mensaje de confirmación | Terminado | Respuesta `201 Created`, mensaje y folio visual. |
| Consulta de clientes registrados | Terminado | Vista autorizada con actualización posterior a registro. |
| Patrón Facade en backend | Terminado | `ClientRegistrationFacade` concentra el caso de uso. |
| Patrón Repository | Terminado | `ClientRepository` encapsula persistencia. |
| Fachada dedicada del lado Vue | Pendiente | Extraer la preparación de `FormData` a `clientRegistrationFacade.js`. |
| Rol denominado literalmente `ADMINISTRADOR` | Pendiente de decisión | Actualmente se usa `GERENTE` como equivalente administrativo. |
| Relación física con sucursal o taller | Pendiente | Considerada en el diseño, aún no existe entidad ni columna. |
| Verificación binaria/allowlist estricto de formatos de imagen | Pendiente | Hoy se acepta `image/*`; se recomienda inspeccionar contenido real y permitir una lista explícita de formatos. |
| Pruebas automatizadas del caso de uso | Pendiente | Deben cubrir permisos, límite de archivo, duplicados, persistencia y respuestas HTTP. |
| Instalación de Archify | Pendiente de autorización | La instalación desde el repositorio externo `tt-a1i/archify` requiere autorización explícita antes de descargar y ejecutar componentes no verificados. |

## 9. Diagrama de componentes

El diagrama HTML actualizado se encuentra en [`diagrama_componentes_registro_cliente.html`](diagrama_componentes_registro_cliente.html). Representa Vue.js, API REST, Spring Security, `ClientRegistrationFacade`, `ClientRepository`, almacenamiento de fotografías y MariaDB/MySQL, incluyendo el flujo de consulta de clientes registrados.

## 10. Integración con GitHub

El repositorio local está vinculado al remoto configurado en GitHub:

`https://github.com/ZurisadaiSalvadorBadillo/taller-mecanico.git`

La documentación y el diagrama pertenecen al proyecto local. Su publicación en GitHub requiere el flujo normal de control de versiones del equipo.
