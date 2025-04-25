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
const { mutateAsync, isLoading } = useMutation({
    mutation: saveTicket,
    onSettled() {
        queryCache.invalidateQueries({
            key: [TicketKeys.Index],
        })
    },
})

const save = async () => {
    const toastId = toast.loading('Ticket wordt aangemaakt...', {
        dismissible: false,
        duration: Infinity,
    })
    try {
        const ticket = await mutateAsync({
            vendorId: vendor.id,
            description: title,
        })

        if (!ticket || !ticket.id) {
            throw new Error('Fout bij aanmaken ticket!')
        }

        toast.success('Ticket aangemaakt', {
            id: toastId,
            duration: 500,
            dismissible: true,
        })

        confetti()

        await router.push({ name: 'ticket.show', params: { id: ticket.id } })
    } catch (ex) {
        const errorMsg = ex instanceof Error ? ex.message : 'Fout bij aanmaken ticket.'
        toast.error(errorMsg, {
            id: toastId,
            duration: 3_000,
            dismissible: true,
        })
    } finally {
        // Catcher in case of weird stuff
        setTimeout(() => {
            if (toastId) toast.dismiss(toastId)
        }, 5_000)
    }
}
</script>

<template>
    <Card>
        <CardHeader>
            <CardTitle>Je nieuwe ticket</CardTitle>
            <CardDescription>
                Check hieronder of de samenvatting klopt. Na het aanmaken kan je meer toevoegen.
            </CardDescription>
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
        <Button
            variant="default"
            @click.prevent="save"
            class="flex items-center gap-2"
            :disabled="isLoading"
        >
            <Check class="h-4" />
            Opslaan
        </Button>
    </div>
</template>

<style scoped></style>
