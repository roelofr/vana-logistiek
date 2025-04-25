<script setup lang="ts">
import {
    FormControl,
    FormDescription,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from '@/components/ui/form'
import { Input } from '@/components/ui/input'

const props = defineProps({
    name: {
        type: String,
        required: true,
    },
    label: {
        type: String,
        required: false,
        default: (props: { name: string }) => props.name,
    },
    type: {
        type: String,
        required: false,
        default: 'text',
    },
    required: {
        type: Boolean,
        default: false,
    },
    description: {
        type: String,
        default: null,
    },
    placeholder: {
        type: String,
        default: null,
    },
})

const model = defineModel({ required: true })
</script>

<template>
    <FormField v-model="model" v-slot="{ componentField }" :label="props.label" :name="props.name">
        <FormItem>
            <FormLabel>{{ props.label }}</FormLabel>
            <FormControl>
                <Input
                    :type="props.type"
                    :placeholder="props.placeholder"
                    v-bind="componentField"
                    :required="props.required"
                />
            </FormControl>
            <FormDescription v-if="$slots.description">
                <slot name="description" />
            </FormDescription>
            <FormDescription v-else-if="props.description">
                {{ props.description }}
            </FormDescription>
            <FormMessage />
        </FormItem>
    </FormField>
</template>
