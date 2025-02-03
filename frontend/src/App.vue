<script setup lang="ts">
import { AppServiceWorker, NavBar } from '@/components/app'
import { useRoute } from 'vue-router'
import { computed } from 'vue'
import { Toaster } from '@/components/ui/sonner'

const route = useRoute()

const shouldHideNav = computed(() => route.meta.hideUi || false)
</script>

<template>
    <div class="flex min-h-screen flex-col bg-background">
        <NavBar v-if="!shouldHideNav" />
        <RouterView />
    </div>
    <Toaster />
    <AppServiceWorker />
</template>

<style scoped></style>
