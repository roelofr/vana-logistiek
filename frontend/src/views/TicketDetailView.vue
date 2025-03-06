<script setup lang="ts">
import { Heading, Paragraph } from '@/components/ui/typography'
import { AppAlert, AppContainer } from '@/components/app'
import { useQuery } from '@pinia/colada'
import { findTicket, TicketKeys } from '@/api'
import { Card, CardContent } from '@/components/ui/card'
import { LoaderCircle } from 'lucide-vue-next'
import { computed } from 'vue'

const { id: ticketId } = defineProps<{
    id: number
}>()

const {
    data: ticket,
    error,
    status,
} = useQuery({
    key: [TicketKeys.Index],
    query: () => findTicket(ticketId),
})
const intlDateFormat = new Intl.DateTimeFormat(undefined, { dateStyle: 'short' })

const formatDateIfPresent = (date: Date | undefined | null) => {
    return !date ? null : intlDateFormat.format(date)
}

const encodedValue = computed(() =>
    ticket.value != null ? JSON.stringify(ticket.value, undefined, 2) : undefined,
)

const localeCreated = computed(() => formatDateIfPresent(ticket.value?.created_at))
const localeUpdated = computed(() => formatDateIfPresent(ticket.value?.updated_at))
</script>

<template>
    <AppContainer content>
        <Heading level="1">{{ ticket?.description ?? `Ticket ${ticketId}` }}</Heading>
        <Paragraph class="text-muted-foreground">
            <ul class="list-none flex gap-4">
                <li>Ticket #{{ ticketId }}</li>
                <li v-if="localeCreated">Aangemaakt {{ localeCreated }}</li>
                <li v-if="localeUpdated">Bijgewerkt {{ localeUpdated }}</li>
            </ul>
        </Paragraph>
        <template v-if="status == 'pending'">
            <Card>
                <CardContent class="flex p-8 items-center justify-center">
                    <div class="flex flex-col items-center">
                        <div class="flex items-center space-x-4 px-10 py-20">
                            <LoaderCircle class="animate-spin h-6" />
                            <span class="text-lg">Frustratie doorgeven...</span>
                        </div>
                    </div>
                </CardContent>
            </Card>
        </template>
        <template v-else-if="error || status == 'error'">
            <AppAlert title="Ophalen van data mislukt">
                Het ticket konden niet worden opgehaald, sorry.
            </AppAlert>
        </template>
        <template v-else>
            <div class="container py-10 mx-auto">
                <pre><code>{{ encodedValue }}</code></pre>
            </div>
        </template>
    </AppContainer>
</template>
