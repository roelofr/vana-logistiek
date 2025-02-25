<script setup lang="ts">
import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert'
import { TriangleAlert } from 'lucide-vue-next'
import type { Component } from 'vue'

const { title, icon, destructive } = defineProps<{
    title?: string
    icon?: Component
    destructive?: boolean
}>()

defineSlots<{
    default(): unknown
    title?(): unknown
}>()
</script>

<template>
    <Alert :variant="destructive ? 'destructive' : 'default'">
        <Component :is="icon ?? TriangleAlert" class="h-4 w-4" />
        <AlertTitle v-if="title">{{ title }}</AlertTitle>
        <AlertTitle v-else-if="$slots.title">
            <slot name="title" />
        </AlertTitle>
        <AlertDescription>
            <slot />
        </AlertDescription>
    </Alert>
</template>

<style scoped></style>
