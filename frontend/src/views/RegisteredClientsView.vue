<script setup>
import { onBeforeUnmount, onMounted, ref } from 'vue'
import api from '../services/api'

const clients = ref([])
const loading = ref(true)
const error = ref('')

function clearPhotoPreviews() {
  clients.value.forEach((client) => {
    if (client.photoPreview) URL.revokeObjectURL(client.photoPreview)
  })
}

async function loadClients() {
  loading.value = true
  error.value = ''
  clearPhotoPreviews()
  try {
    const { data } = await api.get('/clients')
    clients.value = await Promise.all(data.map(async (client) => {
      const photo = await api.get(client.photoUrl, { responseType: 'blob' })
      return { ...client, photoPreview: URL.createObjectURL(photo.data) }
    }))
  } catch (requestError) {
    error.value = requestError.response?.data?.message || 'No fue posible cargar los clientes registrados.'
  } finally {
    loading.value = false
  }
}

onMounted(loadClients)
onBeforeUnmount(clearPhotoPreviews)
</script>

<template>
  <section class="registered-clients" aria-labelledby="registered-clients-title">
    <div class="section-heading client-heading">
      <div>
        <p class="eyebrow">Base de datos</p>
        <h2 id="registered-clients-title">Clientes registrados</h2>
      </div>
      <button class="btn btn-outline-dark btn-sm" type="button" :disabled="loading" @click="loadClients">Actualizar</button>
    </div>

    <p v-if="loading" class="empty-clients">Cargando clientes...</p>
    <div v-else-if="error" class="alert alert-danger mb-0" role="alert">{{ error }}</div>
    <p v-else-if="clients.length === 0" class="empty-clients">Aun no hay clientes registrados.</p>
    <div v-else class="client-detail-grid">
      <article v-for="client in clients" :key="client.id" class="client-detail-card">
        <img class="client-photo" :src="client.photoPreview" :alt="`Fotografia de ${client.fullName}`" />
        <div class="client-detail-heading">
          <div>
            <p class="eyebrow">Folio #CL-{{ client.id }}</p>
            <h3>{{ client.fullName }}</h3>
          </div>
        </div>

        <dl class="client-data">
          <div><dt>Contacto alternativo</dt><dd>{{ client.alternateContactName }}</dd></div>
          <div><dt>Fecha de nacimiento</dt><dd>{{ client.birthDate }}</dd></div>
          <div><dt>Telefono personal</dt><dd>{{ client.personalPhone }}</dd></div>
          <div><dt>Telefono de trabajo</dt><dd>{{ client.workPhone }}</dd></div>
          <div><dt>Gmail personal</dt><dd>{{ client.personalEmail }}</dd></div>
          <div><dt>Gmail del trabajo</dt><dd>{{ client.workEmail }}</dd></div>
          <div class="client-address"><dt>Direccion</dt><dd>{{ client.street }}, {{ client.neighborhood }}, {{ client.municipality }}, {{ client.state }}, CP {{ client.postalCode }}</dd></div>
        </dl>
      </article>
    </div>
  </section>
</template>
