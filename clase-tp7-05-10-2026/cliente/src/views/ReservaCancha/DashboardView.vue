<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import ComponentCard from '@/components/common/ComponentCard.vue'
import MetricCard from '@/components/common/MetricCard.vue'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import {
  ArrowRightIcon,
  CalendarIcon,
  CalendarPlusIcon,
  CheckCircleIcon,
  FlagIcon,
  LoaderCircleIcon,
  OctagonAlertIcon,
  RegenerateIcon,
  SystemIcon,
} from '@/icons/layout'
import { obtenerCanchas, obtenerReservas, verificarSalud } from '@/services/api'
import type { SaludResponse } from '@/types/api'

const cargando = ref(true)
const errorGeneral = ref<string | null>(null)

const salud = ref<SaludResponse | null>(null)
const errorSalud = ref<string | null>(null)

const totalCanchas = ref<number | null>(null)
const errorCanchas = ref<string | null>(null)

const totalReservas = ref<number | null>(null)
const errorReservas = ref<string | null>(null)

async function cargarDatosDashboard() {
  cargando.value = true
  errorGeneral.value = null
  errorSalud.value = null
  errorCanchas.value = null
  errorReservas.value = null

  const [resSalud, resCanchas, resReservas] = await Promise.allSettled([
    verificarSalud(),
    obtenerCanchas(),
    obtenerReservas(),
  ])

  if (resSalud.status === 'fulfilled') {
    salud.value = resSalud.value
  } else {
    salud.value = null
    errorSalud.value =
      resSalud.reason instanceof Error
        ? resSalud.reason.message
        : 'No se pudo consultar GET /salud'
  }

  if (resCanchas.status === 'fulfilled') {
    totalCanchas.value = resCanchas.value.length
  } else {
    totalCanchas.value = null
    errorCanchas.value =
      resCanchas.reason instanceof Error
        ? resCanchas.reason.message
        : 'No se pudo consultar GET /canchas'
  }

  if (resReservas.status === 'fulfilled') {
    totalReservas.value = resReservas.value.length
  } else {
    totalReservas.value = null
    errorReservas.value =
      resReservas.reason instanceof Error
        ? resReservas.reason.message
        : 'No se pudo consultar GET /reservas'
  }

  const errores = [errorSalud.value, errorCanchas.value, errorReservas.value].filter(Boolean)
  if (errores.length > 0) {
    errorGeneral.value = errores.join(' — ')
  }

  cargando.value = false
}

onMounted(() => {
  cargarDatosDashboard()
})
</script>

<template>
  <AdminLayout>
    <div class="space-y-6">
      <!-- Encabezado -->
      <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h2 class="text-xl font-semibold text-gray-800 dark:text-white/90">Panel principal</h2>
          <p class="mt-1 text-sm text-gray-500 dark:text-gray-400">
            Resumen general del sistema ReservaCancha y estado del servicio en tiempo real.
          </p>
        </div>

        <div class="flex flex-wrap items-center gap-3">
          <Button variant="outline" :disabled="cargando" @click="cargarDatosDashboard">
            <LoaderCircleIcon v-if="cargando" class="size-4 animate-spin" />
            <RegenerateIcon v-else class="size-4" />
            Actualizar
          </Button>

          <Button :as="RouterLink" to="/reservas/nueva">
            <CalendarPlusIcon class="size-5" />
            Nueva reserva
          </Button>
        </div>
      </div>

      <!-- Alerta de error si falla alguna consulta -->
      <Alert v-if="errorGeneral" variant="destructive" class="p-4">
        <OctagonAlertIcon class="size-5" />
        <AlertTitle>Error al consultar la API</AlertTitle>
        <AlertDescription>
          <p>{{ errorGeneral }}</p>
        </AlertDescription>
      </Alert>

      <!-- Tarjetas de indicadores reales -->
      <div class="grid grid-cols-1 gap-4 sm:grid-cols-2 xl:grid-cols-3 md:gap-6">
        <!-- Indicador de conexión con GET /salud -->
        <MetricCard
          title="Conexión con el Backend (GET /salud)"
          :value="
            cargando
              ? 'Verificando...'
              : salud?.estado === 'ok'
                ? salud.servicio
                : 'Sin conexión'
          "
        >
          <template #icon>
            <SystemIcon />
          </template>
          <template #badge>
            <Badge v-if="cargando" color="gray">Consultando</Badge>
            <Badge v-else-if="salud?.estado === 'ok'" color="success">
              <CheckCircleIcon />
              estado: {{ salud.estado }}
            </Badge>
            <Badge v-else color="error">
              <OctagonAlertIcon />
              Desconectado
            </Badge>
          </template>
        </MetricCard>

        <!-- Cantidad de canchas con GET /canchas -->
        <MetricCard
          title="Canchas Activas (GET /canchas)"
          :value="cargando ? '...' : totalCanchas !== null ? totalCanchas : '—'"
        >
          <template #icon>
            <FlagIcon />
          </template>
          <template #badge>
            <RouterLink to="/canchas">
              <Badge color="primary" class="cursor-pointer">
                Ver canchas
                <ArrowRightIcon />
              </Badge>
            </RouterLink>
          </template>
        </MetricCard>

        <!-- Cantidad de reservas con GET /reservas -->
        <MetricCard
          title="Reservas Registradas (GET /reservas)"
          :value="cargando ? '...' : totalReservas !== null ? totalReservas : '—'"
        >
          <template #icon>
            <CalendarIcon />
          </template>
          <template #badge>
            <RouterLink to="/reservas">
              <Badge color="primary" class="cursor-pointer">
                Ver reservas
                <ArrowRightIcon />
              </Badge>
            </RouterLink>
          </template>
        </MetricCard>
      </div>

      <!-- Accesos rápidos -->
      <ComponentCard
        title="Accesos rápidos"
        desc="Navegación directa a las secciones principales del sistema."
      >
        <div class="grid grid-cols-1 gap-4 md:grid-cols-3">
          <div
            class="flex flex-col justify-between rounded-xl border border-gray-200 p-5 dark:border-gray-800"
          >
            <div>
              <div class="flex items-center gap-3">
                <div
                  class="flex size-10 items-center justify-center rounded-lg bg-brand-50 text-brand-500 dark:bg-brand-500/15 dark:text-brand-400"
                >
                  <FlagIcon class="size-5" />
                </div>
                <h4 class="text-base font-semibold text-gray-800 dark:text-white/90">Canchas</h4>
              </div>
              <p class="mt-3 text-sm text-gray-500 dark:text-gray-400">
                Consultar el listado de canchas activas registradas en el sistema.
              </p>
            </div>
            <div class="mt-5">
              <Button :as="RouterLink" to="/canchas" variant="outline" class="w-full">
                Ir a Canchas
                <ArrowRightIcon class="size-4" />
              </Button>
            </div>
          </div>

          <div
            class="flex flex-col justify-between rounded-xl border border-gray-200 p-5 dark:border-gray-800"
          >
            <div>
              <div class="flex items-center gap-3">
                <div
                  class="flex size-10 items-center justify-center rounded-lg bg-brand-50 text-brand-500 dark:bg-brand-500/15 dark:text-brand-400"
                >
                  <CalendarIcon class="size-5" />
                </div>
                <h4 class="text-base font-semibold text-gray-800 dark:text-white/90">Reservas</h4>
              </div>
              <p class="mt-3 text-sm text-gray-500 dark:text-gray-400">
                Visualizar todas las reservas existentes y consultar su detalle por ID.
              </p>
            </div>
            <div class="mt-5">
              <Button :as="RouterLink" to="/reservas" variant="outline" class="w-full">
                Ir a Reservas
                <ArrowRightIcon class="size-4" />
              </Button>
            </div>
          </div>

          <div
            class="flex flex-col justify-between rounded-xl border border-gray-200 p-5 dark:border-gray-800"
          >
            <div>
              <div class="flex items-center gap-3">
                <div
                  class="flex size-10 items-center justify-center rounded-lg bg-brand-50 text-brand-500 dark:bg-brand-500/15 dark:text-brand-400"
                >
                  <CalendarPlusIcon class="size-5" />
                </div>
                <h4 class="text-base font-semibold text-gray-800 dark:text-white/90">
                  Nueva reserva
                </h4>
              </div>
              <p class="mt-3 text-sm text-gray-500 dark:text-gray-400">
                Registrar una nueva reserva seleccionando cancha, cliente, fecha y horario.
              </p>
            </div>
            <div class="mt-5">
              <Button :as="RouterLink" to="/reservas/nueva" class="w-full">
                Crear reserva
                <CalendarPlusIcon class="size-4" />
              </Button>
            </div>
          </div>
        </div>
      </ComponentCard>
    </div>
  </AdminLayout>
</template>

