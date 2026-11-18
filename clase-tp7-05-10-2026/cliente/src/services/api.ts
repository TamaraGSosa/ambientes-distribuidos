import type {
  Cancha,
  InfoResponse,
  PydanticValidationDetailItem,
  Reserva,
  ReservaCreatePayload,
  ReservaField,
  ReservaFieldErrors,
  SaludResponse,
} from '@/types/api'

export const API_BASE_URL =
  (import.meta.env.VITE_API_URL as string | undefined)?.replace(/\/$/, '') || '/api'

const VALID_RESERVA_FIELDS: Set<string> = new Set([
  'cancha_id',
  'cliente',
  'fecha',
  'hora_inicio',
  'hora_fin',
])

export class ApiError extends Error {
  status: number
  isConnectionError: boolean
  generalMessage: string | null
  fieldErrors: ReservaFieldErrors
  rawBody: unknown

  constructor(options: {
    message: string
    status: number
    isConnectionError?: boolean
    generalMessage?: string | null
    fieldErrors?: ReservaFieldErrors
    rawBody?: unknown
  }) {
    super(options.message)
    this.name = 'ApiError'
    this.status = options.status
    this.isConnectionError = options.isConnectionError ?? false
    this.generalMessage = options.generalMessage ?? null
    this.fieldErrors = options.fieldErrors ?? {}
    this.rawBody = options.rawBody
  }
}

async function buildApiErrorFromResponse(response: Response): Promise<ApiError> {
  const contentType = response.headers.get('content-type') ?? ''
  let rawText = ''
  try {
    rawText = await response.text()
  } catch {
    rawText = ''
  }

  let data: unknown = null
  if (rawText && contentType.includes('application/json')) {
    try {
      data = JSON.parse(rawText)
    } catch {
      data = null
    }
  } else if (rawText) {
    try {
      data = JSON.parse(rawText)
    } catch {
      data = null
    }
  }

  // Si el proxy de desarrollo devuelve 502/503/504 o un 500 sin JSON cuando el contenedor está apagado
  if (
    data === null &&
    (response.status === 502 ||
      response.status === 503 ||
      response.status === 504 ||
      (response.status === 500 && !contentType.includes('application/json')))
  ) {
    const msg = 'No fue posible comunicarse con el servidor. Verificá que el backend esté activo.'
    return new ApiError({
      message: msg,
      status: 0,
      isConnectionError: true,
      generalMessage: msg,
    })
  }

  const fieldErrors: ReservaFieldErrors = {}
  const unmappedMessages: string[] = []

  if (data && typeof data === 'object' && 'detail' in data) {
    const detail = (data as { detail: unknown }).detail

    if (typeof detail === 'string') {
      return new ApiError({
        message: detail,
        status: response.status,
        generalMessage: detail,
        fieldErrors: {},
        rawBody: data,
      })
    }

    if (Array.isArray(detail)) {
      for (const item of detail as PydanticValidationDetailItem[]) {
        if (!item || typeof item !== 'object') continue
        const msg = typeof item.msg === 'string' ? item.msg : 'Valor inválido'
        const loc = Array.isArray(item.loc) ? item.loc : []
        const lastLoc = loc.length > 0 ? String(loc[loc.length - 1]) : ''

        if (VALID_RESERVA_FIELDS.has(lastLoc)) {
          const field = lastLoc as ReservaField
          if (!fieldErrors[field]) {
            fieldErrors[field] = []
          }
          fieldErrors[field]!.push(msg)
        } else {
          const prefix = lastLoc && lastLoc !== 'body' ? `${lastLoc}: ` : ''
          unmappedMessages.push(`${prefix}${msg}`)
        }
      }

      const hasFieldErrors = Object.keys(fieldErrors).length > 0
      const generalMsg =
        unmappedMessages.length > 0
          ? unmappedMessages.join(' | ')
          : hasFieldErrors
            ? null
            : 'Los datos enviados contienen errores de validación.'

      const summaryParts: string[] = []
      for (const [f, msgs] of Object.entries(fieldErrors)) {
        if (msgs && msgs.length > 0) {
          summaryParts.push(`${f}: ${msgs.join(', ')}`)
        }
      }
      if (unmappedMessages.length > 0) {
        summaryParts.push(...unmappedMessages)
      }

      return new ApiError({
        message: summaryParts.join(' | ') || 'Error de validación (HTTP 422)',
        status: response.status,
        generalMessage: generalMsg,
        fieldErrors,
        rawBody: data,
      })
    }
  }

  const fallbackMessage = `Error HTTP ${response.status}: No se pudo completar la solicitud.`
  return new ApiError({
    message: fallbackMessage,
    status: response.status,
    generalMessage: fallbackMessage,
    fieldErrors: {},
    rawBody: data,
  })
}

async function request<T>(path: string, options?: RequestInit): Promise<T> {
  let response: Response
  try {
    response = await fetch(`${API_BASE_URL}${path}`, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...(options?.headers ?? {}),
      },
    })
  } catch {
    const msg = 'No fue posible comunicarse con el servidor. Verificá que el backend esté activo.'
    throw new ApiError({
      message: msg,
      status: 0,
      isConnectionError: true,
      generalMessage: msg,
    })
  }

  if (!response.ok) {
    throw await buildApiErrorFromResponse(response)
  }

  return (await response.json()) as T
}

export function obtenerInfoServicio(): Promise<InfoResponse> {
  return request<InfoResponse>('/')
}

export function verificarSalud(): Promise<SaludResponse> {
  return request<SaludResponse>('/salud')
}

export function obtenerCanchas(): Promise<Cancha[]> {
  return request<Cancha[]>('/canchas')
}

export function obtenerReservas(): Promise<Reserva[]> {
  return request<Reserva[]>('/reservas')
}

export function obtenerReservaPorId(id: number): Promise<Reserva> {
  return request<Reserva>(`/reservas/${id}`)
}

export function crearReserva(payload: ReservaCreatePayload): Promise<Reserva> {
  return request<Reserva>('/reservas', {
    method: 'POST',
    body: JSON.stringify(payload),
  })
}
