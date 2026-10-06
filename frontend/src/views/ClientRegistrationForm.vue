<script setup>
import { onMounted, reactive, ref } from 'vue'
import api from '../services/api'
import { findLocationByPostalCode, getStates, isMexicanPostalCode, searchNeighborhoods } from '../services/locationDirectory.js'

const emit = defineEmits(['registered'])

const emptyForm = () => ({
  fullName: '', alternateContactName: '', birthDate: '', personalPhone: '', workPhone: '',
  personalEmail: '', workEmail: '', street: '', neighborhood: '', municipality: '', state: '', postalCode: '', workshopId: ''
})

const form = reactive(emptyForm())
const photo = ref(null)
const loading = ref(false)
const locationLoading = ref(false)
const error = ref('')
const success = ref('')
const locationFeedback = ref('')
const selectedStateCode = ref('')
const selectedNeighborhoodId = ref('')
const neighborhoodQuery = ref('')
const neighborhoodOptions = ref([])
const selectedLocation = ref(null)
const stateOptions = getStates()
const workshops = ref([])
const workshopsError = ref('')
let locationRequest = 0

async function loadWorkshops() {
  workshopsError.value = ''
  try {
    const { data } = await api.get('/workshops')
    workshops.value = data
  } catch {
    workshopsError.value = 'No fue posible cargar los talleres disponibles.'
  }
}

function applyLocation(location, feedback) {
  selectedStateCode.value = location.stateCode
  form.state = location.state
  form.municipality = location.municipality
  form.neighborhood = location.neighborhood
  form.postalCode = location.postalCode
  selectedNeighborhoodId.value = location.id
  selectedLocation.value = location
  locationFeedback.value = feedback
}

async function onPostalCodeInput() {
  form.postalCode = form.postalCode.replace(/\D/g, '').slice(0, 5)
  selectedLocation.value = null
  selectedNeighborhoodId.value = ''
  neighborhoodOptions.value = []
  locationFeedback.value = ''
  if (!isMexicanPostalCode(form.postalCode)) return

  const requestId = ++locationRequest
  locationLoading.value = true
  try {
    const result = await findLocationByPostalCode(form.postalCode)
    if (requestId !== locationRequest) return
    if (!result) {
      form.state = ''
      form.municipality = ''
      form.neighborhood = ''
      locationFeedback.value = 'El codigo postal no fue encontrado en el catalogo nacional.'
      return
    }

    form.state = result.state
    selectedStateCode.value = result.stateCode
    form.municipality = result.municipality
    form.neighborhood = ''
    neighborhoodOptions.value = result.neighborhoods
    locationFeedback.value = `Se cargaron ${result.neighborhoods.length} colonias de ${result.municipality}, ${result.state}. Selecciona una.`

    if (result.neighborhoods.length === 1) {
      applyLocation(result.neighborhoods[0], 'Ubicacion completada desde el codigo postal.')
    }
  } catch {
    locationFeedback.value = 'No fue posible consultar el catalogo nacional de codigos postales.'
  } finally {
    if (requestId === locationRequest) locationLoading.value = false
  }
}

function onStateChange() {
  const state = stateOptions.find((option) => option.code === selectedStateCode.value)
  form.state = state?.name || ''
  selectedNeighborhoodId.value = ''
  selectedLocation.value = null
  neighborhoodQuery.value = ''
  neighborhoodOptions.value = []
  form.neighborhood = ''
  form.municipality = ''
  form.postalCode = ''
  locationFeedback.value = 'Escribe al menos tres letras de la colonia para buscarla en el estado seleccionado.'
}

async function onNeighborhoodQuery() {
  selectedNeighborhoodId.value = ''
  selectedLocation.value = null
  form.neighborhood = ''
  form.municipality = ''
  form.postalCode = ''
  if (!form.state || neighborhoodQuery.value.trim().length < 3) {
    neighborhoodOptions.value = []
    return
  }

  const state = stateOptions.find((option) => option.code === selectedStateCode.value)
  if (!state) {
    locationFeedback.value = 'Selecciona un estado valido para buscar sus colonias.'
    return
  }
  const requestId = ++locationRequest
  locationLoading.value = true
  try {
    const matches = await searchNeighborhoods(state.code, neighborhoodQuery.value)
    if (requestId !== locationRequest) return
    neighborhoodOptions.value = matches
    locationFeedback.value = matches.length ? `Se encontraron ${matches.length} colonias. Selecciona una.` : 'No se encontraron colonias con ese nombre en el estado seleccionado.'
  } catch {
    locationFeedback.value = 'No fue posible buscar colonias en el catalogo nacional.'
  } finally {
    if (requestId === locationRequest) locationLoading.value = false
  }
}

function onNeighborhoodChange() {
  const location = neighborhoodOptions.value.find((option) => option.id === selectedNeighborhoodId.value)
  if (!location) return
  applyLocation(location, `Codigo postal ${location.postalCode} cargado automaticamente.`)
}

function onPhotoChange(event) {
  const selectedPhoto = event.target.files?.[0] || null
  photo.value = selectedPhoto
  error.value = ''
  if (selectedPhoto && selectedPhoto.size > 7 * 1024 * 1024) {
    photo.value = null
    event.target.value = ''
    error.value = 'La fotografia no puede superar 7 MB.'
  }
}

async function registerClient() {
  error.value = ''
  success.value = ''
  if (!photo.value) {
    error.value = 'Selecciona una fotografia del cliente.'
    return
  }
  if (!selectedLocation.value) {
    error.value = 'Selecciona una colonia valida para confirmar la direccion.'
    return
  }
  if (!form.workshopId) {
    error.value = 'Selecciona el taller al que pertenece el cliente.'
    return
  }

  const payload = new FormData()
  Object.entries(form).forEach(([key, value]) => payload.append(key, value))
  payload.append('photo', photo.value)
  loading.value = true
  try {
    const { data } = await api.post('/clients', payload, { headers: { 'Content-Type': 'multipart/form-data' } })
    success.value = `${data.message} Folio: ${data.clientId}.`
    emit('registered')
    Object.assign(form, emptyForm())
    photo.value = null
    locationFeedback.value = ''
    selectedStateCode.value = ''
    selectedNeighborhoodId.value = ''
    neighborhoodQuery.value = ''
    neighborhoodOptions.value = []
    selectedLocation.value = null
    document.getElementById('client-photo').value = ''
  } catch (requestError) {
    error.value = requestError.response?.data?.message || 'No fue posible registrar al cliente.'
  } finally {
    loading.value = false
  }
}

onMounted(loadWorkshops)
</script>

<template>
  <section class="client-registration" aria-labelledby="client-registration-title">
    <div class="section-heading client-heading">
      <div>
        <p class="eyebrow">Registro autorizado</p>
        <h2 id="client-registration-title">Nuevo cliente</h2>
      </div>
      <span class="form-note">Todos los campos son obligatorios</span>
    </div>

    <form class="client-form" @submit.prevent="registerClient">
      <div class="form-section">
        <h3>Asignacion de taller</h3>
        <div class="form-grid">
          <label>Taller
            <select v-model="form.workshopId" class="form-select" required :disabled="!workshops.length">
              <option value="" disabled>{{ workshops.length ? 'Selecciona un taller' : 'No hay talleres registrados' }}</option>
              <option v-for="workshop in workshops" :key="workshop.id" :value="workshop.id">{{ workshop.name }}</option>
            </select>
          </label>
        </div>
        <p v-if="workshopsError" class="location-feedback is-error">{{ workshopsError }}</p>
      </div>
      <div class="form-section">
        <h3>Datos personales</h3>
        <div class="form-grid">
          <label>Nombre completo<input v-model.trim="form.fullName" class="form-control" maxlength="160" required /></label>
          <label>Contacto alternativo<input v-model.trim="form.alternateContactName" class="form-control" maxlength="160" required /></label>
          <label>Fecha de nacimiento<input v-model="form.birthDate" class="form-control" type="date" required /></label>
          <label>Fotografia (maximo 7 MB)<input id="client-photo" class="form-control" type="file" accept="image/*" required @change="onPhotoChange" /></label>
        </div>
      </div>

      <div class="form-section">
        <h3>Contacto</h3>
        <div class="form-grid">
          <label>Telefono personal<input v-model.trim="form.personalPhone" class="form-control" type="tel" pattern="[0-9+() -]{7,25}" required /></label>
          <label>Telefono de trabajo<input v-model.trim="form.workPhone" class="form-control" type="tel" pattern="[0-9+() -]{7,25}" required /></label>
          <label>Gmail personal<input v-model.trim="form.personalEmail" class="form-control" type="email" required /></label>
          <label>Gmail del trabajo<input v-model.trim="form.workEmail" class="form-control" type="email" required /></label>
        </div>
      </div>

      <div class="form-section">
        <h3>Direccion</h3>
        <div class="form-grid">
          <label>Calle (captura manual)<input v-model.trim="form.street" class="form-control" maxlength="160" required /></label>
          <label>Codigo postal
            <input v-model.trim="form.postalCode" class="form-control" inputmode="numeric" pattern="[0-9]{5}" maxlength="5" required @input="onPostalCodeInput" />
          </label>
          <label>Estado
            <select v-model="selectedStateCode" class="form-select" required @change="onStateChange">
              <option value="" disabled>Selecciona un estado</option>
              <option v-for="state in stateOptions" :key="state.code" :value="state.code">{{ state.name }}</option>
            </select>
          </label>
          <label>Buscar colonia en el estado
            <input v-model.trim="neighborhoodQuery" class="form-control" :disabled="!form.state" minlength="3" @input="onNeighborhoodQuery" />
          </label>
          <label>Colonia
            <select v-model="selectedNeighborhoodId" class="form-select" :disabled="!neighborhoodOptions.length" required @change="onNeighborhoodChange">
              <option value="" disabled>Selecciona una colonia</option>
              <option v-for="location in neighborhoodOptions" :key="location.id" :value="location.id">{{ location.neighborhood }} ({{ location.neighborhoodType }})</option>
            </select>
          </label>
          <label>Municipio<input v-model="form.municipality" class="form-control" maxlength="120" readonly required /></label>
        </div>
        <p v-if="locationLoading" class="location-feedback" role="status">Consultando catalogo nacional...</p>
        <p v-else-if="locationFeedback" class="location-feedback" :class="{ 'is-error': locationFeedback.includes('No fue') || locationFeedback.includes('no fue') }" role="status">{{ locationFeedback }}</p>
      </div>

      <div v-if="error" class="alert alert-danger mb-0" role="alert">{{ error }}</div>
      <div v-if="success" class="alert alert-success mb-0" role="status">{{ success }}</div>
      <button class="btn btn-danger register-client-button" type="submit" :disabled="loading || locationLoading">
        {{ loading ? 'Guardando cliente...' : 'Guardar cliente' }}
      </button>
    </form>
  </section>
</template>
