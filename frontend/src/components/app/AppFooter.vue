<script setup lang="ts">
import { ref } from 'vue'

const appVersion = ref(import.meta.env.VITE_APP_VERSION)
</script>

<template>
    <div class="px-4 py-2 mt-8">
        <div class="flex flex-row items-center justify-end text-muted-foreground">
            <span>Voor intern gebruik</span>
            <span>Versie {{ appVersion }}</span>
        </div>
    </div>
</template>
