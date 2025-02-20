<script setup lang="ts">
import { AppContainer } from '@/components/app'
import PickVendor from '@/views/CreateTicketView/PickVendor.vue'
import PickType from '@/views/CreateTicketView/PickType.vue'
import { computed, ref } from 'vue'
import { ticketTypesMap, type Vendor } from '@/domain'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Heading, Paragraph } from '@/components/ui/typography'
import PickTitle from '@/views/CreateTicketView/PickTItle.vue'

const currentStep = ref(1)
const vendor = ref<null | Vendor>(null)
const type = ref<null | string>(null)
const desc = ref<null | string>(null)
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
            <Card v-else>
                <CardHeader>
                    <CardTitle>That's all folks</CardTitle>
                    <CardDescription>De rest is nog niet af :(</CardDescription>
                </CardHeader>

                <CardContent>
                    <dl class="grid grid-cols-1 gap-4 md:grid-cols-[250px_1fr]">
                        <dt class="font-bold">Standhouder</dt>
                        <dd class="text-muted-foreground">
                            {{ vendor!.name }} ({{ vendor!.number }})
                        </dd>

                        <dt class="font-bold">Type</dt>
                        <dd class="text-muted-foreground flex items-center">
                            <Component :is="currentType?.icon" class="h-4 w-4 mr-2" />
                            {{ currentType!.label }}
                        </dd>

                        <dt class="font-bold">Omschrijving</dt>
                        <dd class="text-muted-foreground flex items-center">
                            {{ desc }}
                        </dd>
                    </dl>
                </CardContent>
            </Card>
        </div>
    </AppContainer>
</template>

<style scoped></style>
