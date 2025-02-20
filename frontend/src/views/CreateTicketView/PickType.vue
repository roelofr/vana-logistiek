<script setup lang="ts">
import { Form } from '@/components/ui/form'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import BigSelect, { type BigSelectOption } from '@/components/app/BigSelect.vue'
import { toast } from 'vue-sonner'
import { ticketTypes } from '@/domain'

const types: BigSelectOption[] = ticketTypes.map((type) => ({
    value: type.name,
    label: type.label,
    description: type.description,
    icon: type.icon,
}))

const type = defineModel<null | string>({ required: true })

const emit = defineEmits(['submit'])

const checkSubmit = () => {
    if (!type.value) return toast.warning('Selecteer een type.')

    emit('submit')
}
</script>

<template>
    <div class="space-y-4">
        <Form class="space-y-8">
            <Card>
                <CardHeader class="pb-3">
                    <CardTitle>Type ticket</CardTitle>
                    <CardDescription
                        >Wat voor soort ticket wordt dit en welke kleisoort voel je hierbij?
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <BigSelect v-model="type" :options="types" />
                </CardContent>
            </Card>

            <div class="text-right">
                <Button variant="default" @click.prevent="checkSubmit">Carry on...</Button>
            </div>
        </Form>
    </div>
</template>

<style scoped></style>
