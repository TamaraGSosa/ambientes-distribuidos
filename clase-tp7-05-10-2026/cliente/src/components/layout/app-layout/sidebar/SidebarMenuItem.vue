<script setup lang="ts">
import { computed } from 'vue'
import { RouterLink } from 'vue-router'
import { SidebarMenuItem as UiSidebarMenuItem } from '@/components/ui/sidebar'
import { cn } from '@/utils/cn'
import type { NavItem } from './types'

interface SidebarMenuItemProps {
  nav: NavItem
  showContent: boolean
  pathname: string
}

const props = defineProps<SidebarMenuItemProps>()

const isDirectActive = computed(() => {
  if (!props.nav.path) return false
  if (props.nav.path === props.pathname) return true
  if (
    props.nav.path === '/reservas' &&
    props.pathname.startsWith('/reservas/') &&
    props.pathname !== '/reservas/nueva'
  ) {
    return true
  }
  return false
})
</script>

<template>
  <UiSidebarMenuItem>
    <RouterLink
      v-if="nav.path"
      :to="nav.path"
      :target="nav.target"
      :class="cn('group menu-item', isDirectActive ? 'menu-item-active' : 'menu-item-inactive')"
    >
      <span :class="cn(isDirectActive ? 'menu-item-icon-active' : 'menu-item-icon-inactive')">
        <component :is="nav.icon" class="size-6 stroke-[1.5px]" />
      </span>
      <span v-if="showContent" class="menu-item-text">{{ nav.name }}</span>
    </RouterLink>
  </UiSidebarMenuItem>
</template>
