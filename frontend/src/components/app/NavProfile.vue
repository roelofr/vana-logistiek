<script setup lang="ts">

import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel, DropdownMenuSeparator, DropdownMenuTrigger
} from '@/components/ui/dropdown-menu'
import { CircleUser } from 'lucide-vue-next'
import { Button } from '@/components/ui/button'
</script>

<template>
  <DropdownMenu>
    <DropdownMenuTrigger as-child>
      <Button variant="outline">
        <CircleUser class="h-5 w-5" />
        <span class="sr-only">Toon of verberg gebruikersmenu</span>
      </Button>
    </DropdownMenuTrigger>

    <DropdownMenuContent align="end">
      <DropdownMenuLabel>Tessa</DropdownMenuLabel>
      <DropdownMenuSeparator />
      <DropdownMenuItem>Instellingen</DropdownMenuItem>
      <DropdownMenuSeparator />
      <DropdownMenuItem>Uitloggen</DropdownMenuItem>
    </DropdownMenuContent>
  </DropdownMenu>
</template>

<style scoped>

</style>
