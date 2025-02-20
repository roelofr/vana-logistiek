<script setup lang="ts">
import { SquareDashed } from 'lucide-vue-next'
import { cn } from '@/lib'
import type { Component } from 'vue'

export interface BigSelectOption {
    value: string
    icon?: Component
    label: string
    description: string
}

const model = defineModel<null | string>({ required: true })
const { options } = defineProps<{ options: BigSelectOption[] }>()

const isActive = (option: BigSelectOption) => {
    return option.value === model.value
}
const clickHandler = (option: BigSelectOption) => {
    model.value = option.value
}

const optionClass = (row: BigSelectOption) => {
    return cn(
        '-mx-2 flex items-start space-x-4 rounded-md p-2 transition-all',
        isActive(row)
            ? 'bg-accent text-accent-foreground'
            : 'hover:bg-accent hover:text-accent-foreground',
    )
}
</script>

<template>
    <div class="grid gap-1">
        <div
            role="button"
            v-for="row in options"
            :key="row.value"
            :class="optionClass(row)"
            @click.prevent="clickHandler(row)"
        >
            <component :is="row.icon ?? SquareDashed" class="mt-px h-5 w-5" />
            <div class="space-y-1">
                <p class="text-sm font-medium leading-none">
                    {{ row.label }}
                </p>
                <p class="text-sm text-muted-foreground" v-if="row.description">
                    {{ row.description }}
                </p>
            </div>
        </div>
    </div>
</template>
