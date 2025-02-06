<script setup lang="ts">
import { Button } from '@/components/ui/button'
import { AppContainer } from '@/components/app'
import { Heading, Paragraph } from '@/components/ui/typography'
import JSConfetti from 'js-confetti'
import { onBeforeUnmount, onMounted, ref, watch } from 'vue'
import { toast } from 'vue-sonner'

let jsConfetti: JSConfetti

const goodGirlCounter = ref(0)
watch(goodGirlCounter, (value) => {
    if (value % 5 === 0 && jsConfetti) jsConfetti.addConfetti()
})

const goodGirl = () => {
    goodGirlCounter.value++

    toast('Goof girl', {
        description: 'Tessa is a good girl',
    })
}

const badGirl = () => {
    toast('Bald girl', {
        description: 'Tessa is a bad girl, she clicked the button',
    })
}

onMounted(() => {
    jsConfetti = new JSConfetti()
})

onBeforeUnmount(() => jsConfetti && jsConfetti.destroyCanvas())
</script>

<template>
    <AppContainer content>
        <Heading level="1">Penis LogistiekApp</Heading>
        <Paragraph
            >Vet nieuw, cool, high-tech en beter op je mobiel te gebruiken dan MyVana.
        </Paragraph>
        <Paragraph
            >Voor technische vragen, mag je bij Roelof zijn. Voor poep-ideeën of vragen over de
            confetti, mag je bij Tessa zijn.
        </Paragraph>

        <div class="mt-6 flex gap-4 flex-wrap">
            <Button @click="goodGirl">Tessa hier klikken</Button>

            <Button variant="outline" @click="badGirl">Tessa hier niet klikken</Button>
        </div>
    </AppContainer>
</template>

<style scoped></style>
