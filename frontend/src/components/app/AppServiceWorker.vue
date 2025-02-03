<script setup lang="ts">
import { useRegisterSW } from 'virtual:pwa-register/vue'
import { toast } from 'vue-sonner'
import { watch } from 'vue'

const { offlineReady, needRefresh, updateServiceWorker } = useRegisterSW()

watch(offlineReady, (value) => {
    if (!value) return

    toast('Applicatie gedownload', {
        description: 'De applicatie is gedownload, dus werkt nu ook met minder goede verbindingen.',
        duration: 10_000,
    })
})

watch(needRefresh, (value) => {
    if (!value) return

    toast('Nieuw versie beschikbaar', {
        dismissible: true,
        duration: 0,
        action: {
            label: 'Herladen',
            onClick: updateServiceWorker,
        },
    })
})
</script>
