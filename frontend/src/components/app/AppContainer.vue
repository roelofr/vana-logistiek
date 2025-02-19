<script setup lang="ts">
import { reactive } from 'vue'

const props = defineProps({
    content: {
        type: Boolean,
        default: false,
    },
    size: {
        type: String,
        default: 'normal',
        validator(value: string): boolean {
            return ['wide', 'normal', 'narrow'].includes(value)
        },
    },
})

const containerClass = reactive({
    container: true,
    'max-w-7xl': props.size === 'normal',
    'max-w-4xl': props.size === 'narrow',
})
</script>

<template>
    <div :class="containerClass">
        <slot />
    </div>
</template>
