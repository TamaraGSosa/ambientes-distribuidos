import { computed, ref } from 'vue'

export interface PersonalInfo {
  avatarUrl: string
  fullName: string
  role: string
  location: string
  firstName: string
  lastName: string
  email: string
  phone: string
  bio: string
}

const STORAGE_KEY = 'reservacancha_perfil_usuario'

const defaultNeutralProfile: PersonalInfo = {
  avatarUrl: '',
  fullName: 'Usuario',
  role: 'Operador del sistema',
  location: 'ReservaCancha',
  firstName: 'Usuario',
  lastName: '',
  email: '',
  phone: '',
  bio: '',
}

function loadStoredProfile(): PersonalInfo {
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (!raw) return { ...defaultNeutralProfile }
    const parsed = JSON.parse(raw) as Partial<PersonalInfo>
    return {
      ...defaultNeutralProfile,
      ...parsed,
    }
  } catch {
    return { ...defaultNeutralProfile }
  }
}

const profileState = ref<PersonalInfo>(loadStoredProfile())

export function useUserProfile() {
  const displayName = computed(() => {
    const full = `${profileState.value.firstName ?? ''} ${profileState.value.lastName ?? ''}`.trim()
    return full || profileState.value.fullName?.trim() || 'Usuario'
  })

  const shortName = computed(() => {
    return profileState.value.firstName?.trim() || 'Usuario'
  })

  const initialLetter = computed(() => {
    const base = shortName.value
    return base ? base.charAt(0).toUpperCase() : 'U'
  })

  function actualizarPerfil(nuevoPerfil: PersonalInfo) {
    const full = `${nuevoPerfil.firstName ?? ''} ${nuevoPerfil.lastName ?? ''}`.trim() || 'Usuario'
    const actualizado: PersonalInfo = {
      ...nuevoPerfil,
      fullName: full,
    }
    profileState.value = actualizado
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(actualizado))
    } catch {
      // Ignorar errores de almacenamiento local
    }
  }

  function limpiarSesionLocal() {
    profileState.value = { ...defaultNeutralProfile }
    try {
      localStorage.removeItem(STORAGE_KEY)
    } catch {
      // Ignorar errores de almacenamiento local
    }
  }

  return {
    profileState,
    displayName,
    shortName,
    initialLetter,
    actualizarPerfil,
    limpiarSesionLocal,
  }
}
