<template>
  <ConfigProvider dir="ltr">
    <ThemeProvider>
      <SidebarProvider>
        <RouterView />
      </SidebarProvider>
    </ThemeProvider>
  </ConfigProvider>
</template>

<script setup lang="ts">
import { ConfigProvider } from 'reka-ui'
import ThemeProvider from './components/layout/ThemeProvider.vue'
import SidebarProvider from './components/layout/SidebarProvider.vue'
</script>
