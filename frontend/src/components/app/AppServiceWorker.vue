<script setup lang="ts">
import { useRegisterSW } from 'virtual:pwa-register/vue'
import { toast } from 'vue-sonner'
import { watch } from 'vue'
import { DownloadIcon } from 'lucide-vue-next'

const autoUpdateFrequencyMs = 5 * 60 * 1000 // every 5 minutes
let autoUpdateInterval: NodeJS.Timeout

const { offlineReady, needRefresh, updateServiceWorker } = useRegisterSW({
    // Handle registration
    onRegisteredSW(scriptUrl, registration) {
        if (!registration) return

        console.log('ServiceWorker registered via URL %s', scriptUrl)

        if (autoUpdateInterval) clearInterval(autoUpdateInterval)

        autoUpdateInterval = setInterval(() => registration.update(), autoUpdateFrequencyMs)
    },
})

watch(offlineReady, (value) => {
    if (!value) return

    toast('Applicatie gedownload', {
        duration: 3_000,
        dismissible: true,
        description: 'De applicatie is nu offline beschikbaar.',
        icon: DownloadIcon,
    })
})

watch(needRefresh, (value) => {
    if (!value) return

    toast('Nieuw versie beschikbaar', {
        important: true,
        duration: Infinity,
        action: {
            label: 'Herladen',
            onClick: () => updateServiceWorker(true),
        },
    })
})
</script>

<template>
    <div class="hidden"></div>
</template>
