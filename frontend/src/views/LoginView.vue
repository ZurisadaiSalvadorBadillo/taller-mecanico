<script setup>
import { ref } from 'vue'
import api from '../services/api'
import { localForgotPassword, localLogin, localRegister, localResetPassword } from '../services/localAuth'

const emit = defineEmits(['logged-in'])

const mode = ref('login')
const name = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const code = ref('')
const newPassword = ref('')
const loading = ref(false)
const error = ref('')
const success = ref('')
const developmentResetCode = ref('')

async function login() {
  error.value = ''
  success.value = ''
  loading.value = true

  try {
    const { data } = await api.post('/auth/login', {
      email: email.value,
      password: password.value
    })
    emit('logged-in', data)
  } catch (err) {
    try {
      emit('logged-in', localLogin({ email: email.value, password: password.value }))
    } catch (localErr) {
      error.value = err.response?.data?.message || localErr.message || 'No se pudo iniciar sesion.'
    }
  } finally {
    loading.value = false
  }
}

async function register() {
  error.value = ''
  success.value = ''

  if (password.value !== confirmPassword.value) {
    error.value = 'Las contrasenas no coinciden.'
    return
  }

  loading.value = true

  try {
    await api.post('/auth/register', {
      name: name.value,
      email: email.value,
      password: password.value
    })

    const { data } = await api.post('/auth/login', {
      email: email.value,
      password: password.value
    })
    emit('logged-in', data)
  } catch (err) {
    try {
      emit('logged-in', localRegister({
        name: name.value,
        email: email.value,
        password: password.value
      }))
    } catch (localErr) {
      error.value = err.response?.data?.message || localErr.message || 'No se pudo completar el registro.'
    }
  } finally {
    loading.value = false
  }
}

async function requestResetCode() {
  error.value = ''
  success.value = ''
  developmentResetCode.value = ''
  loading.value = true

  try {
    const { data } = await api.post('/auth/forgot-password', { email: email.value })
    success.value = data.message
    developmentResetCode.value = data.developmentResetCode || ''
    mode.value = 'reset'
  } catch (err) {
    try {
      const data = localForgotPassword(email.value)
      success.value = data.message
      developmentResetCode.value = data.developmentResetCode || ''
      mode.value = 'reset'
    } catch (localErr) {
      error.value = err.response?.data?.message || localErr.message || 'No se pudo generar el codigo.'
    }
  } finally {
    loading.value = false
  }
}

async function resetPassword() {
  error.value = ''
  success.value = ''
  loading.value = true

  try {
    const { data } = await api.post('/auth/reset-password', {
      email: email.value,
      code: code.value,
      newPassword: newPassword.value
    })
    success.value = data.message
    password.value = ''
    code.value = ''
    newPassword.value = ''
    mode.value = 'login'
  } catch (err) {
    try {
      const data = localResetPassword({
        email: email.value,
        code: code.value,
        newPassword: newPassword.value
      })
      success.value = data.message
      password.value = ''
      code.value = ''
      newPassword.value = ''
      mode.value = 'login'
    } catch (localErr) {
      error.value = err.response?.data?.message || localErr.message || 'No se pudo actualizar la contrasena.'
    }
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <section class="auth-page">
    <div class="auth-panel">
      <div class="brand-strip">
        <span class="brand-mark">TM</span>
        <div>
          <p class="brand-kicker">Ordenes de reparacion</p>
          <h1>Taller Mecanico</h1>
        </div>
      </div>

      <div class="security-copy">
        <span>Seguridad</span>
        <span>Autenticacion</span>
        <span>Autorizacion</span>
      </div>

      <form v-if="mode === 'register'" class="auth-form" @submit.prevent="register">
        <label class="form-label" for="register-name">Nombre completo</label>
        <input id="register-name" v-model="name" class="form-control" type="text" autocomplete="name" required />

        <label class="form-label" for="register-email">Correo</label>
        <input id="register-email" v-model="email" class="form-control" type="email" autocomplete="email" required />

        <label class="form-label" for="register-password">Contrasena</label>
        <input id="register-password" v-model="password" class="form-control" type="password" autocomplete="new-password" minlength="8" required />

        <label class="form-label" for="confirm-password">Confirmar contrasena</label>
        <input id="confirm-password" v-model="confirmPassword" class="form-control" type="password" autocomplete="new-password" minlength="8" required />

        <button class="btn btn-danger w-100" type="submit" :disabled="loading">
          {{ loading ? 'Creando cuenta...' : 'Registrarme' }}
        </button>

        <button class="link-button" type="button" @click="mode = 'login'">
          Ya tengo cuenta
        </button>
      </form>

      <form v-if="mode === 'login'" class="auth-form" @submit.prevent="login">
        <label class="form-label" for="email">Correo</label>
        <input id="email" v-model="email" class="form-control" type="email" autocomplete="email" required />

        <label class="form-label" for="password">Contrasena</label>
        <input id="password" v-model="password" class="form-control" type="password" autocomplete="current-password" required />

        <button class="btn btn-danger w-100" type="submit" :disabled="loading">
          {{ loading ? 'Entrando...' : 'Iniciar sesion' }}
        </button>

        <div class="auth-actions">
          <button class="link-button" type="button" @click="mode = 'register'">
            Crear cuenta
          </button>
          <button class="link-button" type="button" @click="mode = 'forgot'">
            Recuperar contrasena
          </button>
        </div>
      </form>

      <form v-if="mode === 'forgot'" class="auth-form" @submit.prevent="requestResetCode">
        <label class="form-label" for="forgot-email">Correo registrado</label>
        <input id="forgot-email" v-model="email" class="form-control" type="email" autocomplete="email" required />

        <button class="btn btn-danger w-100" type="submit" :disabled="loading">
          {{ loading ? 'Generando...' : 'Generar codigo' }}
        </button>

        <button class="link-button" type="button" @click="mode = 'login'">
          Volver al login
        </button>
      </form>

      <form v-if="mode === 'reset'" class="auth-form" @submit.prevent="resetPassword">
        <label class="form-label" for="reset-code">Codigo de recuperacion</label>
        <input id="reset-code" v-model="code" class="form-control" inputmode="numeric" maxlength="6" required />

        <label class="form-label" for="new-password">Nueva contrasena</label>
        <input id="new-password" v-model="newPassword" class="form-control" type="password" autocomplete="new-password" required />

        <button class="btn btn-danger w-100" type="submit" :disabled="loading">
          {{ loading ? 'Actualizando...' : 'Cambiar contrasena' }}
        </button>
      </form>

      <div v-if="error" class="alert alert-danger mt-3" role="alert">{{ error }}</div>
      <div v-if="success" class="alert alert-light border mt-3" role="status">{{ success }}</div>
      <div v-if="developmentResetCode" class="dev-code mt-3">
        Codigo temporal de prueba: <strong>{{ developmentResetCode }}</strong>
      </div>

      <div class="demo-users">
        <p>Usuarios demo</p>
        <span>gerente@taller.com / Gerente123</span>
        <span>mecanico@taller.com / Mecanico123</span>
        <span>cliente@taller.com / Cliente123</span>
      </div>
    </div>
  </section>
</template>
