# Caso de uso CB-02: Talleres, clientes, estatus y vehiculos

| Categoria | Nombre del Artefacto / Archivo | Tipo (Metodo, Funcion, Componente, Estado, Endpoint) | Descripcion de Proposito |
| --- | --- | --- | --- |
| Seguridad | `Role` | Modelo | Agrega `ADMINISTRADOR` y `SECRETARIA`; los roles anteriores se conservan solo para compatibilidad. |
| Seguridad | `SecurityConfig` | Middleware | Protege clientes, talleres y vehiculos con ADMINISTRADOR/SECRETARIA; reserva suspension y catalogo para ADMINISTRADOR. |
| Seguridad | `AuthService.register`, `UserService.createUser` | Metodos | Permiten registrar exclusivamente los nuevos roles operativos. |
| Frontend | `LoginView.vue` | Componente modificado | Presenta Administrador y Secretaria como puestos disponibles. |
| Talleres | `Workshop` | Entidad | Almacena nombre, razon social, RFC, correo, telefono, foto y direccion con calle, colonia, localidad, municipio, estado y CP. |
| Talleres | `WorkshopRegistrationRequest` | DTO | Valida todos los campos, RFC mexicano, CP, correo y fotografia requerida. |
| Talleres | `WorkshopRepository.existsWithDuplicateIdentity` | Repository | Rechaza RFC, correo o nombre de taller duplicados. |
| Talleres | `WorkshopPhotoStorage.store` | Servicio | Guarda solo imagenes de hasta 15 MB. |
| Talleres | `WorkshopRegistrationFacade.register`, `list` | Fachada | Coordina duplicados, almacenamiento de foto, persistencia y selector de talleres. |
| Talleres | `WorkshopController` | Controlador | Expone talleres a usuarios autorizados. |
| API | `POST /api/workshops` | Endpoint | Registra taller por `multipart/form-data`; ADMINISTRADOR o SECRETARIA. |
| API | `GET /api/workshops` | Endpoint | Lista talleres disponibles para asociacion; ADMINISTRADOR o SECRETARIA. |
| Frontend | `WorkshopRegistrationForm.vue` | Componente | Captura el alta completa del taller. |
| Frontend | `WorkshopRegistrationForm: form`, `photo`, `loading`, `error`, `success` | Estados | Conservan captura, fotografia, progreso y mensajes. |
| Frontend | `WorkshopRegistrationForm: onPhotoChange`, `registerWorkshop` | Funciones | Valida 15 MB, RFC y envía la alta. |
| Estatus | `UserStatus` | Entidad | Implementa tabla `status_usuario` con id, str_valor, str_descripcion y actualizado. |
| Estatus | `UserStatusSeeder` | Configuracion | Inserta ACTIVO, SUSPENDIDO, CANCELADO y ELIMINADO al iniciar. |
| Estatus | `UserStatusRepository` | Repository | Consulta el catalogo y localiza estatus por valor. |
| Estatus | `UserStatusService.list`, `create`, `update`, `delete`, `required` | Servicio | Implementa CRUD y baja logica con la marca `deleted`, sin borrar el registro fisicamente. |
| Estatus | `UserStatusController` | Controlador | Restringe el CRUD completo de estatus a ADMINISTRADOR. |
| API | `GET/POST /api/user-statuses` | Endpoints | Consulta y crea elementos de catalogo. |
| API | `PUT/DELETE /api/user-statuses/{id}` | Endpoints | Actualiza y elimina logicamente elementos de catalogo. |
| Frontend | `UserStatusCatalogView.vue` | Componente | Pantalla CRUD para el catalogo de estatus. |
| Frontend | `UserStatusCatalogView: statuses`, `form`, `editingId`, `error` | Estados | Controlan listado, edicion y errores del catalogo. |
| Frontend | `UserStatusCatalogView: load`, `edit`, `save`, `remove` | Funciones | Ejecutan el CRUD con confirmacion de eliminacion logica. |
| Clientes | `Client` | Entidad modificada | Asocia taller y `status_usuario`; incluye nombre, apellidos, CURP, RFC, edad, contacto diferenciado y localidad. |
| Clientes | `ClientRegistrationRequest` | DTO | Valida CURP, RFC, edad, telefonos, direccion y taller obligatorios. |
| Clientes | `ClientUpdateRequest` | DTO | Reutiliza el contrato normalizado para editar todos los datos. |
| Clientes | `ClientSummaryResponse` | DTO | Devuelve datos normalizados, taller y estatus para la interfaz. |
| Clientes | `ClientRepository.findByWorkshopId` | Repository | Obtiene clientes aislados por taller con paginacion del servidor. |
| Clientes | `ClientRepository.existsByFirstNameIgnoreCaseAndPaternalSurnameIgnoreCaseAndMaternalSurnameIgnoreCase` | Repository | Evita duplicados por nombre y apellidos. |
| Clientes | `ClientRegistrationFacade.register` | Fachada | Normaliza texto, verifica duplicados, asocia taller y estatus ACTIVO. |
| Clientes | `ClientRegistrationFacade.listRegisteredClients` | Fachada | Aplica filtro obligatorio, pagina maxima de 10 y orden ASC/DESC por nombre. |
| Clientes | `ClientRegistrationFacade.update` | Fachada | Edita datos completos y permite reasignar taller. |
| Clientes | `ClientRegistrationFacade.suspend` | Fachada | Rechaza una segunda suspension y asigna estatus SUSPENDIDO. |
| API | `POST /api/clients` | Endpoint | Crea cliente asociado a taller; ADMINISTRADOR o SECRETARIA. |
| API | `GET /api/clients?workshopId=&page=&size=&sortDirection=` | Endpoint | Consulta paginada; `size` maximo 10 y orden ASC/DESC. |
| API | `PUT /api/clients/{clientId}` | Endpoint | Edita cliente y taller mediante formulario multipart. |
| API | `PATCH /api/clients/{clientId}/suspension` | Endpoint | Suspende solo con rol ADMINISTRADOR. |
| Frontend | `ClientRegistrationForm.vue` | Componente | Registra cliente normalizado y lo vincula obligatoriamente a un taller. |
| Frontend | `ClientRegistrationForm: form`, `photo`, `workshops`, `neighborhoodOptions`, `stateCode`, `localityId` | Estados | Gestionan identidad, foto, taller y direccion validada por CP. |
| Frontend | `ClientRegistrationForm: onPostalCode`, `searchLocality`, `selectLocality`, `selectState` | Funciones | Autocompletan estado, municipio y localidad a partir de CP o busqueda estatal. |
| Frontend | `ClientRegistrationForm: title`, `registerClient` | Funciones | Normalizan texto, validan CURP/RFC y envian el registro. |
| Frontend | `ClientManagementView.vue` | Componente | Lista por taller, pagina, ordena, edita, suspende y abre gestion de vehiculos. |
| Frontend | `ClientManagementView: workshopId`, `page`, `pageSize`, `sortDirection`, `clients`, `totalPages` | Estados | Controlan filtro, paginacion y ordenamiento de servidor. |
| Frontend | `ClientManagementView: loadClients`, `filter`, `startEdit`, `saveEdit`, `suspend` | Funciones | Gestionan consulta, ventana de edicion y suspension autorizada. |
| Vehiculos | `Vehicle` | Entidad | Almacena VIN, placa, marca, modelo, año, version, kilometraje y numero de serie asociados a un cliente. |
| Vehiculos | `VehicleRequest` | DTO | Aplica regex estricta VIN de 17 caracteres, placa en mayusculas y campos requeridos. |
| Vehiculos | `VehicleRepository` | Repository | Consulta vehiculos por cliente y evita VIN o placa duplicados. |
| Vehiculos | `VehicleService.create`, `list` | Servicio | Exige cliente ACTIVO antes de asociar un vehiculo. |
| Vehiculos | `VehicleController` | Controlador | Restringe la asociacion y consulta a ADMINISTRADOR o SECRETARIA. |
| API | `GET/POST /api/clients/{clientId}/vehicles` | Endpoints | Consulta y asocia vehiculos de un cliente activo. |
| Frontend | `ClientManagementView: selectedClient`, `vehicle`, `brands`, `models`, `vehicles` | Estados | Controlan el modal y la informacion del vehiculo. |
| Frontend | `ClientManagementView: openVehicles`, `loadBrands`, `loadModels`, `addVehicle` | Funciones | Abren el modal, consumen NHTSA vPIC y registran el vehiculo. |
| Frontend | `DashboardView.vue` | Componente modificado | Incorpora menu de talleres, clientes y catalogo de estatus visible solo al administrador. |
| Frontend | `RegisteredWorkshopsView.vue` | Componente | Presenta todos los talleres registrados con datos fiscales, contacto y direccion completa. |
| Frontend | `ClientManagementView: workshopId` | Estado modificado | Inicia en "Todos los talleres" y permite aplicar un filtro individual opcional. |
| Backend | `ClientController.list`, `ClientRegistrationFacade.listRegisteredClients` | Endpoint y fachada modificados | Admiten consulta paginada global cuando no se recibe `workshopId`. |
| Backend | `WorkshopSummaryResponse`, `WorkshopRegistrationFacade.toSummary` | DTO y metodo modificados | Devuelven los datos completos requeridos para visualizar talleres registrados. |
| Backend | `DemoDataSeeder` | Configuracion de datos | Crea de forma idempotente 5 talleres demo y 10 usuarios demo al arrancar con base de datos disponible. |
| Documentacion | `diagrama_componentes.mmd` | Mermaid | Describe Vue, API, JWT/RBAC, catalogo, clientes, vehiculos, repositorios y almacenamiento. |

## Fuente externa

La selección dinámica de marcas y modelos consume vPIC de la NHTSA mediante `GetAllMakes` y `GetModelsForMake`. La fuente es oficial de la National Highway Traffic Safety Administration; apoya datos de fabricante y VIN, pero no sustituye una validación legal mexicana de placas o propiedad.
