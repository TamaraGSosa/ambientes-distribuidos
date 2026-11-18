<script setup lang="ts">
import { RouterLink } from 'vue-router'
import GridShape from '@/components/common/CommonGridShape.vue'
import { Button } from '@/components/ui/button'
</script>

<template>
  <div class="relative z-1 flex min-h-screen flex-col items-center justify-center overflow-hidden p-6">
    <GridShape />
    <div class="mx-auto w-full max-w-60.5 text-center sm:max-w-118">
      <h1 class="mb-8 font-bold text-gray-800 dark:text-white/90">ERROR</h1>

      <img src="/images/error/404.svg" alt="404" class="dark:hidden" width="472" height="152" />
      <img
        src="/images/error/404-dark.svg"
        alt="404"
        class="hidden dark:block"
        width="472"
        height="152"
      />

      <p class="mt-10 mb-6 text-base text-gray-700 sm:text-lg dark:text-gray-400">
        No pudimos encontrar la página que estás buscando.
      </p>

      <Button size="lg" variant="outline" class="h-12.5 bg-white" :as="RouterLink" to="/">
        Volver al panel principal
      </Button>
    </div>

    <p
      class="absolute start-1/2 bottom-6 -translate-x-1/2 text-center text-sm text-gray-500 dark:text-gray-400"
    >
      &copy; {{ new Date().getFullYear() }} - ReservaCancha
    </p>
  </div>
</template>
