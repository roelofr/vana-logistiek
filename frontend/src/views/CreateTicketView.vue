<script setup lang="ts">
import { AppContainer } from '@/components/app'
import PickVendor from '@/views/CreateTicketView/PickVendor.vue'
import PickType from '@/views/CreateTicketView/PickType.vue'
import { ref } from 'vue'
import type { Vendor } from '@/domain'
import { Card, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'

const currentStep = ref(1)
const vendor = ref<null | Vendor>(null)
const type = ref<null | string>(null)
</script>

<template>
    <AppContainer content size="narrow">
        <PickVendor v-if="currentStep == 1" v-model="vendor" @submit="currentStep = 2" />
        <PickType v-else-if="currentStep == 2" v-model="type" @submit="currentStep = 3" />
        <Card v-else>
            <CardHeader>
                <CardTitle>That's all folks</CardTitle>
                <CardDescription>De rest is nog niet af :(</CardDescription>
            </CardHeader>
        </Card>
    </AppContainer>
</template>

<style scoped></style>
