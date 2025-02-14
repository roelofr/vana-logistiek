<script setup lang="ts">
import { onBeforeMount, ref } from 'vue'
import AppContainer from '@/components/app/AppContainer.vue'
import { Heart, TriangleAlert } from 'lucide-vue-next'

const clientVersion = import.meta.env.VITE_APP_VERSION
const serverVersion = ref()

onBeforeMount(() => {
    fetch('/api/version')
        .then((res) => res.json())
        .then((body) => (body.version ? (serverVersion.value = body.version) : null))
        .catch((error) => console.warn('Version downloading has failed :(', error))
})
</script>

<template>
    <AppContainer content>
        <div
            class="flex flex-col items-end md:flex-row md:items-center justify-end gap-4 text-muted-foreground text-sm"
        >
            <span class="hidden md:inline"
                >Gemaakt met <Heart class="inline-block h-4" /> in Zwollywood</span
            >
            <span class="hidden md:block flex-grow"></span>
            <span class="hidden md:inline">
                <TriangleAlert class="inline-block h-4" />
                Product may contain traces of glitter
            </span>
            <span v-if="serverVersion">Versie {{ clientVersion }} / {{ serverVersion }}</span>
            <span v-else>Versie {{ clientVersion }}</span>
        </div>
    </AppContainer>
</template>
