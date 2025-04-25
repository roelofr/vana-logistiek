<script setup lang="ts">
import { Button } from '@/components/ui/button'
import { useColorMode } from '@vueuse/core'
import { MoonStar, Sun } from 'lucide-vue-next'

const mode = useColorMode({ disableTransition: false })

const toggle = () => {
    mode.value = mode.state.value === 'dark' ? 'light' : 'dark'
}
</script>

<template>
    <Button
        variant="outline"
        @click="toggle"
        class="w-full flex items-center justify-start gap-x-2"
    >
        <Sun class="h-5 w-5 rotate-0 scale-100 transition-all dark:-rotate-90 dark:scale-0" />
        <MoonStar
            class="absolute h-5 w-5 rotate-90 scale-0 transition-all dark:rotate-0 dark:scale-100"
        />
        <span class="font-bold">Wissel thema</span>
    </Button>
</template>
