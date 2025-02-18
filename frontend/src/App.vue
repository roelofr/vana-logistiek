<script setup lang="ts">
import { AppFooter, AppServiceWorker, NavBar } from '@/components/app'
import { useRoute } from 'vue-router'
import { computed, onBeforeUnmount, onMounted } from 'vue'
import { Toaster } from '@/components/ui/sonner'
import JSConfetti from 'js-confetti'

const route = useRoute()

const shouldHideNav = computed(() => route.meta.hideUi || false)

let jsConfetti: JSConfetti

const confetti = () => {
    if (jsConfetti) jsConfetti.addConfetti()
}

onMounted(() => {
    jsConfetti = new JSConfetti()
    document.addEventListener('confetti', confetti, { passive: true })
})

onBeforeUnmount(() => {
    if (jsConfetti) jsConfetti.destroyCanvas()
    document.removeEventListener('confetti', confetti)
})
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
