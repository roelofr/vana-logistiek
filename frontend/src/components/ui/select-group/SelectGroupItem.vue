<script setup lang="ts">
import { cn } from '@/lib/utils.ts'

const {
    value,
    label,
    description = null,
} = defineProps<{
    name: string
    value: string
    label: string
    description?: string
}>()
const model = defineModel()

const labelClass = cn(
    'group flex cursor-pointer border border p-4',
    'first:rounded-tl-md first:rounded-tr-md',
    'last:rounded-br-md last:rounded-bl-md',
    'focus:outline-hidden',
    'has-[:checked]:relative has-[:checked]:border-primary has-[:checked]:bg-accent',
)

const inputClass = cn(
    'relative mt-0.5 size-4 shrink-0 appearance-none rounded-full border border bg-background',
    'before:absolute before:inset-1 before:rounded-full before:bg-background',
    'not-checked:before:hidden checked:border-primary checked:bg-primary',
    'focus-visible:outline-2 focus-visible:outline-offset-2 focus-visible:outline-primary',
    'disabled:border-muted-foreground disabled:bg-muted disabled:before:bg-muted-foreground',
    'forced-colors:appearance-auto forced-colors:before:hidden',
)
</script>

<template>
    <label :value="value" :aria-label="label" :aria-description="description" :class="labelClass">
        <input :name="name" :value="label" type="radio" v-model="model" :class="inputClass" />
        <span class="ml-3 flex flex-col">
            <slot>
                <span
                    class="block text-sm font-medium text-foreground group-has-[:checked]:text-primary"
                >
                    {{ label }}
                </span>
                <span class="block text-sm text-muted-foreground group-has-[:checked]:text-primary">
                    {{ description }}
                </span>
            </slot>
        </span>
    </label>
</template>
