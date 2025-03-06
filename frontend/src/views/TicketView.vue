<script setup lang="ts">
import { TicketTable } from '@/components/tables'
import { Heading } from '@/components/ui/typography'
import { AppAlert, AppContainer } from '@/components/app'
import { useQuery } from '@pinia/colada'
import { findAllTickets, TicketKeys } from '@/api'
import { Card, CardContent } from '@/components/ui/card'
import { LoaderCircle } from 'lucide-vue-next'
import { ref, watch } from 'vue'
import type { Ticket } from '@/domain'

const {
    data: queryData,
    error,
    status,
} = useQuery({
    key: [TicketKeys.Index],
    query: findAllTickets,
})

const data = ref<Ticket[]>([])
watch(status, (newStatus) => {
    if (newStatus === 'success') data.value = queryData.value as Ticket[]
    else data.value = []
})
</script>

<template>
    <AppContainer content>
        <Heading level="1">Ticketoverzicht</Heading>
        <template v-if="status == 'pending'">
            <Card>
                <CardContent class="flex p-8 items-center justify-center">
                    <div class="flex flex-col items-center">
                        <div class="flex items-center space-x-4 px-10 py-20">
                            <LoaderCircle class="animate-spin h-6" />
                            <span class="text-lg">Tickets worden geladen...</span>
                        </div>
                    </div>
                </CardContent>
            </Card>
        </template>
        <template v-else-if="error || status == 'error'">
            <AppAlert title="Ophalen van data mislukt">
                De tickets konden niet worden opgehaald, sorry.
            </AppAlert>
        </template>
        <template v-else>
            <div class="py-10">
                <TicketTable :data="data" />
            </div>
        </template>
    </AppContainer>
</template>
