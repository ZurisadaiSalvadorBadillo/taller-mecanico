<script setup>
import { onMounted, ref } from 'vue'
import api from '../services/api'
const workshops = ref([])
const error = ref('')
async function loadWorkshops() { try { workshops.value = (await api.get('/workshops')).data } catch (requestError) { error.value = requestError.response?.data?.message || 'No fue posible cargar los talleres.' } }
onMounted(loadWorkshops)
</script>
<template><section class="registered-clients"><div class="section-heading client-heading"><div><p class="eyebrow">Directorio</p><h2>Talleres registrados</h2></div><button class="btn btn-outline-dark btn-sm" @click="loadWorkshops">Actualizar</button></div><div v-if="error" class="alert alert-danger mt-3">{{ error }}</div><div v-else class="client-detail-grid"><article v-for="workshop in workshops" :key="workshop.id" class="client-detail-card"><div class="client-detail-heading"><p class="eyebrow">Taller #{{ workshop.id }}</p><h3>{{ workshop.name }}</h3></div><dl class="client-data"><div><dt>Razon social</dt><dd>{{ workshop.businessName }}</dd></div><div><dt>RFC</dt><dd>{{ workshop.rfc }}</dd></div><div><dt>Contacto</dt><dd>{{ workshop.email }}<br>{{ workshop.phone }}</dd></div><div class="client-address"><dt>Direccion</dt><dd>{{ workshop.street }}, {{ workshop.neighborhood }}, {{ workshop.locality }}, {{ workshop.municipality }}, {{ workshop.state }}, CP {{ workshop.postalCode }}</dd></div></dl></article></div></section></template>
