<script setup lang="ts">
import { computed, onMounted, ref, watch } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import PageBreadcrumb from '@/components/common/PageBreadcrumb.vue'
import CardFooter from '@/components/common/CardFooter.vue'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardTitle } from '@/components/ui/card'
import { InputGroup, InputGroupAddon, InputGroupInput } from '@/components/ui/input-group'
import { ScrollArea } from '@/components/ui/scroll-area'
import {
  Table,
  TableBody,
  TableCell,
  TableHeader as TableHead,
  TableRow,
} from '@/components/ui/table'
import {
  CalendarIcon,
  CalendarPlusIcon,
  EyeIcon,
  LoaderCircleIcon,
  OctagonAlertIcon,
  RegenerateIcon,
  SearchIcon,
} from '@/icons/layout'
import { obtenerCanchas, obtenerReservas } from '@/services/api'
import type { Cancha, Reserva } from '@/types/api'

const route = useRoute()
const reservas = ref<Reserva[]>([])
const canchasPorId = ref<Record<number, Cancha>>({})
const cargando = ref(true)
const error = ref<string | null>(null)

const searchQuery = ref(typeof route.query.q === 'string' ? route.query.q : '')
const currentPage = ref(1)
const pageSize = 8

watch(
  () => route.query.q,
  (q) => {
    if (typeof q === 'string') {
      searchQuery.value = q
    }
  },
)

async function cargarDatos() {
  cargando.value = true
  error.value = null
  try {
    const [listaReservas, listaCanchas] = await Promise.all([
      obtenerReservas(),
      obtenerCanchas().catch(() => [] as Cancha[]),
    ])
    reservas.value = listaReservas
    const mapa: Record<number, Cancha> = {}
    for (const cancha of listaCanchas) {
      mapa[cancha.id] = cancha
    }
    canchasPorId.value = mapa
  } catch (err) {
    reservas.value = []
    error.value =
      err instanceof Error ? err.message : 'Ocurrió un error al consultar las reservas.'
  } finally {
    cargando.value = false
  }
}

const reservasFiltradas = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()
  if (!q) return reservas.value
  return reservas.value.filter((r) => {
    const nombreCancha = canchasPorId.value[r.cancha_id]?.nombre?.toLowerCase() ?? ''
    return (
      String(r.id).includes(q) ||
      String(r.cancha_id).includes(q) ||
      nombreCancha.includes(q) ||
      r.cliente.toLowerCase().includes(q) ||
      r.fecha.toLowerCase().includes(q) ||
      r.hora_inicio.toLowerCase().includes(q) ||
      r.hora_fin.toLowerCase().includes(q)
    )
  })
})

const totalPages = computed(() =>
  Math.max(1, Math.ceil(reservasFiltradas.value.length / pageSize)),
)

const reservasPaginadas = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return reservasFiltradas.value.slice(start, start + pageSize)
})

watch(searchQuery, () => {
  currentPage.value = 1
})

onMounted(() => {
  cargarDatos()
})
</script>

<template>
  <AdminLayout>
    <div>
      <PageBreadcrumb pageTitle="Reservas" />

      <!-- Estado de error -->
      <Alert v-if="error" variant="destructive" class="mb-6 p-4">
        <OctagonAlertIcon class="size-5" />
        <AlertTitle>Error al consultar las reservas</AlertTitle>
        <AlertDescription>
          <p>{{ error }}</p>
          <div class="mt-3">
            <Button variant="outline" size="sm" @click="cargarDatos">Reintentar</Button>
          </div>
        </AlertDescription>
      </Alert>

      <Card
        class="w-full max-w-full min-w-0 gap-0 overflow-hidden rounded-2xl border border-gray-200 bg-white p-0 sm:p-0 dark:border-gray-800 dark:bg-white/3"
      >
        <!-- Encabezado de la tabla con búsqueda local y botón Nueva reserva -->
        <div
          class="flex flex-col gap-4 px-5 py-4 sm:flex-row sm:items-center sm:justify-between sm:px-6"
        >
          <div>
            <CardTitle class="text-lg font-semibold text-gray-800 dark:text-white/90">
              Listado de Reservas
            </CardTitle>
            <p class="mt-0.5 text-xs text-gray-500 dark:text-gray-400">
              Registros obtenidos de <code class="font-mono">GET /reservas</code>
            </p>
          </div>

          <div class="flex flex-wrap items-center gap-3">
            <InputGroup class="h-10 flex-1 sm:w-64 sm:flex-initial">
              <InputGroupAddon align="inline-start">
                <SearchIcon class="size-5 text-gray-400" />
              </InputGroupAddon>
              <InputGroupInput
                type="text"
                placeholder="Buscar por cliente, fecha, cancha..."
                :model-value="searchQuery"
                @update:model-value="(val) => (searchQuery = String(val ?? ''))"
              />
            </InputGroup>

            <Button variant="outline" size="sm" :disabled="cargando" @click="cargarDatos">
              <LoaderCircleIcon v-if="cargando" class="size-4 animate-spin" />
              <RegenerateIcon v-else class="size-4" />
              Actualizar
            </Button>

            <Button :as="RouterLink" to="/reservas/nueva" size="sm">
              <CalendarPlusIcon class="size-4" />
              Nueva reserva
            </Button>
          </div>
        </div>

        <!-- Contenido de la tabla -->
        <CardContent class="p-0">
          <ScrollArea class="w-full">
            <Table>
              <TableHead class="border-y border-gray-100 bg-gray-50 dark:border-gray-800 dark:bg-gray-900">
                <TableRow>
                  <TableCell
                    isHeader
                    class="h-11 py-3 ps-5 text-start text-xs font-medium text-gray-500 sm:ps-6 dark:text-gray-400"
                  >
                    ID
                  </TableCell>
                  <TableCell
                    isHeader
                    class="h-11 px-4 py-3 text-start text-xs font-medium text-gray-500 dark:text-gray-400"
                  >
                    Cancha (cancha_id)
                  </TableCell>
                  <TableCell
                    isHeader
                    class="h-11 px-4 py-3 text-start text-xs font-medium text-gray-500 dark:text-gray-400"
                  >
                    Cliente
                  </TableCell>
                  <TableCell
                    isHeader
                    class="h-11 px-4 py-3 text-start text-xs font-medium text-gray-500 dark:text-gray-400"
                  >
                    Fecha
                  </TableCell>
                  <TableCell
                    isHeader
                    class="h-11 px-4 py-3 text-start text-xs font-medium text-gray-500 dark:text-gray-400"
                  >
                    Hora inicio
                  </TableCell>
                  <TableCell
                    isHeader
                    class="h-11 px-4 py-3 text-start text-xs font-medium text-gray-500 dark:text-gray-400"
                  >
                    Hora fin
                  </TableCell>
                  <TableCell
                    isHeader
                    class="h-11 py-3 pe-5 text-end text-xs font-medium text-gray-500 sm:pe-6 dark:text-gray-400"
                  >
                    Detalle
                  </TableCell>
                </TableRow>
              </TableHead>

              <TableBody class="divide-y divide-gray-100 dark:divide-gray-800">
                <!-- Estado de carga -->
                <TableRow v-if="cargando">
                  <TableCell
                    :colspan="7"
                    class="px-6 py-12 text-center text-sm text-gray-500 dark:text-gray-400"
                  >
                    <div class="flex flex-col items-center justify-center">
                      <LoaderCircleIcon class="size-7 animate-spin text-brand-500" />
                      <p class="mt-2">Cargando reservas...</p>
                    </div>
                  </TableCell>
                </TableRow>

                <!-- Filas de reservas -->
                <template v-else-if="reservasPaginadas.length > 0">
                  <TableRow
                    v-for="reserva in reservasPaginadas"
                    :key="reserva.id"
                    class="transition-colors hover:bg-gray-50/60 dark:hover:bg-white/2"
                  >
                    <TableCell
                      class="py-3.5 ps-5 text-sm font-medium text-gray-800 sm:ps-6 dark:text-white/90"
                    >
                      #{{ reserva.id }}
                    </TableCell>

                    <TableCell class="px-4 py-3.5 text-sm text-gray-700 dark:text-gray-300">
                      <div class="flex items-center gap-2">
                        <Badge color="primary" size="sm">ID {{ reserva.cancha_id }}</Badge>
                        <span
                          v-if="canchasPorId[reserva.cancha_id]"
                          class="font-medium text-gray-800 dark:text-white/90"
                        >
                          {{ canchasPorId[reserva.cancha_id]?.nombre }}
                        </span>
                      </div>
                    </TableCell>

                    <TableCell
                      class="px-4 py-3.5 text-sm font-medium text-gray-800 dark:text-white/90"
                    >
                      {{ reserva.cliente }}
                    </TableCell>

                    <TableCell class="px-4 py-3.5 text-sm text-gray-600 dark:text-gray-400">
                      {{ reserva.fecha }}
                    </TableCell>

                    <TableCell class="px-4 py-3.5 text-sm text-gray-600 dark:text-gray-400">
                      {{ reserva.hora_inicio }}
                    </TableCell>

                    <TableCell class="px-4 py-3.5 text-sm text-gray-600 dark:text-gray-400">
                      {{ reserva.hora_fin }}
                    </TableCell>

                    <TableCell class="py-3.5 pe-5 text-end sm:pe-6">
                      <Button
                        :as="RouterLink"
                        :to="`/reservas/${reserva.id}`"
                        variant="outline"
                        size="xs"
                      >
                        <EyeIcon class="size-4" />
                        Ver detalle
                      </Button>
                    </TableCell>
                  </TableRow>
                </template>

                <!-- Estado vacío -->
                <TableRow v-else>
                  <TableCell
                    :colspan="7"
                    class="px-6 py-12 text-center text-sm text-gray-500 dark:text-gray-400"
                  >
                    <div class="flex flex-col items-center justify-center">
                      <div
                        class="mb-3 flex size-12 items-center justify-center rounded-full bg-gray-100 dark:bg-gray-800"
                      >
                        <CalendarIcon class="size-6 text-gray-800 dark:text-white/90" />
                      </div>
                      <h4 class="text-base font-semibold text-gray-800 dark:text-white/90">
                        {{
                          searchQuery
                            ? 'No se encontraron reservas con ese criterio'
                            : 'No hay reservas registradas'
                        }}
                      </h4>
                      <p class="mt-1 text-sm text-gray-500 dark:text-gray-400">
                        {{
                          searchQuery
                            ? 'Probá modificando el texto de búsqueda.'
                            : 'Podés registrar la primera reserva desde el formulario.'
                        }}
                      </p>
                      <div v-if="!searchQuery" class="mt-4">
                        <Button :as="RouterLink" to="/reservas/nueva" size="sm">
                          <CalendarPlusIcon class="size-4" />
                          Crear reserva
                        </Button>
                      </div>
                    </div>
                  </TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </ScrollArea>
        </CardContent>

        <!-- Paginación de TailAdmin -->
        <CardFooter
          v-if="!cargando && reservasFiltradas.length > pageSize"
          :current-page="currentPage"
          :total-pages="totalPages"
          @page-change="(page) => (currentPage = page)"
        />
      </Card>
    </div>
  </AdminLayout>
</template>

