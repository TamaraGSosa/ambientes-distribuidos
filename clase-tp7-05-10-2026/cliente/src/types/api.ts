export interface SaludResponse {
  estado: string
  servicio: string
}

export interface InfoResponse {
  mensaje: string
  estado: string
}

export interface Cancha {
  id: number
  nombre: string
  deporte: string
  precio_hora: number
  activa: boolean
}

export interface Reserva {
  id: number
  cancha_id: number
  cliente: string
  fecha: string
  hora_inicio: string
  hora_fin: string
}

export interface ReservaCreatePayload {
  cancha_id: number | string | null
  cliente: string
  fecha: string
  hora_inicio: string
  hora_fin: string
}

export type ReservaField = 'cancha_id' | 'cliente' | 'fecha' | 'hora_inicio' | 'hora_fin'

export type ReservaFieldErrors = Partial<Record<ReservaField, string[]>>

export interface PydanticValidationDetailItem {
  type?: string
  loc?: Array<string | number>
  msg?: string
  input?: unknown
}
