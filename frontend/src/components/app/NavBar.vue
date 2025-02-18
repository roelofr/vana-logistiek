<script setup lang="ts">
import AppIcon from '@/components/app/AppIcon.vue'
import { Button } from '@/components/ui/button'
import { Sheet, SheetContent, SheetTrigger } from '@/components/ui/sheet'
import { Menu, PartyPopper } from 'lucide-vue-next'
import { ref } from 'vue'
import ColorMode from '@/components/app/ColorMode.vue'
import NavProfile from '@/components/app/NavProfile.vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const menuItems = ref([
    {
        label: 'Dashboard',
        href: '/',
    },
    {
        label: 'Tickets',
        href: '/tickets',
    },
    {
        label: 'Ik wil klagen',
        href: '/klaag',
    },
])

const open = ref(false)
const linkClick = () => {
    open.value = false
}
const confetti = () => document.dispatchEvent(new CustomEvent('confetti'))
</script>

<template>
    <header class="sticky top-0 border-b bg-background">
        <div class="flex items-center gap-4 px-4 md:px-6 h-16 mx-auto max-w-7xl">
            <Sheet v-model:open="open">
                <SheetTrigger as-child>
                    <Button variant="outline" size="icon" class="shrink-0 md:hidden">
                        <Menu class="h-5 w-5" />
                        <span class="sr-only">Toggle navigation menu</span>
                    </Button>
                </SheetTrigger>
                <SheetContent side="left">
                    <nav class="flex flex-col items-stretch gap-6 text-lg font-medium h-full">
                        <RouterLink
                            to="/"
                            @click="linkClick"
                            class="flex items-center gap-2 text-lg font-semibold"
                        >
                            <AppIcon class="h-6 w-6" />
                            <span class="font-bold">Logistiek</span>
                        </RouterLink>
                        <RouterLink
                            v-for="{ href, label } in menuItems"
                            :key="href"
                            :to="href"
                            @click="linkClick"
                            class="text-muted-foreground hover:text-foreground"
                            activeClass="text-foreground"
                        >
                            {{ label }}
                        </RouterLink>
                        <div class="flex-grow w-full"></div>
                        <Button variant="outline" class="text-left" @click="confetti"
                            >Meer confetti
                        </Button>
                    </nav>
                </SheetContent>
            </Sheet>

            <nav
                class="hidden flex-col gap-6 text-lg font-medium md:flex md:flex-row md:items-center md:gap-5 md:text-sm lg:gap-6"
            >
                <RouterLink
                    to="/"
                    class="flex items-center gap-2 text-lg font-semibold md:text-base"
                >
                    <AppIcon class="h-6 w-6" />
                    <span class="font-bold">Logistiek</span>
                </RouterLink>
                <RouterLink
                    v-for="{ href, label } in menuItems"
                    :key="href"
                    :to="href"
                    class="text-muted-foreground transition-colors hover:text-foreground"
                    activeClass="text-foreground"
                >
                    {{ label }}
                </RouterLink>
            </nav>

            <div class="flex-1 min-h-[1px]"></div>

            <div class="flex items-center gap-4 md:ml-auto md:gap-2 lg:gap-4">
                <Button variant="default" @click="router.push({ name: 'ticket.new' })">
                    Nieuw ticket
                </Button>
                <Button class="hidden md:inline-block" variant="outline" @click="confetti">
                    <PartyPopper class="h-[1.2rem] w-[1.2rem]" />
                    <span class="sr-only">Meer confetti</span>
                </Button>
                <ColorMode />
                <NavProfile />
            </div>
        </div>
    </header>
</template>
