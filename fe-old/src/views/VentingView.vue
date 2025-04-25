<script setup lang="ts">
import { AppContainer } from '@/components/app'
import { ref, watch } from 'vue'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { LoaderCircle } from 'lucide-vue-next'
import {
    Dialog,
    DialogContent,
    DialogFooter,
    DialogHeader,
    DialogTitle,
    DialogTrigger,
} from '@/components/ui/dialog'

let loadingTimeout: number | null = null

const open = ref(false)
const response = ref<string | null>(null)
const loading = ref(false)

watch(open, (newValue) => {
    if (newValue !== false) return

    if (loadingTimeout) clearTimeout(loadingTimeout)
    loadingTimeout = null

    response.value = null
    loading.value = false
})

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
    'Even tot tien tellen.',
    'Gelukkig heb je de klaag-i-nator nog',
    'Denk maar even na over het antwoord van 966 ÷ √(529)',
    'Je hebt duidelijk nog geen koffie op.',
    'AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH',
    'huiliehuilie',
]

const startReageer = () => {
    loading.value = true

    const timeout = 700 + Math.floor(Math.random() * 2_000)
    loadingTimeout = setTimeout(reageer as TimerHandler, timeout)
}

const reageer = () => {
    loadingTimeout = null

    response.value = reacties[Math.floor(Math.random() * reacties.length)]
    loading.value = false

    document.dispatchEvent(new CustomEvent('confetti', { detail: 'sad' }))
}

const closeReageer = () => {
    open.value = false
}
</script>

<template>
    <AppContainer content size="narrow">
        <Dialog v-model:open="open">
            <Card>
                <CardHeader class="mb-8">
                    <CardTitle>Klaag-i-nator</CardTitle>
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
                </DialogHeader>

                <template v-if="response">
                    <div class="flex flex-col items-center px-10 py-20 text-center">
                        <p class="text-md font-medium">Tessa zegt...</p>
                        <p class="text-lg text-muted-foreground max-w-[80%] text-wrap">
                            {{ response }}
                        </p>
                    </div>

                    <DialogFooter>
                        <Button @click.prevent="closeReageer">Bedankt</Button>
                    </DialogFooter>
                </template>

                <template v-else-if="loading">
                    <div class="flex flex-col items-center">
                        <div class="flex items-center space-x-4 px-10 py-20">
                            <LoaderCircle class="animate-spin h-6" />
                            <span class="text-lg">Frustratie doorgeven...</span>
                        </div>
                    </div>
                </template>

                <template v-else>
                    <div class="flex flex-col items-center space-x-2 text-center">
                        <p
                            class="px-10 py-20 text-4xl font-extrabold tracking-tight lg:text-5xl klaagtekst"
                        >
                            Nu klagen
                        </p>

                        <p class="text-sm text-muted-foreground">
                            (voel je vrij om tegen je gegevensdrager te schreeuwen)
                        </p>
                    </div>

                    <DialogFooter>
                        <Button @click="startReageer()">Reageren, a.u.b.</Button>
                    </DialogFooter>
                </template>
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
