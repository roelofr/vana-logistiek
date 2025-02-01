<script setup type="ts">
import { reactive } from 'vue'

const props = defineProps({
  content: {
    type: Boolean,
    default: false
  },
  fullWidth: {
    type: Boolean,
    default: false
  }
})

const containerClass = reactive({
  'container': true,
  'max-w-7xl': !props.fullWidth,
  'py-4 lg:py-6': props.content
})

</script>

<template>
  <div :class="containerClass">
    <slot />
  </div>
</template>
