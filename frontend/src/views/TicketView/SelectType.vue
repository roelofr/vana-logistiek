<script setup lang="ts">
import { type Component, ref, watch } from 'vue'
import {
    FormControl,
    FormDescription,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from '@/components/ui/form'
import {
    Select,
    SelectContent,
    SelectGroup,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from '@/components/ui/select'

export type Ticket = {
    key: string
    title: string
    description: string
    signature?: boolean
    icon?: Component
}

const { name, types } = defineProps<{ name: string; types: Ticket[] }>()
const model = defineModel<Ticket>({ default: null })

const activeKey = ref(model.value?.key)

watch(activeKey, (newKey) => {
    const foundTicket = types.find((ticket) => ticket.key == newKey)
    if (foundTicket) model.value = foundTicket
})
</script>

<template>
    <FormField v-slot="{ componentField }" :name="name">
        <FormItem>
            <FormLabel>Ticket type</FormLabel>

            <Select v-bind="componentField">
                <FormControl>
                    <SelectTrigger>
                        <SelectValue placeholder="Selecteer een type melding" />
                    </SelectTrigger>
                </FormControl>
                <SelectContent>
                    <SelectGroup>
                        <template v-for="{ key, title } of types" :key="key">
                            <SelectItem :value="key"> {{ title }}}</SelectItem>
                        </template>
                    </SelectGroup>
                </SelectContent>
            </Select>
            <FormDescription>
                You can manage email addresses in your
                <a href="/examples/forms">email settings</a>.
            </FormDescription>
            <FormMessage />
        </FormItem>
    </FormField>
</template>
