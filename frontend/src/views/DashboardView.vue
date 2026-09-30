<script setup>
import { computed, ref } from 'vue'
import ClientRegistrationForm from './ClientRegistrationForm.vue'
import RegisteredClientsView from './RegisteredClientsView.vue'

const props = defineProps({
  user: {
    type: Object,
    required: true
  }
})

defineEmits(['logout'])

const roleMessage = computed(() => {
  const role = props.user.role
  if (role === 'GERENTE') return 'Acceso completo: usuarios, ordenes, reportes y permisos.'
  if (role === 'RECEPCIONISTA') return 'Puedes registrar clientes y mantener actualizados sus datos de contacto.'
  if (role === 'MECANICO') return 'Puedes actualizar el estatus de los vehiculos asignados.'
  return 'Puedes consultar el estatus de tu vehiculo y el historial de servicio.'
})

const canRegisterClients = computed(() => ['GERENTE', 'RECEPCIONISTA'].includes(props.user.role))
const clientsVersion = ref(0)

const platformOptions = computed(() => {
  const shared = [
    {
      title: 'Ordenes de reparacion',
      text: 'Alta de orden, diagnostico, evidencias, refacciones, mano de obra y cierre.',
      tag: 'Prioridad alta'
    },
    {
      title: 'Seguimiento de vehiculo',
      text: 'Estados: recibido, diagnostico, autorizado, en reparacion, prueba y entregado.',
      tag: 'Cliente'
    },
    {
      title: 'Notificaciones',
      text: 'Avisos por correo o WhatsApp para autorizaciones, avances y entrega.',
      tag: 'Comunicacion'
    }
  ]

  if (props.user.role === 'CLIENTE') {
    return shared
  }

  return [
    ...shared,
    {
      title: 'Inventario',
      text: 'Control de refacciones, proveedores, costos, existencias minimas y compras.',
      tag: 'Almacen'
    },
    {
      title: 'Agenda de taller',
      text: 'Citas, asignacion de mecanicos, bahias disponibles y tiempos estimados.',
      tag: 'Operacion'
    },
    {
      title: 'Reportes',
      text: 'Ingresos, productividad, ordenes vencidas, garantias y rentabilidad.',
      tag: 'Gerencia'
    }
  ]
})
</script>

<template>
  <section class="dashboard-page">
    <nav class="topbar">
      <div class="brand-inline">
        <span class="brand-mark">TM</span>
        <strong>Taller Mecanico</strong>
      </div>
      <button class="btn btn-outline-light btn-sm" type="button" @click="$emit('logout')">Salir</button>
    </nav>

    <div class="dashboard-shell">
      <aside class="sidebar">
        <button class="nav-button active" type="button">Ordenes</button>
        <button class="nav-button" type="button">Vehiculos</button>
        <button class="nav-button" type="button">Usuarios</button>
        <button class="nav-button" type="button">Reportes</button>
      </aside>

      <section class="workspace">
        <div class="welcome-row">
          <div>
            <p class="eyebrow">Sesion segura</p>
            <h1>Hola, {{ user.name }}</h1>
            <p>{{ roleMessage }}</p>
          </div>
          <span class="role-badge">{{ user.role }}</span>
        </div>

        <div class="status-grid">
          <article>
            <span>Ordenes abiertas</span>
            <strong>12</strong>
          </article>
          <article>
            <span>En diagnostico</span>
            <strong>5</strong>
          </article>
          <article>
            <span>Listos para entrega</span>
            <strong>3</strong>
          </article>
        </div>

        <ClientRegistrationForm v-if="canRegisterClients" @registered="clientsVersion += 1" />
        <RegisteredClientsView v-if="canRegisterClients" :key="clientsVersion" />

        <div class="section-heading">
          <div>
            <p class="eyebrow">Modulos propuestos</p>
            <h2>Opciones para implementar</h2>
          </div>
        </div>

        <div class="module-grid">
          <article v-for="option in platformOptions" :key="option.title" class="module-card">
            <span>{{ option.tag }}</span>
            <h3>{{ option.title }}</h3>
            <p>{{ option.text }}</p>
          </article>
        </div>

        <div class="orders-table">
          <div class="table-title">
            <h2>Ordenes recientes</h2>
            <button class="btn btn-danger btn-sm" type="button">Nueva orden</button>
          </div>
          <div class="table-responsive">
            <table class="table align-middle">
              <thead>
                <tr>
                  <th>Orden</th>
                  <th>Vehiculo</th>
                  <th>Cliente</th>
                  <th>Estatus</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>#OR-1042</td>
                  <td>Nissan Versa 2020</td>
                  <td>Cliente Demo</td>
                  <td><span class="status-pill">Diagnostico</span></td>
                </tr>
                <tr>
                  <td>#OR-1041</td>
                  <td>Chevrolet Aveo 2018</td>
                  <td>Maria Lopez</td>
                  <td><span class="status-pill ready">Listo</span></td>
                </tr>
                <tr>
                  <td>#OR-1040</td>
                  <td>Toyota Hilux 2021</td>
                  <td>Carlos Ruiz</td>
                  <td><span class="status-pill repair">Reparacion</span></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </section>
    </div>
  </section>
</template>
