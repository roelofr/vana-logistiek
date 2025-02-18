<script setup lang="ts">
import { AppContainer } from '@/components/app'
import { ref } from 'vue'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from '@/components/ui/dialog'
import { toast } from 'vue-sonner'

const open = ref(false)

const reacties = [
    'Ja, je mag slaan.',
    'Dat is inderdaad stommiepommie',
    'Nee :(',
    'Nee :)',
    '[Diepe zucht]... Ik leef met je mee.',
    'Wil je een snoepje?',
    'Wil je een koekje?',
    'Standhouders zijn kut.',
    'Had je maar meer moeten eten',
    'Had je maar meer moeten drinken',
    'Standhouder heeft nooit gelijk',
    'Heb je al zonnebrand gebruikt?',
    'Jeetje.',
]

const reageer = () => {
    const reactie = reacties[Math.floor(Math.random() * reacties.length)]
    toast('Reactie van Tessa', {
        description: reactie,
        duration: 0,
        dismissible: true,
        action: {
            label: 'Bedankt Tessa',
            onClick: () => {
                document.dispatchEvent(new CustomEvent('confetti', { detail: 'sad' }))
            },
        },
    })

    open.value = false
}
</script>

<template>
    <AppContainer content size="narrow">
        <Dialog v-model:open="open">
            <Card>
                <CardHeader class="mb-8">
                    <CardTitle>Klaaginator</CardTitle>
                    <CardDescription>
                        Soms moet dat, soms zit het allemaal even tegen.
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <DialogTrigger as-child>
                        <Button>Start klagen</Button>
                    </DialogTrigger>
                </CardContent>
            </Card>
            <DialogContent>
                <DialogHeader>
                    <DialogTitle>Klaag-i-nator</DialogTitle>
                    <DialogDescription
                        class="px-10 py-20 text-center text-4xl font-extrabold tracking-tight lg:text-5xl klaagtekst"
                    >
                        Nu klagen
                    </DialogDescription>
                </DialogHeader>

                <DialogFooter>
                    <Button @click="reageer()">Reageren, a.u.b.</Button>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    </AppContainer>
</template>

<style scoped>
.klaagtekst {
    pointer-events: none;
    position: relative;
    animation: klaag 600ms infinite;
}

@keyframes klaag {
    from,
    to {
        transform: scale(1);
    }
    50% {
        transform: scale(1.5);
    }
}
</style>
