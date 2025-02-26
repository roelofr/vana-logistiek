<script setup lang="ts">
import { Form } from '@/components/ui/form'
import VendorPicker from '@/components/VendorPicker.vue'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { toast } from 'vue-sonner'
import { type Vendor } from '@/domain'
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { TriangleAlert } from 'lucide-vue-next'

const vendor = defineModel<null | Vendor>({ required: true })

const emit = defineEmits(['submit'])

const checkSubmit = () => {
    if (!vendor.value) return toast.warning('Selecteer een standhouder')

    emit('submit')
}
</script>

<template>
    <div class="space-y-4">
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

            <Alert>
                <TriangleAlert class="h-4 w-4" />
                <AlertTitle>Geen spoedgevallen</AlertTitle>
                <AlertDescription>
                    Gebruik dit formulier niet voor spoedgevallen, daar heb je een portfoon of
                    telefoon voor.
                </AlertDescription>
            </Alert>

            <div class="text-right">
                <Button variant="default" @click.prevent="checkSubmit">OMG, slets go</Button>
            </div>
        </Form>
    </div>
</template>

<style scoped></style>
