<script setup lang="ts">
import { useRegisterSW } from 'virtual:pwa-register/vue'
import { toast } from 'vue-sonner'
import { watch } from 'vue'
import { DownloadIcon } from 'lucide-vue-next'

const autoUpdateFrequencyMs = 60 * 60 * 1000
let autoUpdateInterval: number = 0

const { offlineReady, needRefresh, updateServiceWorker } = useRegisterSW({
    onRegisteredSW(scriptUrl, registration) {
        if (!registration) return

        console.log('ServiceWorker registered via URL %s', scriptUrl)

        if (autoUpdateInterval) clearInterval(autoUpdateInterval)

        autoUpdateInterval = setInterval(
            registration.update.bind(registration) as TimerHandler,
            autoUpdateFrequencyMs,
        )
    },
})

watch(offlineReady, (value) => {
    if (!value) return

    toast('Applicatie gedownload', {
        duration: 30_000,
        dismissible: true,
        description: 'De applicatie is gedownload, dus werkt nu ook met minder goede verbindingen.',
        icon: DownloadIcon,
    })
})

watch(needRefresh, (value) => {
    if (!value) return

    toast('Nieuw versie beschikbaar', {
        dismissible: true,
        duration: 0,
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
