<script setup lang="ts">
import { Button } from '@/components/ui/button'
import {
    Command,
    CommandEmpty,
    CommandGroup,
    CommandInput,
    CommandItem,
    CommandList,
} from '@/components/ui/command'

import { type Vendor, vendors } from '@/domain'

import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
import { cn } from '@/lib/utils.ts'
import { Check, ChevronsUpDown } from 'lucide-vue-next'
import { computed, type ComputedRef, ref } from 'vue'
import VendorPickerLine from '@/components/VendorPickerLine.vue'
import { useVendorStore } from '@/stores/vendors.store.ts'
import { storeToRefs } from 'pinia'

const vendorStore = useVendorStore()
const { vendors } = storeToRefs(vendorStore)

const value = defineModel<Number>()
const open = ref(false)
const currentVendor: ComputedRef<Vendor | null> = computed(() => vendors.value.find((v: Vendor) => v.id === value.value) ?? null)

// Load the value
vendorStore.getAllIfMIssing();

const vendorValue = (vendor: Vendor) => `${vendor.number} ${vendor.name}`
</script>

<template>
    <Popover v-model:open="open">
        <PopoverTrigger as-child>
            <Button
                variant="outline"
                role="combobox"
                :aria-expanded="open"
                class="flex w-full justify-center text-start"
            >
                <div class="flex-grow">
                    <VendorPickerLine v-if="currentVendor" :vendor="currentVendor" />
                    <span class="text-muted-foreground" v-else>Selecteer een standhouder</span>
                </div>
                <ChevronsUpDown class="ml-2 h-4 w-4 shrink-0 opacity-50" />
            </Button>
        </PopoverTrigger>
        <PopoverContent class="p-0 popover-content-anchor-width">
            <Command>
                <CommandInput class="h-9" placeholder="Zoek standhouder" />
                <CommandEmpty> Geen overeenkomende standhouder gevonden.</CommandEmpty>
                <CommandList>
                    <CommandGroup>
                        <CommandItem
                            v-for="vendor in vendors"
                            :key="vendor.id"
                            :value="vendorValue(vendor)"
                            @select="() => selectVendor(vendor.id)"
                        >
                            <VendorPickerLine :vendor="vendor" />
                            <Check
                                :class="
                                    cn(
                                        'ml-auto h-4 w-4',
                                        value === vendor ? 'opacity-100' : 'opacity-0',
                                    )
                                "
                            />
                        </CommandItem>
                    </CommandGroup>
                </CommandList>
            </Command>
        </PopoverContent>
    </Popover>
</template>

<style>
.popover-content-anchor-width {
    width: var(--radix-popper-anchor-width);
    max-height: var(--radix-popper-anchor-height);
}

@screen md {
    .popover-content-anchor-width {
        max-height: var(--radix-popper-available-height);
    }
}
</style>
