<script setup lang="ts">
import { AppContainer } from '@/components/app'
import StepSelect from './TicketView/StepSelect.vue'
import { ref } from 'vue'
import type { Vendor } from '@/domain'

const vendor = ref<Vendor>()
const type = ref()
</script>

<template>
    <AppContainer content size="narrow">
        <StepSelect v-model:vendor="vendor" v-model:type="type" />
    </AppContainer>
</template>

<style scoped></style>
