<script setup lang="ts">
import { AppFooter, AppServiceWorker } from '@/components/app'
import { useRoute } from 'vue-router'
import { computed, onBeforeUnmount, onMounted } from 'vue'
import { Toaster } from '@/components/ui/sonner'
import { AppWrapper } from '@/components'
import JSConfetti from 'js-confetti'
import { presets } from '@/lib/confettis.ts'

const route = useRoute()

const shouldHideNav = computed(() => Boolean(route.meta.hideUi || false))

let jsConfetti: JSConfetti

const confetti = (event: CustomEvent) => {
    if (!jsConfetti) return

    const presetName = event.detail ?? 'default'
    const presetConfig = presets.get(presetName) ?? {}

    jsConfetti.addConfetti(presetConfig)
}

onMounted(() => {
    jsConfetti = new JSConfetti()
    document.addEventListener('confetti', confetti as EventListener)
})

onBeforeUnmount(() => {
    if (jsConfetti) jsConfetti.destroyCanvas()
    document.removeEventListener('confetti', confetti as EventListener)
})
</script>

<template>
    <AppWrapper :hide-nav="shouldHideNav">
        <RouterView />
        <AppFooter class="mt-auto" />
    </AppWrapper>
    <Toaster />
    <AppServiceWorker />
</template>

<style scoped></style>
