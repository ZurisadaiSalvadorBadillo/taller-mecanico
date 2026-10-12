<script setup>
import { onMounted, reactive, ref } from 'vue'
import api from '../services/api'
const statuses = ref([]), error = ref(''), editingId = ref(null)
const form = reactive({ value: 'ACTIVO', description: '' })
async function load() { try { statuses.value = (await api.get('/user-statuses')).data } catch (e) { error.value = e.response?.data?.message || 'No fue posible cargar estatus.' } }
function edit(status) { editingId.value = status.id; form.value = status.value; form.description = status.description }
async function save() { try { if (editingId.value) await api.put(`/user-statuses/${editingId.value}`, form); else await api.post('/user-statuses', form); editingId.value = null; form.value = 'ACTIVO'; form.description = ''; load() } catch (e) { error.value = e.response?.data?.message || 'No fue posible guardar el estatus.' } }
async function remove(id) { if (!window.confirm('Eliminar logicamente este estatus?')) return; try { await api.delete(`/user-statuses/${id}`); load() } catch (e) { error.value = e.response?.data?.message || 'No fue posible eliminar.' } }
onMounted(load)
</script>
<template><section class="client-registration"><div class="section-heading client-heading"><div><p class="eyebrow">Catalogo</p><h2>Catalogo de estatus</h2></div></div><form class="client-form" @submit.prevent="save"><div class="form-section"><div class="form-grid"><label>Valor<select v-model="form.value" class="form-select"><option>ACTIVO</option><option>SUSPENDIDO</option><option>CANCELADO</option><option>ELIMINADO</option></select></label><label>Descripcion<input v-model.trim="form.description" class="form-control" required /></label></div></div><button class="btn btn-danger">{{ editingId ? 'Actualizar estatus' : 'Crear estatus' }}</button></form><p v-if="error" class="alert alert-danger mt-3">{{ error }}</p><div class="clients-table table-responsive"><table class="table"><thead><tr><th>Valor</th><th>Descripcion</th><th>Actualizado</th><th></th></tr></thead><tbody><tr v-for="status in statuses" :key="status.id"><td>{{ status.value }}</td><td>{{ status.description }}</td><td>{{ status.updatedAt }}</td><td><button class="btn btn-outline-dark btn-sm" @click="edit(status)">Editar</button> <button class="btn btn-danger btn-sm" @click="remove(status.id)">Eliminar</button></td></tr></tbody></table></div></section></template>
