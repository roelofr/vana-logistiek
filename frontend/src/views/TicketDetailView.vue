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
    data: queryData,
    error,
    status,
} = useQuery({
    key: [TicketKeys.Index],
    query: () => findTicket(ticketId),
})

const encodedValue = computed(() =>
    queryData != null ? JSON.stringify(queryData, undefined, 2) : undefined,
)
</script>

<template>
    <AppContainer content>
        <Heading level="1">Ticket {{ ticketId }}</Heading>
        <Paragraph class="text-muted-foreground"> Wow, een ticket! </Paragraph>
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
                <pre><code>
                    {{ encodedValue }}
                </code></pre>
            </div>
        </template>
    </AppContainer>
</template>
