# Caso de uso CB-02: Talleres y gestion de clientes

## Alcance implementado

- Creacion de talleres con fotografia, datos fiscales y direccion completa.
- Prevencion de talleres duplicados por nombre, RFC o correo.
- Asociacion obligatoria de cada cliente con un taller.
- Consulta de clientes por taller con paginacion de servidor (5 o 10 registros), orden ascendente o descendente, edicion y suspension.
- Suspension protegida para los roles `GERENTE` y `RECEPCIONISTA` tanto por configuracion de seguridad como por anotacion del controlador.

## Artefactos implementados

| Categoria | Nombre del Artefacto / Archivo | Tipo (Metodo, Funcion, Componente, Estado, Endpoint) | Descripcion de Proposito |
| --- | --- | --- | --- |
| Frontend | `frontend/src/views/DashboardView.vue` | Componente y estado `activeView` | Reorganiza el menu lateral y alterna entre Registro de talleres y Gestion de clientes. |
| Frontend | `frontend/src/views/DashboardView.vue` | Estado `workshopsVersion` | Fuerza la recarga de opciones de taller despues de registrar uno nuevo. |
| Frontend | `frontend/src/views/DashboardView.vue` | Estado `clientsVersion` | Refresca la gestion de clientes despues de un registro de cliente. |
| Frontend | `frontend/src/views/WorkshopRegistrationForm.vue` | Componente | Presenta el formulario de alta de taller con todos los campos solicitados. |
| Frontend | `WorkshopRegistrationForm.vue: emptyForm` | Funcion | Crea el estado inicial de nombre, direccion, razon social, telefono, RFC y correo. |
| Frontend | `WorkshopRegistrationForm.vue: form`, `photo`, `loading`, `error`, `success` | Estados | Conservan datos capturados, fotografia, progreso y mensajes de la alta de taller. |
| Frontend | `WorkshopRegistrationForm.vue: onPhotoChange` | Funcion | Rechaza localmente fotografias superiores a 15 MB. |
| Frontend | `WorkshopRegistrationForm.vue: registerWorkshop` | Funcion | Normaliza RFC a mayusculas, valida la expresion RFC, crea `FormData` y envia el alta a la API. |
| Frontend | `frontend/src/views/ClientRegistrationForm.vue` | Componente modificado | Agrega la seleccion obligatoria de taller al flujo existente de alta de cliente. |
| Frontend | `ClientRegistrationForm.vue: workshops`, `workshopsError` | Estados | Mantienen los talleres disponibles y cualquier error al cargarlos. |
| Frontend | `ClientRegistrationForm.vue: loadWorkshops` | Funcion | Consulta `GET /api/workshops` para poblar el selector de taller. |
| Frontend | `ClientRegistrationForm.vue: form.workshopId` | Estado | Envía la asociacion obligatoria `taller_id` al crear un cliente. |
| Frontend | `frontend/src/views/ClientManagementView.vue` | Componente | Consulta, filtra, pagina, ordena, edita y suspende clientes. |
| Frontend | `ClientManagementView.vue: workshops`, `clients`, `selectedWorkshopId` | Estados | Mantienen talleres, pagina de clientes y el filtro obligatorio por taller. |
| Frontend | `ClientManagementView.vue: page`, `pageSize`, `totalPages`, `totalElements`, `sortDirection` | Estados | Controlan paginacion en servidor, limite maximo de 10 y ordenamiento ASC/DESC. |
| Frontend | `ClientManagementView.vue: loadWorkshops` | Funcion | Carga talleres y selecciona el primero disponible como filtro inicial. |
| Frontend | `ClientManagementView.vue: loadClients` | Funcion | Solicita la pagina filtrada mediante `GET /api/clients` y carga fotografias de los resultados. |
| Frontend | `ClientManagementView.vue: changeFilter`, `changePage` | Funciones | Reinician la pagina al cambiar filtro u orden, o solicitan la pagina elegida. |
| Frontend | `ClientManagementView.vue: startEdit`, `cancelEdit`, `saveEdit` | Funciones | Abren, descartan y guardan la edicion completa de un cliente, incluida la reasignacion de taller. |
| Frontend | `ClientManagementView.vue: onEditPhotoChange` | Funcion | Valida que una fotografia de reemplazo no supere 7 MB. |
| Frontend | `ClientManagementView.vue: suspendClient` | Funcion | Confirma y ejecuta la suspension con `PATCH /api/clients/{id}/suspension`. |
| Frontend | `frontend/src/assets/styles.css` | Estilos modificados | Define filtros, paginacion, acciones y distintivos visuales para clientes activos o suspendidos. |
| Backend | `backend/src/main/java/com/taller/auth/model/Workshop.java` | Entidad | Modela `talleres`: identidad, direccion, datos fiscales, contacto y metadatos de fotografia. |
| Backend | `backend/src/main/java/com/taller/auth/model/Client.java` | Entidad modificada | Incluye la relacion obligatoria `taller_id` con `Workshop` y el estado `ACTIVO` o `SUSPENDIDO`. |
| Backend | `backend/src/main/java/com/taller/auth/repository/WorkshopRepository.java` | Repository | Persiste talleres, los lista por nombre y detecta nombre, RFC o correo duplicados. |
| Backend | `backend/src/main/java/com/taller/auth/repository/ClientRepository.java` | Repository modificado | Ejecuta pagina filtrada por `workshopId` y valida contactos duplicados, incluso al editar. |
| Backend | `backend/src/main/java/com/taller/auth/dto/WorkshopRegistrationRequest.java` | DTO | Recibe y valida los campos del taller, incluido RFC mexicano, CP, correo, telefono y foto. |
| Backend | `backend/src/main/java/com/taller/auth/dto/WorkshopSummaryResponse.java` | DTO | Devuelve identificador y nombre de taller para selectores del frontend. |
| Backend | `backend/src/main/java/com/taller/auth/dto/ClientRegistrationRequest.java` | DTO modificado | Hace obligatorio `workshopId` durante el registro de clientes. |
| Backend | `backend/src/main/java/com/taller/auth/dto/ClientUpdateRequest.java` | DTO | Recibe la actualizacion total del cliente y una fotografia opcional. |
| Backend | `backend/src/main/java/com/taller/auth/dto/ClientSummaryResponse.java` | DTO modificado | Incluye taller asociado y estado del cliente en cada resultado paginado. |
| Backend | `backend/src/main/java/com/taller/auth/service/WorkshopPhotoStorage.java` | Servicio | Guarda imagenes de taller, acepta solo imagenes y limita el archivo a 15 MB. |
| Backend | `backend/src/main/java/com/taller/auth/service/WorkshopRegistrationFacade.java` | Fachada | Coordina validacion de duplicados, fotografia, entidad y repositorio para registrar y listar talleres. |
| Backend | `WorkshopRegistrationFacade.register` | Metodo | Normaliza correo y RFC, rechaza duplicados, almacena foto y persiste el taller. |
| Backend | `WorkshopRegistrationFacade.list` | Metodo | Devuelve talleres ordenados para asociar clientes. |
| Backend | `backend/src/main/java/com/taller/auth/service/ClientRegistrationFacade.java` | Fachada modificada | Coordina registro, consulta paginada, edicion, suspension y asociacion con taller. |
| Backend | `ClientRegistrationFacade.register` | Metodo | Valida contactos, busca taller obligatorio y crea el cliente con estado `ACTIVO`. |
| Backend | `ClientRegistrationFacade.listRegisteredClients` | Metodo | Aplica paginacion, filtro por taller y orden por nombre en la base de datos. |
| Backend | `ClientRegistrationFacade.update` | Metodo | Actualiza todos los datos, permite cambiar taller y conserva o reemplaza la foto. |
| Backend | `ClientRegistrationFacade.suspend` | Metodo | Cambia el estado del cliente a `SUSPENDIDO`. |
| Backend | `ClientRegistrationFacade.findWorkshop`, `toSummary` | Metodos | Validan el taller elegido y construyen la respuesta de cliente. |
| Backend | `backend/src/main/java/com/taller/auth/controller/WorkshopController.java` | Controlador | Expone los endpoints de alta y consulta de talleres. |
| API | `POST /api/workshops` | Endpoint | Registra un taller usando `multipart/form-data`; requiere GERENTE o RECEPCIONISTA. |
| API | `GET /api/workshops` | Endpoint | Devuelve los talleres disponibles para asociar clientes; requiere GERENTE o RECEPCIONISTA. |
| Backend | `backend/src/main/java/com/taller/auth/controller/ClientController.java` | Controlador modificado | Expone el alta, consulta paginada, edicion, suspension y fotografia de clientes. |
| API | `POST /api/clients` | Endpoint | Registra un cliente con `workshopId` obligatorio. |
| API | `GET /api/clients?workshopId=&page=&size=&sortDirection=` | Endpoint | Devuelve clientes del taller seleccionado; `size` admite como maximo 10 y `sortDirection` admite ASC o DESC. |
| API | `PUT /api/clients/{clientId}` | Endpoint | Edita todos los datos del cliente, incluida la reasignacion de taller. |
| API | `PATCH /api/clients/{clientId}/suspension` | Endpoint | Suspende al cliente; solo GERENTE y RECEPCIONISTA pueden invocarlo. |
| API | `GET /api/clients/{clientId}/photo` | Endpoint existente protegido | Entrega la fotografia de cada cliente listado. |
| Backend | `backend/src/main/java/com/taller/auth/config/SecurityConfig.java` | Middleware de seguridad modificado | Restringe rutas de talleres, edicion y suspension de clientes a GERENTE y RECEPCIONISTA. |
| Backend | `@PreAuthorize` en `WorkshopController` y `ClientController` | Autorizacion de controlador | Repite la politica RBAC a nivel de metodo para impedir la suspension desde cualquier otro rol. |
| Backend | `backend/src/main/java/com/taller/auth/controller/ApiExceptionHandler.java` | Manejador modificado | Convierte la duplicidad de taller en respuesta HTTP 409 con mensaje claro. |
| Backend | `backend/src/main/resources/application.properties` | Configuracion modificada | Ajusta el limite global multipart a 15 MB y define el directorio `uploads/talleres`. |
| Documentacion | `diagrama_componentes.mmd` | Diagrama Mermaid | Representa Vue, API, JWT/RBAC, fachadas, repositorios, tablas y almacenamiento de fotos. |

## Validaciones aplicadas

| Regla | Frontend | Backend |
| --- | --- | --- |
| RFC de Mexico | Regex antes de enviar el formulario. | `@Pattern` con `^[A-Z&Ñ]{3,4}[0-9]{2}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])[A-Z0-9]{3}$`. |
| Taller duplicado | Mensaje recibido de la API. | Rechaza nombre, RFC o correo repetidos con HTTP 409. |
| Foto de taller | Rechaza archivos mayores de 15 MB. | Solo imagen y maximo 15 MB. |
| Cliente asociado | Selector obligatorio de taller. | `workshopId` obligatorio y FK `taller_id` no nula. |
| Paginacion | Solo permite 5 o 10 registros. | `size` validado con maximo 10. |
| Suspension | Boton solo disponible en la gestion autorizada. | `SecurityConfig` y `@PreAuthorize` exigen GERENTE o RECEPCIONISTA. |
