<script setup lang="ts">
import type { District } from '@/domain'
import { reactive } from 'vue'

const props = defineProps<{ value: District }>()
const district = reactive(props.value)
</script>

<template>
    <span class="district">
        <span class="md:hidden">{{ district.mobileName }}</span>
        <span class="hidden md:inline">{{ district.name }}</span>
    </span>
</template>

<style scoped>
.district {
    @apply inline-block py-0 px-1 leading-none text-xs uppercase tracking-wide;
    @apply bg-background text-foreground border-secondary-foreground;
}
</style>
