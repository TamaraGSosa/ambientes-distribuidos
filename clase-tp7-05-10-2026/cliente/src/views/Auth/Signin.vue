<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink } from 'vue-router'
import AuthLayout from '@/components/layout/AuthLayout.vue'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Button } from '@/components/ui/button'
import { Field, FieldLabel } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import { ChevronLeftIcon, InfoCircleIcon } from '@/icons/layout'

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
              Iniciar sesión
            </h1>
            <p class="text-sm text-gray-500 dark:text-gray-400">
              Ingresá tu correo electrónico y contraseña para acceder al panel de ReservaCancha.
            </p>
          </div>

          <form class="space-y-5" @submit.prevent="avisoSinBackendAuth = true">
            <Alert v-if="avisoSinBackendAuth" variant="info" class="p-4">
              <InfoCircleIcon class="size-5" />
              <AlertTitle>Autenticación no implementada en el backend</AlertTitle>
              <AlertDescription>
                <p>
                  La API actual de ReservaCancha no dispone de un endpoint de autenticación de
                  usuarios. Podés acceder directamente al sistema desde el enlace al panel
                  principal.
                </p>
                <div class="mt-3">
                  <Button :as="RouterLink" to="/" variant="outline" size="sm">
                    Ir al panel principal
                  </Button>
                </div>
              </AlertDescription>
            </Alert>

            <Field>
              <FieldLabel class="mb-1.5 block">Correo electrónico</FieldLabel>
              <Input v-model="email" type="email" placeholder="usuario@ejemplo.com" />
            </Field>

            <Field>
              <FieldLabel class="mb-1.5 block">Contraseña</FieldLabel>
              <Input v-model="password" type="password" placeholder="Ingresá tu contraseña" />
            </Field>

            <Button class="w-full" size="lg" type="submit">Iniciar sesión</Button>
          </form>

          <div class="mt-5">
            <p class="text-center text-sm font-normal text-gray-700 sm:text-start dark:text-gray-400">
              ¿No tenés una cuenta?
              <RouterLink
                to="/signup"
                class="text-brand-500 hover:text-brand-600 dark:text-brand-400"
              >
                Registrarse
              </RouterLink>
            </p>
          </div>
        </div>
      </div>
    </div>
  </AuthLayout>
</template>
