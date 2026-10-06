<script setup>
import { reactive, ref } from 'vue'
import api from '../services/api'

const emit = defineEmits(['registered'])
const rfcPattern = /^[A-Z&Ñ]{3,4}[0-9]{2}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])[A-Z0-9]{3}$/
const emptyForm = () => ({ name: '', state: '', postalCode: '', municipality: '', neighborhood: '', street: '', businessName: '', phone: '', rfc: '', email: '' })
const form = reactive(emptyForm())
const photo = ref(null)
const loading = ref(false)
const error = ref('')
const success = ref('')

function onPhotoChange(event) {
  const selected = event.target.files?.[0] || null
  photo.value = selected
  error.value = ''
  if (selected && selected.size > 15 * 1024 * 1024) {
    photo.value = null
    event.target.value = ''
    error.value = 'La fotografia del taller no puede superar 15 MB.'
  }
}

async function registerWorkshop() {
  error.value = ''
  success.value = ''
  form.rfc = form.rfc.trim().toUpperCase()
  if (!rfcPattern.test(form.rfc)) {
    error.value = 'El RFC no tiene un formato valido.'
    return
  }
  if (!photo.value) {
    error.value = 'Selecciona la fotografia del taller.'
    return
  }
  const payload = new FormData()
  Object.entries(form).forEach(([key, value]) => payload.append(key, value))
  payload.append('photo', photo.value)
  loading.value = true
  try {
    const { data } = await api.post('/workshops', payload, { headers: { 'Content-Type': 'multipart/form-data' } })
    success.value = `Taller registrado correctamente. Folio: ${data.id}.`
    Object.assign(form, emptyForm())
    photo.value = null
    document.getElementById('workshop-photo').value = ''
    emit('registered')
  } catch (requestError) {
    error.value = requestError.response?.data?.message || 'No fue posible registrar el taller.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="client-registration" aria-labelledby="workshop-registration-title">
    <div class="section-heading client-heading">
      <div><p class="eyebrow">Gestion de talleres</p><h2 id="workshop-registration-title">Registrar taller</h2></div>
      <span class="form-note">Todos los campos son obligatorios</span>
    </div>
    <form class="client-form" @submit.prevent="registerWorkshop">
      <div class="form-section">
        <h3>Identificacion del taller</h3>
        <div class="form-grid">
          <label>Nombre del taller<input v-model.trim="form.name" class="form-control" maxlength="160" required /></label>
          <label>Razon social<input v-model.trim="form.businessName" class="form-control" maxlength="180" required /></label>
          <label>RFC<input v-model="form.rfc" class="form-control" maxlength="13" pattern="[A-Z&Ñ]{3,4}[0-9]{2}(0[1-9]|1[0-2])(0[1-9]|[12][0-9]|3[01])[A-Z0-9]{3}" required /></label>
          <label>Telefono<input v-model.trim="form.phone" class="form-control" type="tel" pattern="[0-9+() -]{7,25}" required /></label>
          <label>Gmail del taller<input v-model.trim="form.email" class="form-control" type="email" maxlength="160" required /></label>
          <label>Fotografia (maximo 15 MB)<input id="workshop-photo" class="form-control" type="file" accept="image/*" required @change="onPhotoChange" /></label>
        </div>
      </div>
      <div class="form-section">
        <h3>Direccion completa</h3>
        <div class="form-grid">
          <label>Estado<input v-model.trim="form.state" class="form-control" maxlength="120" required /></label>
          <label>Codigo postal<input v-model.trim="form.postalCode" class="form-control" inputmode="numeric" pattern="[0-9]{5}" maxlength="5" required /></label>
          <label>Municipio<input v-model.trim="form.municipality" class="form-control" maxlength="120" required /></label>
          <label>Colonia<input v-model.trim="form.neighborhood" class="form-control" maxlength="120" required /></label>
          <label>Calle<input v-model.trim="form.street" class="form-control" maxlength="160" required /></label>
        </div>
      </div>
      <div v-if="error" class="alert alert-danger mb-0" role="alert">{{ error }}</div>
      <div v-if="success" class="alert alert-success mb-0" role="status">{{ success }}</div>
      <button class="btn btn-danger register-client-button" type="submit" :disabled="loading">{{ loading ? 'Guardando taller...' : 'Guardar taller' }}</button>
    </form>
  </section>
</template>
