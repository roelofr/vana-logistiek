<script setup lang="ts">
import { Heading, Paragraph } from '@/components/ui/typography'
import { Form } from '@/components/ui/form'
import { ref } from 'vue'
import VendorPicker from '@/components/VendorPicker.vue'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import BigSelect, { type BigSelectOption } from '@/components/app/BigSelect.vue'

const types: BigSelectOption[] = [
    {
        value: 'bijbestelling',
        label: 'Bijbestelling',
        description: 'Materialen direct verstrekt aan standhouder',
    },
    {
        value: 'aanvraag',
        label: 'Aanvraag',
        description: 'Materiaalaanvraag bij derden (Gator, heftruck, Keukenhof)',
    },
    {
        value: 'controle',
        label: 'Controle',
        description: 'Speed-dial de hulp van Tessa',
    },
]

const type = ref<string>(types[0].value)
</script>

<template>
    <div class="space-y-4">
        <div>
            <Heading level="1">Nieuw ticket</Heading>
            <Paragraph>
                Kies je standhouder en wat voor soort ticket je aan wilt maken. Daarna komt de rest.
            </Paragraph>
        </div>

        <Form class="space-y-8">
            <Card>
                <CardHeader class="pb-3">
                    <CardTitle>Standhouder</CardTitle>
                    <CardDescription>Wie wil bij jou een ei kwijt? </CardDescription>
                </CardHeader>
                <CardContent>
                    <VendorPicker />
                </CardContent>
            </Card>

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
        </Form>
    </div>
</template>

<style scoped></style>
