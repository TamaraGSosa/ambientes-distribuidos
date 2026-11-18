<script setup lang="ts">
import { Button } from '@/components/ui/button'
import { useTheme } from '@/composables/useTheme'
import { MoonIcon, SunIcon } from '@/icons/layout'

const { toggleTheme, isDarkMode } = useTheme()
</script>

<template>
  <Button
    variant="outline"
    size="icon-lg"
    class="relative"
    :aria-label="isDarkMode ? 'Cambiar a modo claro' : 'Cambiar a modo oscuro'"
    :title="isDarkMode ? 'Cambiar a modo claro' : 'Cambiar a modo oscuro'"
    @click="toggleTheme"
  >
    <SunIcon v-if="isDarkMode" />
    <MoonIcon v-else />
  </Button>
</template>
