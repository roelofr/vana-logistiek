<script setup lang="ts">
import { Heading, Paragraph } from '@/components/ui/typography'
import { Form } from '@/components/ui/form'
import { computed, ref } from 'vue'
import VendorPicker from '@/components/VendorPicker.vue'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import BigSelect, { type BigSelectOption } from '@/components/app/BigSelect.vue'
import { toast } from 'vue-sonner'
import { ticketTypes, type Vendor } from '@/domain'
import { Ticket } from 'lucide-vue-next'

const types: BigSelectOption[] = ticketTypes.map((type) => ({
    value: type.name,
    label: type.label,
    description: type.description,
    icon: type.icon,
}))

const vendor = ref<Vendor>()
const type = ref<string>(types[0].value)
const typeLabel = computed(() => types.find(({ value }) => value == type.value)?.label)

const click = () => {
    if (!vendor.value) return toast.warning('Standhouder ontbreekt')

    toast.success('Ticket aangemaakt', {
        icon: Ticket,
        description: `Voor ${vendor.value?.name} met soort ${typeLabel.value}`,
    })
}
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
                    <CardDescription>Wie wil bij jou een ei kwijt?</CardDescription>
                </CardHeader>
                <CardContent>
                    <VendorPicker v-model="vendor" />
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

            <div class="text-right">
                <Button variant="default" @click.prevent="click">OMG, slets go</Button>
            </div>
        </Form>
    </div>
</template>

<style scoped></style>
