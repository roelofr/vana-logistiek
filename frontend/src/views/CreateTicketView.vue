<script setup lang="ts">
import { AppContainer } from '@/components/app'
import PickVendor from '@/views/CreateTicketView/PickVendor.vue'
import PickType from '@/views/CreateTicketView/PickType.vue'
import { computed, ref } from 'vue'
import { ticketTypesMap, type Vendor } from '@/domain'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Heading, Paragraph } from '@/components/ui/typography'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { TriangleAlert } from 'lucide-vue-next'

const currentStep = ref(1)
const vendor = ref<null | Vendor>(null)
const type = ref<null | string>(null)
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

            <Alert v-if="currentStep == 1">
                <TriangleAlert class="h-4 w-4" />
                <AlertTitle>Geen spoedgevallen</AlertTitle>
                <AlertDescription>
                    Gebruik dit formulier niet voor spoedgevallen, daar heb je een portfoon of
                    telefoon voor.
                </AlertDescription>
            </Alert>

            <PickVendor v-if="currentStep == 1" v-model="vendor" @submit="currentStep = 2" />
            <PickType v-else-if="currentStep == 2" v-model="type" @submit="currentStep = 3" />
            <Card v-else>
                <CardHeader>
                    <CardTitle>That's all folks</CardTitle>
                    <CardDescription>De rest is nog niet af :(</CardDescription>
                </CardHeader>

                <CardContent>
                    <dl class="grid grid-cols-[250px_1fr] gap-4">
                        <dt class="font-bold">Standhouder</dt>
                        <dd class="text-muted-foreground">
                            {{ vendor!.name }} ({{ vendor!.number }})
                        </dd>

                        <dt class="font-bold">Type</dt>
                        <dd class="text-muted-foreground flex items-center">
                            <Component :is="currentType?.icon" class="h-4 w-4 mr-2" />
                            {{ currentType!.label }}
                        </dd>
                    </dl>
                </CardContent>
            </Card>
        </div>
    </AppContainer>
</template>

<style scoped></style>
