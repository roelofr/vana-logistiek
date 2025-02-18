<script setup lang="ts">
import type { District } from '@/domain'

const { value } = defineProps<{ value: District }>()
</script>

<template>
    <span class="district">
        <span class="md:hidden">{{ value.mobileName }}</span>
        <span class="hidden md:inline">{{ value.name }}</span>
    </span>
</template>

<style scoped>
.district {
    @apply inline-block py-0 px-1 leading-none text-xs uppercase tracking-wide;
    @apply bg-background text-foreground border-secondary-foreground;
}
</style>
