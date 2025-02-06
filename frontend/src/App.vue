<script setup lang="ts">
import { AppFooter, AppServiceWorker, NavBar } from '@/components/app'
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
        <AppFooter />
    </div>
    <Toaster />
    <AppServiceWorker />
</template>

<style scoped></style>
