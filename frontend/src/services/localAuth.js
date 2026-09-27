const USERS_KEY = 'taller_local_users'
const RESET_KEY = 'taller_local_reset_codes'

const demoUsers = [
  {
    id: 1,
    name: 'Gerente Principal',
    email: 'gerente@taller.com',
    password: 'Gerente123',
    role: 'GERENTE'
  },
  {
    id: 2,
    name: 'Mecanico Taller',
    email: 'mecanico@taller.com',
    password: 'Mecanico123',
    role: 'MECANICO'
  },
  {
    id: 3,
    name: 'Cliente Demo',
    email: 'cliente@taller.com',
    password: 'Cliente123',
    role: 'CLIENTE'
  }
]

function encodePassword(password) {
  return btoa(unescape(encodeURIComponent(password)))
}

function readUsers() {
  const stored = localStorage.getItem(USERS_KEY)
  if (stored) {
    return JSON.parse(stored)
  }

  const seeded = demoUsers.map((user) => ({
    ...user,
    passwordHash: encodePassword(user.password),
    password: undefined
  }))
  localStorage.setItem(USERS_KEY, JSON.stringify(seeded))
  return seeded
}

function writeUsers(users) {
  localStorage.setItem(USERS_KEY, JSON.stringify(users))
}

function readResetCodes() {
  return JSON.parse(localStorage.getItem(RESET_KEY) || '{}')
}

function writeResetCodes(codes) {
  localStorage.setItem(RESET_KEY, JSON.stringify(codes))
}

function publicUser(user) {
  return {
    token: `local-token-${user.id}-${Date.now()}`,
    userId: user.id,
    name: user.name,
    email: user.email,
    role: user.role
  }
}

export function localRegister({ name, email, password }) {
  const users = readUsers()
  const normalizedEmail = email.trim().toLowerCase()

  if (users.some((user) => user.email === normalizedEmail)) {
    throw new Error('El correo ya esta registrado.')
  }

  const user = {
    id: Date.now(),
    name: name.trim(),
    email: normalizedEmail,
    passwordHash: encodePassword(password),
    role: 'CLIENTE'
  }

  users.push(user)
  writeUsers(users)
  return publicUser(user)
}

export function localLogin({ email, password }) {
  const users = readUsers()
  const normalizedEmail = email.trim().toLowerCase()
  const user = users.find((item) => item.email === normalizedEmail)

  if (!user || user.passwordHash !== encodePassword(password)) {
    throw new Error('Credenciales invalidas.')
  }

  return publicUser(user)
}

export function localForgotPassword(email) {
  const users = readUsers()
  const normalizedEmail = email.trim().toLowerCase()
  const user = users.find((item) => item.email === normalizedEmail)
  const code = String(Math.floor(100000 + Math.random() * 900000))

  if (user) {
    const resetCodes = readResetCodes()
    resetCodes[normalizedEmail] = {
      code,
      expiresAt: Date.now() + 15 * 60 * 1000
    }
    writeResetCodes(resetCodes)
  }

  return {
    message: 'Si el correo existe, se genero un codigo de recuperacion.',
    developmentResetCode: user ? code : ''
  }
}

export function localResetPassword({ email, code, newPassword }) {
  const normalizedEmail = email.trim().toLowerCase()
  const resetCodes = readResetCodes()
  const reset = resetCodes[normalizedEmail]

  if (!reset || reset.code !== code || reset.expiresAt < Date.now()) {
    throw new Error('Codigo o correo invalido.')
  }

  const users = readUsers()
  const userIndex = users.findIndex((user) => user.email === normalizedEmail)
  if (userIndex < 0) {
    throw new Error('Codigo o correo invalido.')
  }

  users[userIndex].passwordHash = encodePassword(newPassword)
  writeUsers(users)
  delete resetCodes[normalizedEmail]
  writeResetCodes(resetCodes)

  return { message: 'Contrasena actualizada correctamente.' }
}
