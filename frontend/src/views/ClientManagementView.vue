<script setup>
import { onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import api from '../services/api'

const workshops = ref([])
const clients = ref([])
const selectedWorkshopId = ref('')
const page = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = ref(10)
const sortDirection = ref('ASC')
const loading = ref(false)
const error = ref('')
const success = ref('')
const editingId = ref(null)
const editPhoto = ref(null)
const editForm = reactive(emptyEditForm())

function emptyEditForm() {
  return {
    fullName: '', alternateContactName: '', birthDate: '', personalPhone: '', workPhone: '', personalEmail: '', workEmail: '',
    street: '', neighborhood: '', municipality: '', state: '', postalCode: '', workshopId: ''
  }
}

function clearPhotoPreviews() {
  clients.value.forEach((client) => client.photoPreview && URL.revokeObjectURL(client.photoPreview))
}

async function loadWorkshops() {
  try {
    const { data } = await api.get('/workshops')
    workshops.value = data
    if (!selectedWorkshopId.value && data.length) selectedWorkshopId.value = data[0].id
  } catch (requestError) {
    error.value = requestError.response?.data?.message || 'No fue posible cargar los talleres.'
  }
}

async function loadClients() {
  if (!selectedWorkshopId.value) {
    clients.value = []
    return
  }
  loading.value = true
  error.value = ''
  success.value = ''
  clearPhotoPreviews()
  try {
    const { data } = await api.get('/clients', {
      params: { workshopId: selectedWorkshopId.value, page: page.value, size: pageSize.value, sortDirection: sortDirection.value }
    })
    totalPages.value = data.totalPages
    totalElements.value = data.totalElements
    clients.value = await Promise.all(data.content.map(async (client) => {
      const photo = await api.get(client.photoUrl, { responseType: 'blob' })
      return { ...client, photoPreview: URL.createObjectURL(photo.data) }
    }))
  } catch (requestError) {
    error.value = requestError.response?.data?.message || 'No fue posible cargar los clientes.'
  } finally {
    loading.value = false
  }
}

function changeFilter() {
  page.value = 0
  loadClients()
}

function changePage(nextPage) {
  page.value = nextPage
  loadClients()
}

function startEdit(client) {
  editingId.value = client.id
  editPhoto.value = null
  Object.assign(editForm, {
    fullName: client.fullName, alternateContactName: client.alternateContactName, birthDate: client.birthDate,
    personalPhone: client.personalPhone, workPhone: client.workPhone, personalEmail: client.personalEmail,
    workEmail: client.workEmail, street: client.street, neighborhood: client.neighborhood, municipality: client.municipality,
    state: client.state, postalCode: client.postalCode, workshopId: client.workshopId
  })
}

function cancelEdit() {
  editingId.value = null
  editPhoto.value = null
  Object.assign(editForm, emptyEditForm())
}

function onEditPhotoChange(event) {
  const selected = event.target.files?.[0] || null
  if (selected && selected.size > 7 * 1024 * 1024) {
    event.target.value = ''
    editPhoto.value = null
    error.value = 'La fotografia del cliente no puede superar 7 MB.'
    return
  }
  editPhoto.value = selected
}

async function saveEdit() {
  error.value = ''
  const payload = new FormData()
  Object.entries(editForm).forEach(([key, value]) => payload.append(key, value))
  if (editPhoto.value) payload.append('photo', editPhoto.value)
  try {
    await api.put(`/clients/${editingId.value}`, payload, { headers: { 'Content-Type': 'multipart/form-data' } })
    success.value = 'Cliente actualizado correctamente.'
    cancelEdit()
    page.value = 0
    await loadClients()
  } catch (requestError) {
    error.value = requestError.response?.data?.message || 'No fue posible actualizar el cliente.'
  }
}

async function suspendClient(client) {
  if (client.status === 'SUSPENDIDO' || !window.confirm(`Suspender a ${client.fullName}?`)) return
  error.value = ''
  try {
    await api.patch(`/clients/${client.id}/suspension`)
    success.value = 'Cliente suspendido correctamente.'
    await loadClients()
  } catch (requestError) {
    error.value = requestError.response?.data?.message || 'No fue posible suspender el cliente.'
  }
}

onMounted(async () => {
  await loadWorkshops()
  await loadClients()
})
onBeforeUnmount(clearPhotoPreviews)
</script>

<template>
  <section class="registered-clients" aria-labelledby="registered-clients-title">
    <div class="section-heading client-heading">
      <div><p class="eyebrow">Consulta por taller</p><h2 id="registered-clients-title">Gestion de clientes</h2></div>
      <button class="btn btn-outline-dark btn-sm" type="button" :disabled="loading" @click="loadClients">Actualizar</button>
    </div>
    <div class="client-filters">
      <label>Taller
        <select v-model="selectedWorkshopId" class="form-select" @change="changeFilter">
          <option value="" disabled>Selecciona un taller</option>
          <option v-for="workshop in workshops" :key="workshop.id" :value="workshop.id">{{ workshop.name }}</option>
        </select>
      </label>
      <label>Registros por pagina
        <select v-model.number="pageSize" class="form-select" @change="changeFilter"><option :value="5">5</option><option :value="10">10</option></select>
      </label>
      <label>Orden
        <select v-model="sortDirection" class="form-select" @change="changeFilter"><option value="ASC">Ascendente</option><option value="DESC">Descendente</option></select>
      </label>
    </div>
    <div v-if="error" class="alert alert-danger mt-3 mb-0" role="alert">{{ error }}</div>
    <div v-if="success" class="alert alert-success mt-3 mb-0" role="status">{{ success }}</div>
    <p v-if="loading" class="empty-clients">Cargando clientes...</p>
    <p v-else-if="!selectedWorkshopId" class="empty-clients">Selecciona un taller para consultar sus clientes.</p>
    <p v-else-if="clients.length === 0" class="empty-clients">No hay clientes en el taller seleccionado.</p>
    <div v-else class="client-detail-grid">
      <article v-for="client in clients" :key="client.id" class="client-detail-card">
        <img class="client-photo" :src="client.photoPreview" :alt="`Fotografia de ${client.fullName}`" />
        <div class="client-detail-heading"><p class="eyebrow">Folio #CL-{{ client.id }}</p><h3>{{ client.fullName }}</h3><span class="client-status" :class="{ suspended: client.status === 'SUSPENDIDO' }">{{ client.status }}</span></div>
        <dl class="client-data">
          <div><dt>Taller</dt><dd>{{ client.workshopName }}</dd></div>
          <div><dt>Contacto alternativo</dt><dd>{{ client.alternateContactName }}</dd></div>
          <div><dt>Fecha de nacimiento</dt><dd>{{ client.birthDate }}</dd></div>
          <div><dt>Telefono personal</dt><dd>{{ client.personalPhone }}</dd></div>
          <div><dt>Telefono de trabajo</dt><dd>{{ client.workPhone }}</dd></div>
          <div><dt>Gmail personal</dt><dd>{{ client.personalEmail }}</dd></div>
          <div><dt>Gmail del trabajo</dt><dd>{{ client.workEmail }}</dd></div>
          <div class="client-address"><dt>Direccion</dt><dd>{{ client.street }}, {{ client.neighborhood }}, {{ client.municipality }}, {{ client.state }}, CP {{ client.postalCode }}</dd></div>
        </dl>
        <div class="client-actions"><button class="btn btn-outline-dark btn-sm" type="button" @click="startEdit(client)">Editar</button><button class="btn btn-danger btn-sm" type="button" :disabled="client.status === 'SUSPENDIDO'" @click="suspendClient(client)">Suspender</button></div>
      </article>
    </div>
    <div v-if="totalPages > 1" class="pagination-bar"><span>{{ totalElements }} clientes</span><button class="btn btn-outline-dark btn-sm" :disabled="page === 0 || loading" @click="changePage(page - 1)">Anterior</button><span>Pagina {{ page + 1 }} de {{ totalPages }}</span><button class="btn btn-outline-dark btn-sm" :disabled="page + 1 >= totalPages || loading" @click="changePage(page + 1)">Siguiente</button></div>
    <form v-if="editingId" class="client-form edit-client-form" @submit.prevent="saveEdit">
      <div class="section-heading client-heading"><div><p class="eyebrow">Edicion</p><h2>Actualizar cliente</h2></div><button class="btn btn-outline-dark btn-sm" type="button" @click="cancelEdit">Cancelar</button></div>
      <div class="form-section"><div class="form-grid">
        <label>Nombre completo<input v-model.trim="editForm.fullName" class="form-control" required /></label>
        <label>Contacto alternativo<input v-model.trim="editForm.alternateContactName" class="form-control" required /></label>
        <label>Fecha de nacimiento<input v-model="editForm.birthDate" class="form-control" type="date" required /></label>
        <label>Taller<select v-model="editForm.workshopId" class="form-select" required><option v-for="workshop in workshops" :key="workshop.id" :value="workshop.id">{{ workshop.name }}</option></select></label>
        <label>Telefono personal<input v-model.trim="editForm.personalPhone" class="form-control" required /></label>
        <label>Telefono de trabajo<input v-model.trim="editForm.workPhone" class="form-control" required /></label>
        <label>Gmail personal<input v-model.trim="editForm.personalEmail" class="form-control" type="email" required /></label>
        <label>Gmail del trabajo<input v-model.trim="editForm.workEmail" class="form-control" type="email" required /></label>
        <label>Fotografia nueva (opcional)<input class="form-control" type="file" accept="image/*" @change="onEditPhotoChange" /></label>
        <label>Calle<input v-model.trim="editForm.street" class="form-control" required /></label>
        <label>Colonia<input v-model.trim="editForm.neighborhood" class="form-control" required /></label>
        <label>Municipio<input v-model.trim="editForm.municipality" class="form-control" required /></label>
        <label>Estado<input v-model.trim="editForm.state" class="form-control" required /></label>
        <label>Codigo postal<input v-model.trim="editForm.postalCode" class="form-control" pattern="[0-9]{5}" maxlength="5" required /></label>
      </div></div>
      <button class="btn btn-danger register-client-button" type="submit">Guardar cambios</button>
    </form>
  </section>
</template>
