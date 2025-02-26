<script setup lang="ts">
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Check } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
import { type TicketType, type Vendor } from '@/domain'
import { saveTicket, TicketKeys } from '@/api'
import { useRouter } from 'vue-router'
import { useMutation, useQueryCache } from '@pinia/colada'
import { confetti } from '@/lib'
import { toast } from 'vue-sonner'

const router = useRouter()
const { vendor, type, title } = defineProps<{
    vendor: Vendor
    type: TicketType
    title: string
}>()

const queryCache = useQueryCache()
const { mutate, state: mutateState } = useMutation({
    mutation: saveTicket,
    onSettled() {
        queryCache.invalidateQueries({
            key: [TicketKeys.Index],
        })
    },
})

const doSave = async (): Promise<number> => {
    await mutate({
        vendorId: vendor.id,
        description: title,
    })

    const state = mutateState.value

    if (state.status === 'error') throw new Error(String(state.error))

    const ticket = state.data
    if (!ticket || !ticket.id) {
        throw new Error('Fout bij aanmaken ticket!')
    }

    await router.push({ name: 'ticket.show', params: { id: ticket.id } })

    confetti()

    return ticket.id
}

const save = async () => {
    toast.promise(doSave, {
        loading: 'Ticket wordt aangemaakt...',
        success: (ticketId: number) => `Ticket #${ticketId} is aangemaakt.`,
        error: (error: unknown) => `Fout: ${error}`,
    })
}
</script>

<template>
    <Card>
        <CardHeader>
            <CardTitle>That's all folks</CardTitle>
            <CardDescription>De rest is nog niet af :(</CardDescription>
        </CardHeader>

        <CardContent>
            <dl class="grid grid-cols-1 gap-4 md:grid-cols-[250px_1fr]">
                <dt class="font-bold">Standhouder</dt>
                <dd class="text-muted-foreground">{{ vendor.name }} ({{ vendor.number }})</dd>

                <dt class="font-bold">Type</dt>
                <dd class="text-muted-foreground flex items-center">
                    <Component :is="type.icon" class="h-4 w-4 mr-2" />
                    {{ type.label }}
                </dd>

                <dt class="font-bold">Omschrijving</dt>
                <dd class="text-muted-foreground flex items-center">
                    {{ title }}
                </dd>
            </dl>
        </CardContent>
    </Card>

    <div class="text-right">
        <Button variant="default" @click.prevent="save" class="flex items-center gap-2">
            <Check class="h-4" />
            Opslaan
        </Button>
    </div>
</template>

<style scoped></style>
