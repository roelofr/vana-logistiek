<script setup lang="ts">
import { h } from 'vue'
import { type ColumnDef, createColumnHelper } from '@tanstack/vue-table'
import type { Ticket } from '@/domain'
import { DataTable } from '@/components/ui/data-table'

const columnHelper = createColumnHelper<Ticket>()

const columns: ColumnDef<Ticket, string>[] = [
    columnHelper.accessor((row) => String(row.id), {
        id: 'id',
        header: () => h('div', { class: 'text-right' }, 'ID'),
        cell: (info) => h('div', { class: 'text-right font-medium' }, info.getValue()),
    }),
    columnHelper.accessor('description', {
        header: () => h('div', 'Titel'),
        cell: (info) => h('div', { class: 'font-medium' }, info.getValue()),
    }),
    columnHelper.accessor((row) => String(row.status), {
        id: 'status',
        header: () => h('div', 'Status'),
        cell: (info) => h('div', info.getValue()),
    }),
]

const { data } = defineProps<{ data: Ticket[] }>()
</script>

<template>
    <DataTable :columns="columns" :data="data" />
</template>

<style scoped></style>
