<script setup lang="ts">
import { NavItems } from '@/domain'
import { Button } from '@/components/ui/button'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'
import { Input } from '@/components/ui/input'
import { Sheet, SheetContent, SheetTrigger } from '@/components/ui/sheet'
import AppIcon from '@/components/app/AppIcon.vue'
import { CircleUser, Menu, Search } from 'lucide-vue-next'
import { confetti, DefaultBoolean } from '@/lib'
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const { hideNav } = defineProps({
    hideNav: DefaultBoolean(false),
})

const navOpen = ref(false)

const navClick = () => {
    navOpen.value = false
}
</script>

<template>
    <div class="grid min-h-screen w-full md:grid-cols-[220px_1fr] lg:grid-cols-[280px_1fr]">
        <div class="hidden border-r bg-muted/40 md:block" v-if="!hideNav">
            <div class="flex h-full max-h-screen flex-col gap-2">
                <div class="flex h-14 items-center border-b px-4 lg:h-[60px] lg:px-6">
                    <RouterLink to="/" class="flex items-center gap-3 py-2 font-semibold">
                        <AppIcon class="h-6 w-6" />
                        <span class="font-bold">Logistiek</span>
                    </RouterLink>
                    <!-- <Button variant="outline" size="icon" class="ml-auto h-8 w-8">
                      <Bell class="h-4 w-4" />
                      <span class="sr-only">Toggle notifications</span>
                    </Button> -->
                </div>
                <div class="flex-1">
                    <nav class="grid items-start px-2 text-sm font-medium lg:px-4">
                        <RouterLink
                            v-for="link in NavItems"
                            :key="link.id"
                            :to="link.href"
                            class="flex items-center gap-3 rounded-lg px-3 py-2 transition-all hover:text-primary text-muted-foreground"
                            activeClass="bg-muted text-primary"
                        >
                            <component v-if="link.icon" :is="link.icon" class="h-4 w-4" />
                            {{ link.label }}
                        </RouterLink>
                    </nav>
                </div>
                <div class="mt-auto p-4">
                    <Card>
                        <CardHeader class="p-2 pt-0 md:p-4">
                            <CardTitle>Feeling down?</CardTitle>
                            <CardDescription>
                                Nothing a bit more confetti can't fix!
                            </CardDescription>
                        </CardHeader>
                        <CardContent class="p-2 pt-0 md:p-4 md:pt-0">
                            <Button size="sm" class="w-full" @click="confetti()">
                                Meer confetti!
                            </Button>
                        </CardContent>
                    </Card>
                </div>
            </div>
        </div>
        <div class="flex flex-col">
            <header
                class="flex h-14 items-center gap-4 border-b bg-muted/40 px-4 lg:h-[60px] lg:px-6"
            >
                <Sheet v-if="!hideNav" v-model:open="navOpen">
                    <SheetTrigger as-child>
                        <Button variant="outline" size="icon" class="shrink-0 md:hidden">
                            <Menu class="h-5 w-5" />
                            <span class="sr-only">Toon/verberg navigatie</span>
                        </Button>
                    </SheetTrigger>
                    <SheetContent side="left" class="flex flex-col">
                        <nav class="grid gap-2 text-lg font-medium">
                            <RouterLink
                                to="/"
                                class="flex items-center gap-4 py-2 text-lg font-semibold"
                                @click="navClick"
                            >
                                <AppIcon class="h-6 w-6" />
                                <span class="font-bold">Logistiek</span>
                            </RouterLink>
                            <RouterLink
                                class="mx-[-0.65rem] flex items-center gap-4 rounded-xl px-3 py-2 hover:text-foreground text-muted-foreground"
                                active-class="bg-muted text-foreground"
                                v-for="link in NavItems"
                                :key="link.id"
                                :to="link.href"
                                @click="navClick"
                            >
                                <component v-if="link.icon" :is="link.icon" class="h-5 w-5" />
                                {{ link.label }}
                            </RouterLink>
                        </nav>
                        <div class="mt-auto">
                            <Card>
                                <CardHeader>
                                    <CardTitle>Feeling down?</CardTitle>
                                    <CardDescription>
                                        Nothing a bit more confetti can't fix!
                                    </CardDescription>
                                </CardHeader>
                                <CardContent>
                                    <Button size="sm" class="w-full" @click="confetti()">
                                        Meer confetti!
                                    </Button>
                                </CardContent>
                            </Card>
                        </div>
                    </SheetContent>
                </Sheet>
                <div class="w-full flex-1">
                    <form>
                        <div class="relative">
                            <Search
                                class="absolute left-2.5 top-2.5 h-4 w-4 text-muted-foreground"
                            />
                            <Input
                                type="search"
                                placeholder="Search products..."
                                class="w-full appearance-none bg-background pl-8 shadow-none md:w-2/3 lg:w-1/3"
                            />
                        </div>
                    </form>
                </div>
                <Button variant="default" @click="router.push({ name: 'ticket.new' })">
                    Nieuw ticket
                </Button>
                <DropdownMenu>
                    <DropdownMenuTrigger as-child>
                        <Button variant="secondary" size="icon" class="rounded-full">
                            <CircleUser class="h-5 w-5" />
                            <span class="sr-only">Toon gebruikersmenu</span>
                        </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent align="end">
                        <DropdownMenuLabel>My Account</DropdownMenuLabel>
                        <DropdownMenuSeparator />
                        <DropdownMenuItem>Settings</DropdownMenuItem>
                        <DropdownMenuItem>Support</DropdownMenuItem>
                        <DropdownMenuSeparator />
                        <DropdownMenuItem>Logout</DropdownMenuItem>
                    </DropdownMenuContent>
                </DropdownMenu>
            </header>
            <main class="flex flex-1 flex-col gap-4 p-4 lg:gap-6 lg:p-6">
                <slot />
            </main>
        </div>
    </div>
</template>
