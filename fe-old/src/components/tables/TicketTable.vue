<script setup lang="ts">
import { h } from 'vue'
import { type ColumnDef, createColumnHelper } from '@tanstack/vue-table'
import type { Ticket } from '@/domain'
import { DataTable } from '@/components/ui/data-table'
import { TicketStatusDisplay } from '@/components'
import { TableCell } from '@/components/ui/table'

const columnHelper = createColumnHelper<Ticket>()

const columns: ColumnDef<Ticket, string>[] = [
    columnHelper.accessor((row) => String(row.id), {
        id: 'id',
        header: () => h('div', { class: 'text-right' }, 'ID'),
    }),
    columnHelper.accessor('description', {
        header: () => h('div', 'Titel'),
    }),
    columnHelper.accessor((row) => String(row.status), {
        id: 'status',
        header: () => h('div', 'Status'),
    }),
]

const { data } = defineProps<{ data: Ticket[] }>()
</script>

<template>
    <DataTable v-bind="{ columns, data }">
        <template v-slot:row="{ data }">
            <TableCell>
                {{ data.id }}
            </TableCell>
            <TableCell>
                <RouterLink :to="{ name: 'ticket.show', params: { id: data.id } }">
                    {{ data.description }}
                </RouterLink>
            </TableCell>
            <TableCell>
                <div>
                    <TicketStatusDisplay :status="data.status" />
                </div>
            </TableCell>
        </template>
    </DataTable>
</template>

<style scoped></style>
