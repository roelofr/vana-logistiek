<script setup lang="ts">
import type { Vendor } from '@/domain'
import DistrictName from '@/components/app/DistrictName.vue'

const { vendor } = defineProps<{ vendor: Vendor }>()
</script>

<template>
    <div class="w-full grid vendor-grid items-center gap-2">
        <div v-if="vendor.district">
            <DistrictName :value="vendor.district" />
        </div>
        <div v-else>&nbsp;</div>
        <div class="text-muted-foreground text-right">{{ vendor.number }}</div>
        <div class="text-foreground flex-grow-1 min-h-1">{{ vendor.name }}</div>
    </div>
</template>

<style scoped>
.vendor-grid {
    grid-template-columns: 1.5rem 3rem 1fr;
}

@screen md {
    grid-template-columns: minmax(5rem, max-content) 3rem 1fr;
}
</style>
