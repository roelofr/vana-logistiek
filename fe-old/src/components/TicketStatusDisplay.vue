<script setup lang="ts">
import { TicketStatus, TicketStatusDetails } from '@/domain'
import { computed } from 'vue'
import { Badge } from '@/components/ui/badge'

const props = defineProps<{
    status: TicketStatus
}>()

const statusDetails = computed(
    () =>
        TicketStatusDetails.get(props.status) ??
        TicketStatusDetails.get(props.status.toLowerCase() as TicketStatus),
)
</script>

<template>
    <template v-if="statusDetails">
        <Badge variant="outline" class="inline-flex items-center gap-2">
            <Component :is="statusDetails.icon" class="h-4 w-4" />
            {{ statusDetails.label }}
        </Badge>
    </template>
    <Badge v-else variant="destructive">Ongeldige status</Badge>
</template>

<style scoped></style>
