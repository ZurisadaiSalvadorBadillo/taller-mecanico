<script setup>
import { reactive, ref } from 'vue'
import api from '../services/api'

const emit = defineEmits(['registered'])

const emptyForm = () => ({
  fullName: '', alternateContactName: '', birthDate: '', personalPhone: '', workPhone: '',
  personalEmail: '', workEmail: '', street: '', neighborhood: '', municipality: '', state: '', postalCode: ''
})

const form = reactive(emptyForm())
const photo = ref(null)
const loading = ref(false)
const error = ref('')
const success = ref('')

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
    document.getElementById('client-photo').value = ''
  } catch (requestError) {
    error.value = requestError.response?.data?.message || 'No fue posible registrar al cliente.'
  } finally {
    loading.value = false
  }
}
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
          <label>Calle<input v-model.trim="form.street" class="form-control" maxlength="160" required /></label>
          <label>Colonia<input v-model.trim="form.neighborhood" class="form-control" maxlength="120" required /></label>
          <label>Municipio<input v-model.trim="form.municipality" class="form-control" maxlength="120" required /></label>
          <label>Estado<input v-model.trim="form.state" class="form-control" maxlength="120" required /></label>
          <label>Codigo postal<input v-model.trim="form.postalCode" class="form-control" inputmode="numeric" pattern="[0-9]{5}" maxlength="5" required /></label>
        </div>
      </div>

      <div v-if="error" class="alert alert-danger mb-0" role="alert">{{ error }}</div>
      <div v-if="success" class="alert alert-success mb-0" role="status">{{ success }}</div>
      <button class="btn btn-danger register-client-button" type="submit" :disabled="loading">
        {{ loading ? 'Guardando cliente...' : 'Guardar cliente' }}
      </button>
    </form>
  </section>
</template>
