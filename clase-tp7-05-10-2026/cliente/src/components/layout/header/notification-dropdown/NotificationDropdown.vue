<script setup lang="ts">
import { ref } from 'vue'
import { RouterLink } from 'vue-router'
import { Button, buttonVariants } from '@/components/ui/button'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { BellIcon, XIcon } from '@/icons/layout'

const isOpen = ref(false)
</script>

<template>
  <DropdownMenu :open="isOpen" @update:open="isOpen = $event">
    <DropdownMenuTrigger
      :class="buttonVariants({ variant: 'outline', size: 'icon-lg', class: 'relative' })"
      aria-label="Notificaciones"
      title="Notificaciones"
    >
      <BellIcon class="size-auto!" />
    </DropdownMenuTrigger>

    <DropdownMenuContent
      align="end"
      :side-offset="12"
      class="w-80 overflow-hidden p-3 sm:w-88 dark:bg-[#1E2635]"
    >
      <div class="flex items-center justify-between pb-3">
        <h5 class="text-base font-semibold text-gray-800 dark:text-gray-200">Notificaciones</h5>
        <Button
          variant="ghost"
          size="icon-xs"
          aria-label="Cerrar notificaciones"
          @click="isOpen = false"
        >
          <XIcon class="size-5" />
        </Button>
      </div>

      <DropdownMenuSeparator class="my-0" />

      <div class="flex flex-col items-center justify-center px-4 py-8 text-center">
        <div
          class="mb-2.5 flex size-10 items-center justify-center rounded-full bg-gray-100 text-gray-500 dark:bg-gray-800 dark:text-gray-400"
        >
          <BellIcon class="size-5" />
        </div>
        <p class="text-sm font-medium text-gray-800 dark:text-white/90">
          No hay notificaciones pendientes
        </p>
        <p class="mt-1 text-xs text-gray-500 dark:text-gray-400">
          Podés consultar el estado de las reservas desde el listado principal.
        </p>
      </div>

      <RouterLink
        to="/reservas"
        :class="buttonVariants({ variant: 'outline', size: 'default', class: 'mt-1 w-full' })"
        @click="isOpen = false"
      >
        Ir a Reservas
      </RouterLink>
    </DropdownMenuContent>
  </DropdownMenu>
</template>
