<script setup>
import { computed, ref } from 'vue'
import LoginView from './views/LoginView.vue'
import DashboardView from './views/DashboardView.vue'

const storedUser = localStorage.getItem('taller_user')
const user = ref(storedUser ? JSON.parse(storedUser) : null)

const isAuthenticated = computed(() => Boolean(user.value))

function handleLoggedIn(authData) {
  localStorage.setItem('taller_token', authData.token)
  localStorage.setItem('taller_user', JSON.stringify(authData))
  user.value = authData
}

function logout() {
  localStorage.removeItem('taller_token')
  localStorage.removeItem('taller_user')
  user.value = null
}
</script>

<template>
  <main>
    <DashboardView v-if="isAuthenticated" :user="user" @logout="logout" />
    <LoginView v-else @logged-in="handleLoggedIn" />
  </main>
</template>
