<script setup lang="ts">
import { TicketTable } from '@/components/tables'
import { Heading } from '@/components/ui/typography'
import { AppAlert, AppContainer } from '@/components/app'
import { useQuery } from '@pinia/colada'
import { findAllTickets, TicketKeys } from '@/api'
import { Card, CardContent } from '@/components/ui/card'
import { LoaderCircle } from 'lucide-vue-next'
import type { Ticket } from '@/domain'
import { computed } from 'vue'

const { data, error, asyncStatus } = useQuery({
    key: [TicketKeys.Index],
    query: findAllTickets,
})

const safeData = computed(() => (data.value ?? []) as Ticket[])
</script>

<template>
    <AppContainer content>
        <Heading level="1">Ticketoverzicht</Heading>
        <template v-if="asyncStatus == 'loading'">
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
        <template v-else-if="error">
            <AppAlert title="Ophalen van data mislukt">
                De tickets konden niet worden opgehaald, sorry.
            </AppAlert>
        </template>
        <template v-else>
            <div class="py-10">
                <TicketTable :data="safeData" />
            </div>
        </template>
    </AppContainer>
</template>
