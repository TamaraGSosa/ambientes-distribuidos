<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { Sidebar, SidebarContent, useSidebar } from '@/components/ui/sidebar'
import { navItems } from './data'
import AppSidebarHeader from './SidebarHeader.vue'
import SidebarNavGroup from './SidebarNavGroup.vue'

const route = useRoute()
const { open, isMobile, isHovered } = useSidebar()

const pathname = computed(() => route.path)
const showContent = computed(() => open.value || isHovered.value || isMobile.value)
</script>

<template>
  <Sidebar side="left" collapsible="icon">
    <AppSidebarHeader :show-content="showContent" />

    <SidebarContent class="overflow-y-auto px-5 duration-300 ease-linear">
      <nav class="mb-6">
        <div class="flex flex-col gap-4">
          <SidebarNavGroup
            label="Menú"
            :items="navItems"
            :show-content="showContent"
            :pathname="pathname"
          />
        </div>
      </nav>
    </SidebarContent>
  </Sidebar>
</template>
