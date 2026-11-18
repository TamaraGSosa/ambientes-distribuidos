import {
  CalendarIcon,
  CalendarPlusIcon,
  FlagIcon,
  LayoutGridIcon,
} from '@/icons/layout'
import type { NavItem } from './types'

export const navItems: NavItem[] = [
  {
    icon: LayoutGridIcon,
    name: 'Panel principal',
    path: '/',
  },
  {
    icon: FlagIcon,
    name: 'Canchas',
    path: '/canchas',
  },
  {
    icon: CalendarIcon,
    name: 'Reservas',
    path: '/reservas',
  },
  {
    icon: CalendarPlusIcon,
    name: 'Nueva reserva',
    path: '/reservas/nueva',
  },
]
