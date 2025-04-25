<script setup lang="ts">
const { level } = defineProps({
    level: {
        type: [Number, String],
        validator(value: unknown): boolean {
            if (!value || (typeof value !== 'string' && typeof value !== 'number')) return false

            const valueNumber = Number(value)
            if (!Number.isInteger(valueNumber)) return false

            return valueNumber >= 1 && valueNumber <= 4
        },
    },
})
</script>

<template>
    <h1 v-if="level == 1" class="scroll-m-20 text-4xl font-extrabold tracking-tight lg:text-5xl">
        <slot />
    </h1>
    <h2
        v-else-if="level == 2"
        class="scroll-m-20 border-b pb-2 text-3xl font-semibold tracking-tight transition-colors first:mt-0"
    >
        <slot />
    </h2>
    <h3 v-else-if="level == 3" class="scroll-m-20 text-2xl font-semibold tracking-tight">
        <slot />
    </h3>
    <h4 v-else-if="level == 4" class="scroll-m-20 text-xl font-semibold tracking-tight">
        <slot />
    </h4>
</template>
