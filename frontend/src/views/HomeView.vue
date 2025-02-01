<script setup lang="ts">
import { Button } from '@/components/ui/button'
import AppContainer from '@/components/AppContainer.vue'
</script>

<script lang="ts">
import { toast } from 'vue-sonner'

const goodGirl = () => {
  toast('Good girl', {
    description: 'Tessa is a good girl'
  })
}
</script>

<template>
  <AppContainer content>
    <div>
      <Button @click="goodGirl">Click me</Button>
    </div>
  </AppContainer>
</template>

<style scoped></style>
