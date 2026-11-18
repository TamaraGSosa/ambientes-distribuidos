<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import PageBreadcrumb from '@/components/common/PageBreadcrumb.vue'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardFooter,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import {
  CalendarPlusIcon,
  FlagIcon,
  LoaderCircleIcon,
  OctagonAlertIcon,
  RegenerateIcon,
} from '@/icons/layout'
import { obtenerCanchas } from '@/services/api'
import type { Cancha } from '@/types/api'

const canchas = ref<Cancha[]>([])
const cargando = ref(true)
const error = ref<string | null>(null)

async function cargarCanchas() {
  cargando.value = true
  error.value = null
  try {
    canchas.value = await obtenerCanchas()
  } catch (err) {
    canchas.value = []
    error.value =
      err instanceof Error ? err.message : 'Ocurrió un error al consultar las canchas.'
  } finally {
    cargando.value = false
  }
}

function formatearPrecio(precio: number): string {
  return `$${precio.toLocaleString('es-AR')}`
}

onMounted(() => {
  cargarCanchas()
})
</script>

<template>
  <AdminLayout>
    <div>
      <PageBreadcrumb pageTitle="Canchas" />

      <div class="mb-6 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <p class="text-sm text-gray-500 dark:text-gray-400">
          Listado de canchas activas obtenidas de <code class="font-mono">GET /canchas</code>.
        </p>

        <Button variant="outline" size="sm" :disabled="cargando" @click="cargarCanchas">
          <LoaderCircleIcon v-if="cargando" class="size-4 animate-spin" />
          <RegenerateIcon v-else class="size-4" />
          Actualizar
        </Button>
      </div>

      <!-- Estado de carga -->
      <Card
        v-if="cargando"
        class="flex flex-col items-center justify-center rounded-2xl border border-gray-200 bg-white py-16 dark:border-gray-800 dark:bg-white/3"
      >
        <LoaderCircleIcon class="size-8 animate-spin text-brand-500" />
        <p class="mt-3 text-sm text-gray-500 dark:text-gray-400">Cargando canchas...</p>
      </Card>

      <!-- Estado de error -->
      <Alert v-else-if="error" variant="destructive" class="p-4">
        <OctagonAlertIcon class="size-5" />
        <AlertTitle>No se pudieron cargar las canchas</AlertTitle>
        <AlertDescription>
          <p>{{ error }}</p>
          <div class="mt-3">
            <Button variant="outline" size="sm" @click="cargarCanchas">Reintentar</Button>
          </div>
        </AlertDescription>
      </Alert>

      <!-- Estado de lista vacía -->
      <Card
        v-else-if="canchas.length === 0"
        class="flex flex-col items-center justify-center rounded-2xl border border-gray-200 bg-white py-16 text-center dark:border-gray-800 dark:bg-white/3"
      >
        <div
          class="mb-3 flex size-12 items-center justify-center rounded-full bg-gray-100 dark:bg-gray-800"
        >
          <FlagIcon class="size-6 text-gray-800 dark:text-white/90" />
        </div>
        <h4 class="text-base font-semibold text-gray-800 dark:text-white/90">
          No hay canchas registradas
        </h4>
        <p class="mt-1 text-sm text-gray-500 dark:text-gray-400">
          El servidor no devolvió canchas activas en este momento.
        </p>
      </Card>

      <!-- Grilla de tarjetas con datos reales -->
      <div v-else class="grid grid-cols-1 gap-6 sm:grid-cols-2 xl:grid-cols-3">
        <Card
          v-for="cancha in canchas"
          :key="cancha.id"
          class="flex flex-col justify-between rounded-2xl border border-gray-200 bg-white dark:border-gray-800 dark:bg-white/3"
        >
          <CardHeader class="border-b border-gray-100 pb-4 dark:border-gray-800">
            <div class="flex items-start justify-between gap-3">
              <div class="flex items-center gap-3">
                <div
                  class="flex size-11 shrink-0 items-center justify-center rounded-xl bg-brand-50 text-brand-500 dark:bg-brand-500/15 dark:text-brand-400"
                >
                  <FlagIcon class="size-5" />
                </div>
                <div>
                  <CardTitle class="text-lg font-semibold text-gray-800 dark:text-white/90">
                    {{ cancha.nombre }}
                  </CardTitle>
                  <CardDescription class="text-xs text-gray-500 dark:text-gray-400">
                    ID #{{ cancha.id }}
                  </CardDescription>
                </div>
              </div>

              <Badge :color="cancha.activa ? 'success' : 'error'">
                {{ cancha.activa ? 'Activa' : 'Inactiva' }}
              </Badge>
            </div>
          </CardHeader>

          <CardContent class="space-y-3 py-4">
            <div class="flex items-center justify-between text-sm">
              <span class="text-gray-500 dark:text-gray-400">Deporte</span>
              <span class="font-medium text-gray-800 dark:text-white/90">{{ cancha.deporte }}</span>
            </div>

            <div class="flex items-center justify-between text-sm">
              <span class="text-gray-500 dark:text-gray-400">Precio por hora</span>
              <span class="font-semibold text-gray-800 dark:text-white/90">
                {{ formatearPrecio(cancha.precio_hora) }}
              </span>
            </div>
          </CardContent>

          <CardFooter class="border-t border-gray-100 pt-4 dark:border-gray-800">
            <Button
              v-if="cancha.activa"
              :as="RouterLink"
              :to="`/reservas/nueva?cancha_id=${cancha.id}`"
              class="w-full"
            >
              <CalendarPlusIcon class="size-4" />
              Reservar
            </Button>
            <Button v-else disabled variant="outline" class="w-full">No disponible</Button>
          </CardFooter>
        </Card>
      </div>
    </div>
  </AdminLayout>
</template>

