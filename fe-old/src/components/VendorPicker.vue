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
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
import { cn } from '@/lib/utils.ts'
import { Check, ChevronsUpDown, LoaderCircle, OctagonX } from 'lucide-vue-next'
import { computed, ref } from 'vue'
import VendorPickerLine from '@/components/VendorPickerLine.vue'
import { findAllVendors } from '@/api'
import { useQuery } from '@pinia/colada'
import { type Vendor } from '@/domain'

const {
    data: vendors,
    asyncStatus,
    status,
} = useQuery({
    key: ['vendor-list'],
    query: findAllVendors,
})

const value = defineModel<Vendor | null>({ required: true })
const open = ref(false)

const vendorsListNonEmpty = computed(() => status.value === 'success' && vendors.value!.length > 0)
const currentVendor = computed(() => {
    const vendorsValue = vendors.value as Vendor[]

    if (!vendorsValue?.length) return null

    return vendorsValue.find((v: Vendor) => v.id === value.value?.id) ?? null
})

const pickVendor = (vendor: Vendor) => {
    value.value = vendor
    open.value = false
}

const vendorValue = (vendor: Vendor) => `${vendor.number} ${vendor.name}`
</script>

<template>
    <template v-if="asyncStatus === 'loading'">
        <Button
            variant="outline"
            role="combobox"
            class="flex w-full justify-center text-start"
            disabled="disabled"
        >
            <div class="flex-grow">
                <VendorPickerLine v-if="currentVendor" :vendor="currentVendor" />
                <span class="text-muted-foreground flex items-center space-x-4" v-else>
                    <LoaderCircle class="h-5 animate-spin" />
                    <span>Laden...</span>
                </span>
            </div>
            <ChevronsUpDown class="ml-2 h-4 w-4 shrink-0 opacity-50" />
        </Button>
    </template>

    <template v-else-if="!vendorsListNonEmpty">
        <Button
            variant="outline"
            role="combobox"
            class="flex w-full justify-center text-start"
            disabled="disabled"
        >
            <div class="flex-grow">
                <VendorPickerLine v-if="currentVendor" :vendor="currentVendor" />
                <span class="text-muted-foreground flex items-center space-x-4" v-else>
                    <OctagonX class="h-5" />
                    <span>Geen standhouders beschikbaar</span>
                </span>
            </div>
            <ChevronsUpDown class="ml-2 h-4 w-4 shrink-0 opacity-50" />
        </Button>
    </template>

    <Popover v-else v-model:open="open">
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
                            @select="pickVendor(vendor)"
                        >
                            <VendorPickerLine :vendor="vendor" />
                            <Check
                                :class="
                                    cn(
                                        'ml-auto h-4 w-4',
                                        value?.id === vendor.id ? 'opacity-100' : 'opacity-0',
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
