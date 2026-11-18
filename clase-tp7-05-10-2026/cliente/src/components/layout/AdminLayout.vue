<script setup lang="ts">
import { SidebarInset, SidebarProvider } from '@/components/ui/sidebar'
import AppHeader from './app-layout/header/AppHeader.vue'
import AppSidebar from './app-layout/sidebar/AppSidebar.vue'
</script>

<template>
  <SidebarProvider>
    <AppSidebar />

    <SidebarInset>
      <AppHeader />

      <div class="mx-auto w-full max-w-(--breakpoint-2xl) p-4 md:p-6">
        <slot />
      </div>
    </SidebarInset>
  </SidebarProvider>
</template>
