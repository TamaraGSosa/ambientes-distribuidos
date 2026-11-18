<script setup lang="ts">
import { ref } from 'vue'
import PageBreadcrumb from '@/components/common/PageBreadcrumb.vue'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import { Field, FieldGroup, FieldLabel } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { useUserProfile, type PersonalInfo } from '@/composables/useUserProfile'
import { CheckCircleIcon, InfoCircleIcon } from '@/icons/layout'

const { profileState, displayName, initialLetter, actualizarPerfil } = useUserProfile()

const editando = ref(false)
const guardadoExitoso = ref(false)
const form = ref<PersonalInfo>({ ...profileState.value })

function iniciarEdicion() {
  form.value = { ...profileState.value }
  guardadoExitoso.value = false
  editando.value = true
}

function guardarCambios() {
  actualizarPerfil({ ...form.value })
  editando.value = false
  guardadoExitoso.value = true
}
</script>

<template>
  <AdminLayout>
    <div>
      <PageBreadcrumb page-title="Editar perfil" />

      <Alert variant="info" class="mb-6 p-4">
        <InfoCircleIcon class="size-5" />
        <AlertTitle>Almacenamiento local de perfil</AlertTitle>
        <AlertDescription>
          <p>
            El backend actual de ReservaCancha no incluye endpoints de gestión de usuarios. Los
            datos que edites en esta pantalla se guardan localmente en tu navegador durante la
            sesión y se reflejan en el menú superior.
          </p>
        </AlertDescription>
      </Alert>

      <Alert v-if="guardadoExitoso" variant="success" class="mb-6 p-4">
        <CheckCircleIcon class="size-5" />
        <AlertTitle>Perfil actualizado</AlertTitle>
        <AlertDescription>
          <p>Tus datos de perfil se actualizaron localmente en esta sesión.</p>
        </AlertDescription>
      </Alert>

      <Card class="border border-gray-200 p-5 sm:p-6 lg:p-7 dark:border-gray-800">
        <div class="flex flex-wrap items-center justify-between gap-4">
          <div class="flex items-center gap-4 sm:gap-6">
            <Avatar class="size-16 shrink-0 sm:size-20">
              <AvatarImage
                v-if="profileState.avatarUrl"
                :src="profileState.avatarUrl"
                :alt="displayName"
              />
              <AvatarFallback
                class="bg-brand-500 text-xl font-medium text-white sm:text-2xl dark:bg-brand-500 dark:text-white"
              >
                {{ initialLetter }}
              </AvatarFallback>
            </Avatar>

            <div class="flex flex-col gap-1">
              <h3 class="text-base font-semibold text-gray-800 sm:text-lg dark:text-white/90">
                {{ displayName }}
              </h3>
              <div
                class="flex flex-wrap items-center gap-2 text-xs text-gray-500 sm:text-sm dark:text-gray-400"
              >
                <span>{{ profileState.role || 'Operador del sistema' }}</span>
                <span class="inline-block h-3.5 w-px bg-gray-300 dark:bg-gray-700" />
                <span>{{ profileState.location || 'ReservaCancha' }}</span>
              </div>
            </div>
          </div>

          <Button v-if="!editando" type="button" variant="outline" size="sm" @click="iniciarEdicion">
            Editar información
          </Button>
        </div>

        <div v-if="!editando" class="mt-6 grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-3">
          <div>
            <p class="mb-1 text-xs text-gray-500 dark:text-gray-400">Nombre</p>
            <p class="text-sm font-medium text-gray-800 dark:text-white/90">
              {{ profileState.firstName || '—' }}
            </p>
          </div>
          <div>
            <p class="mb-1 text-xs text-gray-500 dark:text-gray-400">Apellido</p>
            <p class="text-sm font-medium text-gray-800 dark:text-white/90">
              {{ profileState.lastName || '—' }}
            </p>
          </div>
          <div>
            <p class="mb-1 text-xs text-gray-500 dark:text-gray-400">Correo electrónico</p>
            <p class="text-sm font-medium text-gray-800 dark:text-white/90">
              {{ profileState.email || '—' }}
            </p>
          </div>
          <div>
            <p class="mb-1 text-xs text-gray-500 dark:text-gray-400">Teléfono</p>
            <p class="text-sm font-medium text-gray-800 dark:text-white/90">
              {{ profileState.phone || '—' }}
            </p>
          </div>
          <div class="sm:col-span-2">
            <p class="mb-1 text-xs text-gray-500 dark:text-gray-400">Rol / Descripción</p>
            <p class="text-sm font-medium text-gray-800 dark:text-white/90">
              {{ profileState.bio || profileState.role || '—' }}
            </p>
          </div>
        </div>

        <form v-else class="mt-6 space-y-5" @submit.prevent="guardarCambios">
          <FieldGroup class="grid grid-cols-1 gap-5 sm:grid-cols-2">
            <Field>
              <FieldLabel>Nombre</FieldLabel>
              <Input v-model="form.firstName" type="text" placeholder="Nombre" />
            </Field>
            <Field>
              <FieldLabel>Apellido</FieldLabel>
              <Input v-model="form.lastName" type="text" placeholder="Apellido" />
            </Field>
            <Field>
              <FieldLabel>Correo electrónico</FieldLabel>
              <Input v-model="form.email" type="email" placeholder="usuario@ejemplo.com" />
            </Field>
            <Field>
              <FieldLabel>Teléfono</FieldLabel>
              <Input v-model="form.phone" type="text" placeholder="+54 9 ..." />
            </Field>
            <Field class="sm:col-span-2">
              <FieldLabel>Rol / Descripción</FieldLabel>
              <Input v-model="form.bio" type="text" placeholder="Operador del sistema" />
            </Field>
          </FieldGroup>

          <div class="flex items-center justify-end gap-3 pt-2">
            <Button type="button" variant="outline" @click="editando = false">Cancelar</Button>
            <Button type="submit">Guardar cambios</Button>
          </div>
        </form>
      </Card>
    </div>
  </AdminLayout>
</template>
