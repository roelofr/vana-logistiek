<script setup lang="ts">
import type { Vendor } from '@/domain'
import DistrictName from '@/components/app/DistrictName.vue'

const props = defineProps<{ vendor: Vendor }>()
</script>

<template>
    <div class="w-full grid gap-4 vendor-grid items-center gap-2">
        <div>
            <DistrictName :value="props.vendor.district" />
        </div>
        <div class="text-muted-foreground text-right">{{ props.vendor.number }}</div>
        <div class="text-foreground flex-grow-1 min-h-1">{{ props.vendor.name }}</div>
    </div>
</template>

<style scoped>
.vendor-grid {
    grid-template-columns: minmax(5rem, max-content) 2rem 1fr;
}
</style>
