<script setup lang="ts">
import flatpickr from 'flatpickr'
import { Spanish } from 'flatpickr/dist/l10n/es.js'
import { computed, onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { RouterLink, useRoute } from 'vue-router'
import AdminLayout from '@/components/layout/AdminLayout.vue'
import ComponentCard from '@/components/common/ComponentCard.vue'
import PageBreadcrumb from '@/components/common/PageBreadcrumb.vue'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { Badge } from '@/components/ui/badge'
import { Button } from '@/components/ui/button'
import { Field, FieldDescription, FieldError, FieldGroup, FieldLabel } from '@/components/ui/field'
import { Input } from '@/components/ui/input'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import {
  ArrowLeftIcon,
  CalendarIcon,
  CalendarPlusIcon,
  CheckCircleIcon,
  EyeIcon,
  LoaderCircleIcon,
  OctagonAlertIcon,
  TimeIcon,
} from '@/icons/layout'
import { ApiError, crearReserva, obtenerCanchas } from '@/services/api'
import type { Cancha, Reserva, ReservaField, ReservaFieldErrors } from '@/types/api'

export interface OpcionDuracion {
  valorHoras: number
  etiqueta: string
}

// Opciones de duración centralizadas para facilitar su mantenimiento
const OPCIONES_DURACION: OpcionDuracion[] = [
  { valorHoras: 1, etiqueta: '1 hora' },
  { valorHoras: 2, etiqueta: '2 horas' },
  { valorHoras: 3, etiqueta: '3 horas' },
]

const route = useRoute()

const canchas = ref<Cancha[]>([])
const cargandoCanchas = ref(true)
const errorCanchas = ref<string | null>(null)

const canchaIdSeleccionada = ref<string>('')
const cliente = ref('')

// fechaVisible muestra dd/mm/aaaa en el input; fechaApi conserva YYYY-MM-DD para POST /reservas
const fechaVisible = ref('')
const fechaApi = ref('')

const horaInicio = ref('')
const duracionSeleccionada = ref<string>(String(OPCIONES_DURACION[0]!.valorHoras))

const dateWrapperRef = ref<HTMLElement | null>(null)
const timeWrapperRef = ref<HTMLElement | null>(null)
let datePickerInstance: flatpickr.Instance | undefined
let timePickerInstance: flatpickr.Instance | undefined

const enviando = ref(false)
const reservaCreada = ref<Reserva | null>(null)

const errorGeneral = ref<string | null>(null)
const codigoEstadoError = ref<number | null>(null)
const esErrorConexion = ref(false)
const erroresCampos = ref<ReservaFieldErrors>({})
const errorCalculoHorario = ref<string | null>(null)

function formatearFechaApi(dateObj: Date): string {
  const anio = dateObj.getFullYear()
  const mes = String(dateObj.getMonth() + 1).padStart(2, '0')
  const dia = String(dateObj.getDate()).padStart(2, '0')
  return `${anio}-${mes}-${dia}`
}

function convertirDdMmYyyyAIso(valor: string): string {
  const limpio = valor.trim()
  if (!limpio) return ''
  const partesBarra = /^(\d{2})\/(\d{2})\/(\d{4})$/.exec(limpio)
  if (partesBarra) {
    return `${partesBarra[3]}-${partesBarra[2]}-${partesBarra[1]}`
  }
  return limpio
}

// Cálculo reactivo de la hora de finalización a partir de horaInicio y duracionSeleccionada
const calculoHoraFin = computed<{
  horaFin: string
  invalido: boolean
  motivoInvalido: string | null
}>(() => {
  const inicio = horaInicio.value.trim()
  const horasDuracion = Number(duracionSeleccionada.value)

  if (!inicio) {
    return {
      horaFin: '',
      invalido: false,
      motivoInvalido: null,
    }
  }

  const match = /^([01]\d|2[0-3]):([0-5]\d)$/.exec(inicio)
  if (!match) {
    return {
      horaFin: '',
      invalido: true,
      motivoInvalido: 'Seleccioná una hora de inicio válida en formato 24 horas (HH:mm).',
    }
  }

  if (!Number.isFinite(horasDuracion) || horasDuracion <= 0) {
    return {
      horaFin: '',
      invalido: true,
      motivoInvalido: 'Seleccioná una duración válida.',
    }
  }

  const horasInicio = Number(match[1])
  const minutosInicio = Number(match[2])
  const totalMinutosInicio = horasInicio * 60 + minutosInicio
  const totalMinutosFin = totalMinutosInicio + horasDuracion * 60

  // Una reserva pertenece a una fecha específica; si cruza las 23:59 el rango dentro del día es inválido
  if (totalMinutosFin >= 24 * 60) {
    return {
      horaFin: '',
      invalido: true,
      motivoInvalido:
        'La hora de finalización supera las 23:59 del día seleccionado. Elegí una hora de inicio anterior o una duración menor.',
    }
  }

  const horasFin = Math.floor(totalMinutosFin / 60)
  const minutosFin = totalMinutosFin % 60
  const horaFinFormateada = `${String(horasFin).padStart(2, '0')}:${String(minutosFin).padStart(2, '0')}`

  return {
    horaFin: horaFinFormateada,
    invalido: false,
    motivoInvalido: null,
  }
})

const horaFinCalculada = computed(() => calculoHoraFin.value.horaFin)

watch([horaInicio, duracionSeleccionada], () => {
  errorCalculoHorario.value = null
  limpiarErrorDeCampo('hora_inicio')
  limpiarErrorDeCampo('hora_fin')
})

const tieneErroresDeCampo = computed(() => Object.keys(erroresCampos.value).length > 0)

const tituloAlertaError = computed(() => {
  if (esErrorConexion.value) {
    return 'Error de conexión con el servidor'
  }
  switch (codigoEstadoError.value) {
    case 400:
      return 'Error de validación de negocio (HTTP 400)'
    case 404:
      return 'Recurso no encontrado (HTTP 404)'
    case 409:
      return 'Conflicto de horario (HTTP 409)'
    case 422:
      return 'Error de validación de datos (HTTP 422)'
    default:
      return codigoEstadoError.value
        ? `Error en la solicitud (HTTP ${codigoEstadoError.value})`
        : 'No se pudo registrar la reserva'
  }
})

function obtenerErroresParaFieldError(campo: ReservaField): Array<{ message?: string }> {
  const lista = erroresCampos.value[campo]
  if (!lista || lista.length === 0) return []
  return lista.map((msg) => ({ message: msg }))
}

function tieneErrorCampo(campo: ReservaField): boolean {
  return Boolean(erroresCampos.value[campo] && erroresCampos.value[campo]!.length > 0)
}

function limpiarErrorDeCampo(campo: ReservaField) {
  if (erroresCampos.value[campo]) {
    const copia = { ...erroresCampos.value }
    delete copia[campo]
    erroresCampos.value = copia
  }
}

async function cargarCanchasDisponibles() {
  cargandoCanchas.value = true
  errorCanchas.value = null
  try {
    const lista = await obtenerCanchas()
    canchas.value = lista

    const preseleccion = route.query.cancha_id
    if (
      preseleccion &&
      lista.some((c) => String(c.id) === String(preseleccion) && c.activa)
    ) {
      canchaIdSeleccionada.value = String(preseleccion)
    } else if (!canchaIdSeleccionada.value && lista.length > 0 && lista[0]) {
      canchaIdSeleccionada.value = String(lista[0].id)
    }
  } catch (err) {
    errorCanchas.value =
      err instanceof Error ? err.message : 'No se pudieron obtener las canchas disponibles.'
  } finally {
    cargandoCanchas.value = false
  }
}

function inicializarSelectoresFechaYHora() {
  const dateInput = dateWrapperRef.value?.querySelector('input')
  if (dateInput) {
    datePickerInstance = flatpickr(dateInput, {
      mode: 'single',
      locale: Spanish,
      dateFormat: 'd/m/Y',
      minDate: 'today',
      allowInput: true,
      disableMobile: true,
      onChange: (selectedDates, dateStr) => {
        fechaVisible.value = dateStr
        if (selectedDates[0]) {
          fechaApi.value = formatearFechaApi(selectedDates[0])
        } else {
          fechaApi.value = convertirDdMmYyyyAIso(dateStr)
        }
        limpiarErrorDeCampo('fecha')
      },
    })
  }

  const timeInput = timeWrapperRef.value?.querySelector('input')
  if (timeInput) {
    timePickerInstance = flatpickr(timeInput, {
      enableTime: true,
      noCalendar: true,
      dateFormat: 'H:i',
      time_24hr: true,
      minuteIncrement: 15,
      allowInput: true,
      disableMobile: true,
      onChange: (_selectedDates, timeStr) => {
        horaInicio.value = timeStr
        limpiarErrorDeCampo('hora_inicio')
        limpiarErrorDeCampo('hora_fin')
      },
    })
  }
}

function handleFechaInputManual(event: Event) {
  const val = (event.target as HTMLInputElement).value
  fechaVisible.value = val
  fechaApi.value = convertirDdMmYyyyAIso(val)
  limpiarErrorDeCampo('fecha')
}

async function handleSubmit() {
  if (enviando.value) return

  // Limpiar estados previos al iniciar un nuevo intento
  reservaCreada.value = null
  errorGeneral.value = null
  codigoEstadoError.value = null
  esErrorConexion.value = false
  erroresCampos.value = {}
  errorCalculoHorario.value = null

  // Evitar enviar un rango horario imposible de calcular (ej. cruza la medianoche)
  if (calculoHoraFin.value.invalido) {
    errorCalculoHorario.value = calculoHoraFin.value.motivoInvalido
    return
  }

  enviando.value = true
  try {
    const canchaIdValor =
      canchaIdSeleccionada.value.trim() === ''
        ? null
        : Number.isNaN(Number(canchaIdSeleccionada.value))
          ? canchaIdSeleccionada.value
          : Number(canchaIdSeleccionada.value)

    const fechaParaEnviar = fechaApi.value || convertirDdMmYyyyAIso(fechaVisible.value)

    const nueva = await crearReserva({
      cancha_id: canchaIdValor,
      cliente: cliente.value,
      fecha: fechaParaEnviar,
      hora_inicio: horaInicio.value,
      hora_fin: horaFinCalculada.value,
    })

    reservaCreada.value = nueva
    cliente.value = ''
    fechaVisible.value = ''
    fechaApi.value = ''
    horaInicio.value = ''
    datePickerInstance?.clear()
    timePickerInstance?.clear()
  } catch (err) {
    // Se conservan los valores ingresados para que el usuario pueda corregirlos y reenviar
    if (err instanceof ApiError) {
      codigoEstadoError.value = err.status
      esErrorConexion.value = err.isConnectionError
      erroresCampos.value = { ...err.fieldErrors }
      errorGeneral.value = err.generalMessage
    } else if (err instanceof Error) {
      errorGeneral.value = err.message
    } else {
      errorGeneral.value = 'Ocurrió un error inesperado al procesar la solicitud.'
    }
  } finally {
    enviando.value = false
  }
}

onMounted(() => {
  cargarCanchasDisponibles()
  inicializarSelectoresFechaYHora()
})

onBeforeUnmount(() => {
  datePickerInstance?.destroy()
  timePickerInstance?.destroy()
})
</script>

<template>
  <AdminLayout>
    <div>
      <PageBreadcrumb pageTitle="Nueva reserva" />

      <div class="mx-auto max-w-3xl space-y-6">
        <!-- Confirmación de éxito únicamente cuando el backend responde 201 -->
        <Alert v-if="reservaCreada" variant="success" class="p-4">
          <CheckCircleIcon class="size-5" />
          <AlertTitle class="flex flex-wrap items-center gap-2">
            <span>Reserva creada correctamente (ID #{{ reservaCreada.id }})</span>
            <Badge color="success" size="sm">HTTP 201</Badge>
          </AlertTitle>
          <AlertDescription>
            <p class="mt-1">
              Se registró la reserva para <strong>{{ reservaCreada.cliente }}</strong> en la cancha
              ID <strong>#{{ reservaCreada.cancha_id }}</strong> el día
              <strong>{{ reservaCreada.fecha }}</strong> de
              <strong>{{ reservaCreada.hora_inicio }}</strong> a
              <strong>{{ reservaCreada.hora_fin }}</strong>.
            </p>
            <div class="mt-4 flex flex-wrap items-center gap-3">
              <Button :as="RouterLink" :to="`/reservas/${reservaCreada.id}`" size="sm">
                <EyeIcon class="size-4" />
                Ver detalle de la reserva
              </Button>
              <Button :as="RouterLink" to="/reservas" variant="outline" size="sm">
                Ir al listado de reservas
              </Button>
            </div>
          </AlertDescription>
        </Alert>

        <!-- Error al cargar el listado inicial de canchas -->
        <Alert v-if="errorCanchas" variant="destructive" class="p-4">
          <OctagonAlertIcon class="size-5" />
          <AlertTitle>Error al consultar las canchas disponibles</AlertTitle>
          <AlertDescription>
            <p>{{ errorCanchas }}</p>
            <div class="mt-3">
              <Button variant="outline" size="sm" @click="cargarCanchasDisponibles">
                Reintentar
              </Button>
            </div>
          </AlertDescription>
        </Alert>

        <!-- Alerta por rango horario imposible de calcular -->
        <Alert v-if="errorCalculoHorario" variant="destructive" class="p-4">
          <OctagonAlertIcon class="size-5" />
          <AlertTitle>Rango horario inválido</AlertTitle>
          <AlertDescription>
            <p class="mt-1">{{ errorCalculoHorario }}</p>
          </AlertDescription>
        </Alert>

        <!-- Alerta general de error del backend (400, 404, 409, error de conexión o resumen 422) -->
        <Alert
          v-if="errorGeneral || tieneErroresDeCampo"
          variant="destructive"
          class="p-4"
        >
          <OctagonAlertIcon class="size-5" />
          <AlertTitle class="flex flex-wrap items-center gap-2">
            <span>{{ tituloAlertaError }}</span>
            <Badge v-if="codigoEstadoError" color="error" size="sm">
              HTTP {{ codigoEstadoError }}
            </Badge>
            <Badge v-else-if="esErrorConexion" color="error" size="sm">Sin respuesta</Badge>
          </AlertTitle>
          <AlertDescription>
            <p v-if="errorGeneral" class="mt-1">{{ errorGeneral }}</p>
            <p v-else-if="tieneErroresDeCampo" class="mt-1">
              Revisá los mensajes de validación indicados debajo de cada campo marcado en rojo.
            </p>
          </AlertDescription>
        </Alert>

        <!-- Formulario con componentes de TailAdmin -->
        <ComponentCard
          title="Registrar nueva reserva"
          desc="Seleccioná la cancha, el cliente, la fecha y la duración para calcular automáticamente el horario."
        >
          <form class="space-y-6" novalidate @submit.prevent="handleSubmit">
            <FieldGroup class="space-y-5">
              <!-- Selección de cancha -->
              <Field :data-invalid="tieneErrorCampo('cancha_id')">
                <FieldLabel>Cancha</FieldLabel>
                <Select
                  :model-value="canchaIdSeleccionada"
                  :disabled="cargandoCanchas || canchas.length === 0 || enviando"
                  @update:model-value="
                    (val) => {
                      canchaIdSeleccionada = String(val ?? '')
                      limpiarErrorDeCampo('cancha_id')
                    }
                  "
                >
                  <SelectTrigger
                    :aria-invalid="tieneErrorCampo('cancha_id') ? 'true' : undefined"
                  >
                    <SelectValue
                      :placeholder="
                        cargandoCanchas
                          ? 'Cargando canchas disponibles...'
                          : 'Seleccionar una cancha'
                      "
                    />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem
                      v-for="cancha in canchas"
                      :key="cancha.id"
                      :value="String(cancha.id)"
                    >
                      #{{ cancha.id }} — {{ cancha.nombre }} ({{ cancha.deporte }} · ${{
                        cancha.precio_hora.toLocaleString('es-AR')
                      }}/h)
                    </SelectItem>
                  </SelectContent>
                </Select>
                <FieldDescription>
                  Canchas activas obtenidas desde <code class="font-mono">GET /canchas</code>.
                </FieldDescription>
                <FieldError :errors="obtenerErroresParaFieldError('cancha_id')" />
              </Field>

              <!-- Nombre del cliente -->
              <Field :data-invalid="tieneErrorCampo('cliente')">
                <FieldLabel>Cliente</FieldLabel>
                <Input
                  v-model="cliente"
                  type="text"
                  placeholder="Nombre y apellido del cliente"
                  :disabled="enviando"
                  :aria-invalid="tieneErrorCampo('cliente') ? 'true' : undefined"
                  @input="limpiarErrorDeCampo('cliente')"
                />
                <FieldError :errors="obtenerErroresParaFieldError('cliente')" />
              </Field>

              <!-- Fecha en una fila completa con calendario en español -->
              <Field :data-invalid="tieneErrorCampo('fecha')">
                <FieldLabel>Fecha de la reserva</FieldLabel>
                <div ref="dateWrapperRef" class="relative">
                  <Input
                    :model-value="fechaVisible"
                    type="text"
                    placeholder="dd/mm/aaaa"
                    class="pe-10"
                    :disabled="enviando"
                    :aria-invalid="tieneErrorCampo('fecha') ? 'true' : undefined"
                    @input="handleFechaInputManual"
                  />
                  <span
                    class="pointer-events-none absolute end-3.5 top-1/2 -translate-y-1/2 text-gray-500 dark:text-gray-400"
                  >
                    <CalendarIcon class="size-5" />
                  </span>
                </div>
                <FieldDescription>
                  Seleccioná una fecha en el calendario (<code class="font-mono">dd/mm/aaaa</code>).
                  Se enviará como <code class="font-mono">{{ fechaApi || 'YYYY-MM-DD' }}</code>.
                </FieldDescription>
                <FieldError :errors="obtenerErroresParaFieldError('fecha')" />
              </Field>

              <!-- Hora de inicio y Duración alineadas en pantallas grandes -->
              <div class="grid grid-cols-1 gap-5 sm:grid-cols-2">
                <Field :data-invalid="tieneErrorCampo('hora_inicio') || calculoHoraFin.invalido">
                  <FieldLabel>Hora de inicio (24 hs)</FieldLabel>
                  <div ref="timeWrapperRef" class="relative">
                    <Input
                      v-model="horaInicio"
                      type="text"
                      placeholder="HH:mm (ej. 18:00)"
                      class="pe-10"
                      :disabled="enviando"
                      :aria-invalid="
                        tieneErrorCampo('hora_inicio') || calculoHoraFin.invalido
                          ? 'true'
                          : undefined
                      "
                      @input="limpiarErrorDeCampo('hora_inicio')"
                    />
                    <span
                      class="pointer-events-none absolute end-3.5 top-1/2 -translate-y-1/2 text-gray-500 dark:text-gray-400"
                    >
                      <TimeIcon class="size-5" />
                    </span>
                  </div>
                  <FieldError :errors="obtenerErroresParaFieldError('hora_inicio')" />
                </Field>

                <Field>
                  <FieldLabel>Duración de la reserva</FieldLabel>
                  <Select
                    :model-value="duracionSeleccionada"
                    :disabled="enviando"
                    @update:model-value="(val) => (duracionSeleccionada = String(val ?? '1'))"
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="Seleccionar duración" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem
                        v-for="opcion in OPCIONES_DURACION"
                        :key="opcion.valorHoras"
                        :value="String(opcion.valorHoras)"
                      >
                        {{ opcion.etiqueta }}
                      </SelectItem>
                    </SelectContent>
                  </Select>
                </Field>
              </div>

              <!-- Hora de finalización calculada automáticamente -->
              <Field :data-invalid="tieneErrorCampo('hora_fin') || calculoHoraFin.invalido">
                <FieldLabel>Hora de finalización (calculada automáticamente)</FieldLabel>
                <div class="relative">
                  <Input
                    :model-value="
                      horaFinCalculada ||
                      (calculoHoraFin.invalido
                        ? 'Horario fuera del límite del día (23:59)'
                        : 'Seleccioná la hora de inicio para calcular')
                    "
                    type="text"
                    readonly
                    class="bg-gray-50 pe-10 font-medium text-gray-800 dark:bg-gray-800/60 dark:text-white/90"
                    :aria-invalid="
                      tieneErrorCampo('hora_fin') || calculoHoraFin.invalido ? 'true' : undefined
                    "
                  />
                  <span
                    class="pointer-events-none absolute end-3.5 top-1/2 -translate-y-1/2 text-gray-500 dark:text-gray-400"
                  >
                    <TimeIcon class="size-5" />
                  </span>
                </div>
                <FieldDescription v-if="horaInicio && horaFinCalculada">
                  Rango horario a enviar: <strong>{{ horaInicio }}</strong> a
                  <strong>{{ horaFinCalculada }}</strong> ({{ duracionSeleccionada }}
                  {{ Number(duracionSeleccionada) === 1 ? 'hora' : 'horas' }}).
                </FieldDescription>
                <FieldError
                  v-if="calculoHoraFin.invalido && calculoHoraFin.motivoInvalido"
                  :errors="[{ message: calculoHoraFin.motivoInvalido }]"
                />
                <FieldError :errors="obtenerErroresParaFieldError('hora_fin')" />
              </Field>
            </FieldGroup>

            <div
              class="flex flex-wrap items-center justify-between gap-3 border-t border-gray-100 pt-4 dark:border-gray-800"
            >
              <Button :as="RouterLink" to="/reservas" variant="outline">
                <ArrowLeftIcon class="size-4" />
                Volver a Reservas
              </Button>

              <Button type="submit" :disabled="enviando || calculoHoraFin.invalido">
                <LoaderCircleIcon v-if="enviando" class="size-4 animate-spin" />
                <CalendarPlusIcon v-else class="size-4" />
                {{ enviando ? 'Enviando solicitud...' : 'Confirmar reserva' }}
              </Button>
            </div>
          </form>
        </ComponentCard>
      </div>
    </div>
  </AdminLayout>
</template>
