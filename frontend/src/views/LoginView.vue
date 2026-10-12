<script setup>
import { ref } from 'vue'
import api from '../services/api'

const emit = defineEmits(['logged-in'])

const mode = ref('login')
const username = ref('')
const email = ref('')
const role = ref('SECRETARIA')
const password = ref('')
const confirmPassword = ref('')
const accessKey = ref('')
const loading = ref(false)
const error = ref('')
const success = ref('')

function resetMessages() {
  error.value = ''
  success.value = ''
}

function changeMode(nextMode) {
  resetMessages()
  mode.value = nextMode
  password.value = ''
  confirmPassword.value = ''
  accessKey.value = ''
}

async function login() {
  resetMessages()
  loading.value = true
  try {
    const { data } = await api.post('/auth/login', {
      username: username.value.trim(),
      password: password.value,
      accessKey: accessKey.value
    })
    emit('logged-in', data)
  } catch (requestError) {
    error.value = requestError.response?.data?.message || 'No fue posible iniciar sesion.'
  } finally {
    loading.value = false
  }
}

async function register() {
  resetMessages()
  if (password.value !== confirmPassword.value) {
    error.value = 'La confirmacion de contrasena no coincide.'
    return
  }

  loading.value = true
  try {
    const { data } = await api.post('/auth/register', {
      username: username.value.trim(),
      email: email.value.trim(),
      role: role.value,
      accessKey: accessKey.value,
      password: password.value,
      confirmPassword: confirmPassword.value
    })
    success.value = data.message
    password.value = ''
    confirmPassword.value = ''
    accessKey.value = ''
    mode.value = 'login'
  } catch (requestError) {
    error.value = requestError.response?.data?.message || 'No fue posible completar el registro.'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="auth-page">
    <main class="auth-panel" aria-labelledby="auth-title">
      <header class="auth-header">
        <span class="brand-mark">TM</span>
        <div>
          <p class="brand-kicker">Gestion operativa</p>
          <h1 id="auth-title">Taller Mecanico</h1>
        </div>
      </header>

      <div class="auth-heading">
        <h2>{{ mode === 'login' ? 'Acceso al sistema' : 'Registro de personal' }}</h2>
        <p>{{ mode === 'login' ? 'Ingresa tus credenciales para continuar.' : 'Crea un acceso para Gerente o Recepcionista.' }}</p>
      </div>

      <form v-if="mode === 'login'" class="auth-form" @submit.prevent="login">
        <label class="form-label" for="username">Usuario</label>
        <input id="username" v-model.trim="username" class="form-control" autocomplete="username" minlength="3" required />

        <label class="form-label" for="password">Contrasena</label>
        <input id="password" v-model="password" class="form-control" type="password" autocomplete="current-password" minlength="8" required />

        <label class="form-label" for="access-key">Palabra clave</label>
        <input id="access-key" v-model="accessKey" class="form-control" type="password" autocomplete="off" minlength="8" required />

        <button class="btn btn-danger w-100" type="submit" :disabled="loading">
          {{ loading ? 'Validando acceso...' : 'Iniciar sesion' }}
        </button>
        <button class="link-button" type="button" @click="changeMode('register')">Registrar personal autorizado</button>
      </form>

      <form v-else class="auth-form" @submit.prevent="register">
        <label class="form-label" for="register-username">Usuario</label>
        <input id="register-username" v-model.trim="username" class="form-control" autocomplete="username" minlength="3" maxlength="60" required />

        <label class="form-label" for="register-email">Correo</label>
        <input id="register-email" v-model.trim="email" class="form-control" type="email" autocomplete="email" required />

        <label class="form-label" for="register-role">Puesto</label>
        <select id="register-role" v-model="role" class="form-select" required>
          <option value="ADMINISTRADOR">Administrador</option>
          <option value="SECRETARIA">Secretaria</option>
        </select>

        <label class="form-label" for="register-key">Palabra clave</label>
        <input id="register-key" v-model="accessKey" class="form-control" type="password" autocomplete="new-password" minlength="8" required />

        <label class="form-label" for="register-password">Contrasena</label>
        <input id="register-password" v-model="password" class="form-control" type="password" autocomplete="new-password" minlength="8" required />

        <label class="form-label" for="confirm-password">Confirmar contrasena</label>
        <input id="confirm-password" v-model="confirmPassword" class="form-control" type="password" autocomplete="new-password" minlength="8" required />

        <button class="btn btn-danger w-100" type="submit" :disabled="loading">
          {{ loading ? 'Creando acceso...' : 'Crear acceso' }}
        </button>
        <button class="link-button" type="button" @click="changeMode('login')">Volver al inicio de sesion</button>
      </form>

      <div v-if="error" class="alert alert-danger mt-3" role="alert">{{ error }}</div>
      <div v-if="success" class="alert alert-success mt-3" role="status">{{ success }}</div>
    </main>
  </section>
</template>
