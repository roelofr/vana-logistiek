<script setup lang="ts">
const { label } = defineProps<{ label: string }>()
</script>

<template>
    <fieldset :aria-label="label" class="-space-y-px rounded-md bg-background">
        <slot />
    </fieldset>
</template>
