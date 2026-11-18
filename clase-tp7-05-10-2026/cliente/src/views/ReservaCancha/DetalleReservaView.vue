<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import ComponentCard from '@/components/common/ComponentCard.vue'
import PageBreadcrumb from '@/components/common/PageBreadcrumb.vue'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card } from '@/components/ui/card'
import {
  ArrowLeftIcon,
  CalendarIcon,
  CalendarPlusIcon,
  FlagIcon,
  LoaderCircleIcon,
  OctagonAlertIcon,
  TimeIcon,
  UserIcon,
} from '@/icons/layout'
import { obtenerCanchas, obtenerReservaPorId } from '@/services/api'
import type { Cancha, Reserva } from '@/types/api'

const route = useRoute()

const reserva = ref<Reserva | null>(null)
const canchaAsociada = ref<Cancha | null>(null)
const cargando = ref(true)
const error = ref<string | null>(null)

const reservaId = computed(() => Number(route.params.id))

async function cargarDetalle() {
  cargando.value = true
  error.value = null
  reserva.value = null
  canchaAsociada.value = null

  if (!Number.isInteger(reservaId.value) || reservaId.value <= 0) {
    error.value = 'El identificador de la reserva no es válido.'
    cargando.value = false
    return
  }

  try {
    const [datosReserva, listaCanchas] = await Promise.all([
      obtenerReservaPorId(reservaId.value),
      obtenerCanchas().catch(() => [] as Cancha[]),
    ])
    reserva.value = datosReserva
    canchaAsociada.value =
      listaCanchas.find((c) => c.id === datosReserva.cancha_id) ?? null
  } catch (err) {
    error.value =
      err instanceof Error ? err.message : 'No se pudo obtener el detalle de la reserva.'
  } finally {
    cargando.value = false
  }
}

watch(
  () => route.params.id,
  () => {
    cargarDetalle()
  },
)

onMounted(() => {
  cargarDetalle()
})
</script>

<template>
  <AdminLayout>
    <div>
      <PageBreadcrumb :pageTitle="`Detalle de Reserva #${route.params.id}`" />

      <div class="mx-auto max-w-3xl space-y-6">
        <div class="flex flex-wrap items-center justify-between gap-3">
          <Button :as="RouterLink" to="/reservas" variant="outline" size="sm">
            <ArrowLeftIcon class="size-4" />
            Volver al listado de reservas
          </Button>

          <Button :as="RouterLink" to="/reservas/nueva" size="sm">
            <CalendarPlusIcon class="size-4" />
            Nueva reserva
          </Button>
        </div>

        <!-- Estado de carga -->
        <Card
          v-if="cargando"
          class="flex flex-col items-center justify-center rounded-2xl border border-gray-200 bg-white py-16 dark:border-gray-800 dark:bg-white/3"
        >
          <LoaderCircleIcon class="size-8 animate-spin text-brand-500" />
          <p class="mt-3 text-sm text-gray-500 dark:text-gray-400">
            Consultando <code class="font-mono">GET /reservas/{{ route.params.id }}</code>...
          </p>
        </Card>

        <!-- Estado de error -->
        <Alert v-else-if="error" variant="destructive" class="p-4">
          <OctagonAlertIcon class="size-5" />
          <AlertTitle>No se pudo cargar la reserva</AlertTitle>
          <AlertDescription>
            <p>{{ error }}</p>
            <div class="mt-3 flex items-center gap-3">
              <Button variant="outline" size="sm" @click="cargarDetalle">Reintentar</Button>
              <Button :as="RouterLink" to="/reservas" variant="outline" size="sm">
                Ir a Reservas
              </Button>
            </div>
          </AlertDescription>
        </Alert>

        <!-- Detalle real de la reserva -->
        <ComponentCard
          v-else-if="reserva"
          :title="`Reserva #${reserva.id}`"
          :desc="`Información obtenida desde GET /reservas/${reserva.id}`"
        >
          <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
            <div class="rounded-xl border border-gray-200 p-4 dark:border-gray-800">
              <div class="flex items-center gap-2 text-xs font-medium text-gray-500 dark:text-gray-400">
                <span>ID de Reserva (id)</span>
              </div>
              <p class="mt-2 text-lg font-semibold text-gray-800 dark:text-white/90">
                #{{ reserva.id }}
              </p>
            </div>

            <div class="rounded-xl border border-gray-200 p-4 dark:border-gray-800">
              <div class="flex items-center gap-2 text-xs font-medium text-gray-500 dark:text-gray-400">
                <UserIcon class="size-4" />
                <span>Cliente (cliente)</span>
              </div>
              <p class="mt-2 text-lg font-semibold text-gray-800 dark:text-white/90">
                {{ reserva.cliente }}
              </p>
            </div>

            <div class="rounded-xl border border-gray-200 p-4 dark:border-gray-800">
              <div class="flex items-center gap-2 text-xs font-medium text-gray-500 dark:text-gray-400">
                <FlagIcon class="size-4" />
                <span>Cancha (cancha_id)</span>
              </div>
              <div class="mt-2 flex flex-wrap items-center gap-2">
                <Badge color="primary">ID #{{ reserva.cancha_id }}</Badge>
                <span
                  v-if="canchaAsociada"
                  class="text-base font-semibold text-gray-800 dark:text-white/90"
                >
                  {{ canchaAsociada.nombre }} ({{ canchaAsociada.deporte }})
                </span>
              </div>
            </div>

            <div class="rounded-xl border border-gray-200 p-4 dark:border-gray-800">
              <div class="flex items-center gap-2 text-xs font-medium text-gray-500 dark:text-gray-400">
                <CalendarIcon class="size-4" />
                <span>Fecha (fecha)</span>
              </div>
              <p class="mt-2 text-lg font-semibold text-gray-800 dark:text-white/90">
                {{ reserva.fecha }}
              </p>
            </div>

            <div class="rounded-xl border border-gray-200 p-4 dark:border-gray-800">
              <div class="flex items-center gap-2 text-xs font-medium text-gray-500 dark:text-gray-400">
                <TimeIcon class="size-4" />
                <span>Hora de inicio (hora_inicio)</span>
              </div>
              <p class="mt-2 text-lg font-semibold text-gray-800 dark:text-white/90">
                {{ reserva.hora_inicio }}
              </p>
            </div>

            <div class="rounded-xl border border-gray-200 p-4 dark:border-gray-800">
              <div class="flex items-center gap-2 text-xs font-medium text-gray-500 dark:text-gray-400">
                <TimeIcon class="size-4" />
                <span>Hora de fin (hora_fin)</span>
              </div>
              <p class="mt-2 text-lg font-semibold text-gray-800 dark:text-white/90">
                {{ reserva.hora_fin }}
              </p>
            </div>
          </div>
        </ComponentCard>
      </div>
    </div>
  </AdminLayout>
</template>

