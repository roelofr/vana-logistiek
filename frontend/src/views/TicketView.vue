<script setup lang="ts">
import { AppContainer } from '@/components/app'
import TicketCreateVendor from './TicketView/TicketCreateVendor.vue'
import { ref } from 'vue'
import type { Vendor } from '@/domain'

const vendor = ref<Vendor>()
const type = ref()
</script>

<template>
    <AppContainer content size="narrow">
        <TicketCreateVendor v-model:vendor="vendor" v-model:type="type" />
    </AppContainer>
</template>

<style scoped></style>
