<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { useUserProfile } from '@/composables/useUserProfile'
import { ChevronDownIcon, LogOutIcon, UserCircleIcon } from '@/icons/layout'
import { cn } from '@/utils/cn'

const router = useRouter()
const isOpen = ref(false)

const { profileState, displayName, shortName, initialLetter, limpiarSesionLocal } =
  useUserProfile()

function handleCerrarSesion() {
  isOpen.value = false
  limpiarSesionLocal()
  router.push('/signin')
}
</script>

<template>
  <DropdownMenu :open="isOpen" @update:open="isOpen = $event">
    <DropdownMenuTrigger class="dropdown-toggle flex items-center text-gray-700 dark:text-gray-400">
      <Avatar class="me-3 size-11 rounded-lg">
        <AvatarImage
          v-if="profileState.avatarUrl"
          :src="profileState.avatarUrl"
          :alt="displayName"
        />
        <AvatarFallback
          class="rounded-lg bg-brand-500 text-sm font-semibold text-white dark:bg-brand-500 dark:text-white"
        >
          {{ initialLetter }}
        </AvatarFallback>
      </Avatar>

      <span class="me-1 block text-sm font-medium text-gray-800 dark:text-white/90">
        {{ shortName }}
      </span>

      <ChevronDownIcon
        :class="
          cn(
            'size-5 text-gray-500 duration-200 dark:text-gray-400',
            isOpen ? 'rotate-180' : 'rotate-0',
          )
        "
      />
    </DropdownMenuTrigger>

    <DropdownMenuContent align="end" :side-offset="12" class="w-65 p-3">
      <div class="px-3 py-2">
        <span class="block text-sm font-medium text-gray-800 dark:text-white/90">
          {{ displayName }}
        </span>
        <span
          v-if="profileState.email"
          class="mt-0.5 block text-xs text-gray-500 dark:text-gray-400"
        >
          {{ profileState.email }}
        </span>
      </div>

      <DropdownMenuSeparator />

      <DropdownMenuItem to="/profile">
        <UserCircleIcon class="size-6" />
        Editar perfil
      </DropdownMenuItem>

      <DropdownMenuSeparator />

      <DropdownMenuItem variant="destructive" @click="handleCerrarSesion">
        <LogOutIcon />
        Cerrar sesión
      </DropdownMenuItem>
    </DropdownMenuContent>
  </DropdownMenu>
</template>
