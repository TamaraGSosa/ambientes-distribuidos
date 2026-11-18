import type { Component } from 'vue'

export interface NavItem {
  name: string
  icon: Component
  path?: string
  target?: string
}
