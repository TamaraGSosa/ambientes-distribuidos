<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink } from 'vue-router'
import AuthLayout from '@/components/layout/AuthLayout.vue'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { Field, FieldLabel } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { ChevronLeftIcon, InfoCircleIcon } from '@/icons/layout'

const nombre = ref('')
const apellido = ref('')
const email = ref('')
const password = ref('')
const avisoSinBackendAuth = ref(false)
</script>

<template>
  <AuthLayout>
    <div class="flex w-full flex-1 flex-col lg:w-1/2">
      <div class="mx-auto mb-5 w-full max-w-md sm:pt-10">
        <RouterLink
          to="/"
          class="inline-flex items-center text-sm text-gray-500 transition-colors hover:text-gray-700 dark:text-gray-400 dark:hover:text-gray-300"
        >
          <ChevronLeftIcon />
          Volver al panel principal
        </RouterLink>
      </div>

      <div class="mx-auto flex w-full max-w-md flex-1 flex-col justify-center">
        <div>
          <div class="mb-5 sm:mb-8">
            <h1
              class="mb-2 text-title-sm font-semibold text-gray-800 sm:text-title-md dark:text-white/90"
            >
              Registrarse
            </h1>
            <p class="text-sm text-gray-500 dark:text-gray-400">
              Completá tus datos para registrarte en ReservaCancha.
            </p>
          </div>

          <form class="space-y-5" @submit.prevent="avisoSinBackendAuth = true">
            <Alert v-if="avisoSinBackendAuth" variant="info" class="p-4">
              <InfoCircleIcon class="size-5" />
              <AlertTitle>Registro de usuarios no implementado en el backend</AlertTitle>
              <AlertDescription>
                <p>
                  La API actual de ReservaCancha no dispone de un endpoint de registro de usuarios.
                  Podés utilizar todas las funciones de canchas y reservas directamente desde el
                  panel principal.
                </p>
                <div class="mt-3">
                  <Button :as="RouterLink" to="/" variant="outline" size="sm">
                    Ir al panel principal
                  </Button>
                </div>
              </AlertDescription>
            </Alert>

            <div class="grid grid-cols-1 gap-5 sm:grid-cols-2">
              <Field>
                <FieldLabel class="mb-1.5 block">Nombre</FieldLabel>
                <Input v-model="nombre" type="text" placeholder="Ingresá tu nombre" />
              </Field>
              <Field>
                <FieldLabel class="mb-1.5 block">Apellido</FieldLabel>
                <Input v-model="apellido" type="text" placeholder="Ingresá tu apellido" />
              </Field>
            </div>

            <Field>
              <FieldLabel class="mb-1.5 block">Correo electrónico</FieldLabel>
              <Input v-model="email" type="email" placeholder="usuario@ejemplo.com" />
            </Field>

            <Field>
              <FieldLabel class="mb-1.5 block">Contraseña</FieldLabel>
              <Input v-model="password" type="password" placeholder="Ingresá tu contraseña" />
            </Field>

            <Button class="w-full" size="lg" type="submit">Registrarse</Button>
          </form>

          <div class="mt-5">
            <p class="text-center text-sm font-normal text-gray-700 sm:text-start dark:text-gray-400">
              ¿Ya tenés una cuenta?
              <RouterLink
                to="/signin"
                class="text-brand-500 hover:text-brand-600 dark:text-brand-400"
              >
                Iniciar sesión
              </RouterLink>
            </p>
          </div>
        </div>
      </div>
    </div>
  </AuthLayout>
</template>
