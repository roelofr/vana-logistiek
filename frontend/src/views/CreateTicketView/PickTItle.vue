<script setup lang="ts">
import { Form } from '@/components/ui/form'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { toast } from 'vue-sonner'
import { AppInput } from '@/components/app'

const description = defineModel<null | string>({ required: true })

const emit = defineEmits(['submit'])

const checkSubmit = () => {
    if (!description.value) return toast.warning('Typ een verhaaltje.')

    emit('submit')
}
</script>

<template>
    <div class="space-y-4">
        <Form class="space-y-8">
            <Card>
                <CardHeader class="pb-3">
                    <CardTitle>Omschrijving</CardTitle>
                    <CardDescription> Houd het kort, anders worden we moe.</CardDescription>
                </CardHeader>
                <CardContent>
                    <AppInput
                        name="description"
                        label="Omschrijving"
                        v-model="description"
                        required
                    />
                </CardContent>
            </Card>

            <div class="text-right">
                <Button variant="default" @click.prevent="checkSubmit">Geil</Button>
            </div>
        </Form>
    </div>
</template>

<style scoped></style>
