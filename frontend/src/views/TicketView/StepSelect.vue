<script setup lang="ts">
import { Heading, Paragraph } from '@/components/ui/typography'
import { Form } from '@/components/ui/form'
import { SelectGroup, SelectGroupItem } from '@/components/ui/select-group'
import { type Ticket } from '@/views/TicketView/SelectType.vue'
import { ref } from 'vue'
import VendorPicker from '@/components/VendorPicker.vue'

const types: Ticket[] = [
    {
        key: 'bijbestelling',
        title: 'Bijbestelling',
        description: 'Materialen direct verstrekt aan standhouder',
        signature: true,
    },
    {
        key: 'aanvraag',
        title: 'Aanvraag',
        description: 'Materiaalaanvraag bij derden (Gator, heftruck, Keukenhof)',
    },
    {
        key: 'controle',
        title: 'Controle',
        description: 'Speed-dial de hulp van Tessa',
    },
]

const type = ref<Ticket>()
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
            <div>
                <fieldset title="Standhouder">
                    <VendorPicker />
                </fieldset>
            </div>

            <div>
                <fieldset title="Type ticket">
                    <SelectGroup label="Type ticket" class="md:grid grid-cols-3 gap-8">
                        <SelectGroupItem
                            v-for="ticket of types"
                            v-model="type"
                            name="ticket-type"
                            :key="ticket.key"
                            :value="ticket.key"
                            :label="ticket.title"
                            :description="ticket.description"
                        />
                    </SelectGroup>
                </fieldset>
            </div>
        </Form>
    </div>
</template>

<style scoped></style>
