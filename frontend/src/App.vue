<script setup lang="ts">
import Nav from '@/components/app/Nav.vue'
import { useRoute } from 'vue-router'
import { computed } from 'vue'
import { Toaster } from '@/components/ui/sonner'

const route = useRoute()

const shouldHideNav = computed(() => route.meta.hideUi || false)
</script>

<template>
  <div class="flex min-h-screen flex-col bg-background">
    <Nav v-if="!shouldHideNav" />
    <RouterView />
  </div>
  <Toaster />
</template>

<style scoped></style>
