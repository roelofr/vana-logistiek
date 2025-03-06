<script setup lang="ts">
import { AppContainer } from '@/components/app'
import PickVendor from '@/views/TicketCreateView/PickVendor.vue'
import PickType from '@/views/TicketCreateView/PickType.vue'
import { computed, ref } from 'vue'
import type { TicketType, Vendor } from '@/domain'
import { ticketTypesMap } from '@/domain'
import { Heading, Paragraph } from '@/components/ui/typography'
import PickTitle from '@/views/TicketCreateView/PickTitle.vue'
import SummaryAndSave from '@/views/TicketCreateView/SummaryAndSave.vue'

const currentStep = ref(1)
const vendor = ref<Vendor | null>(null)
const type = ref<string | null>(null)
const desc = ref<string | null>(null)
const currentType = computed(() => {
    if (type.value) return ticketTypesMap.get(type.value)

    return null
})
</script>
<template>
    <AppContainer content size="narrow">
        <div class="space-y-4">
            <div>
                <Heading level="1">Nieuw ticket</Heading>
                <Paragraph>
                    Klik onderstaande <em>wizard</em> door om een ticket aan te maken.
                </Paragraph>
            </div>

            <PickVendor v-if="currentStep == 1" v-model="vendor" @submit="currentStep = 2" />
            <PickType v-else-if="currentStep == 2" v-model="type" @submit="currentStep = 3" />
            <PickTitle v-else-if="currentStep == 3" v-model="desc" @submit="currentStep = 4" />
            <SummaryAndSave
                v-else
                :type="currentType as TicketType"
                :vendor="vendor as Vendor"
                :title="desc as string"
            />
        </div>
    </AppContainer>
</template>

<style scoped></style>
