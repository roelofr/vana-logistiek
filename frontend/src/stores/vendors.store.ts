import { defineStore } from 'pinia'

import { apiFetch } from '@/lib'

import { type District, type Vendor } from '@/domain'

const CACHE_DURATION_IN_MS = 300_000;

type VendorStoreState = {
    loading: boolean
    lastFetch: null | number,
    error: string | null
    vendors: Vendor[],
}

let vendorId = 1;
const vendor = (district: District, number: string, name: string): Vendor => ({
    id: vendorId++,
    name,
    number,
    district
})

const vendors = [
    vendor(districtsByName['rood'], '1001', ),
]

export const useVendorStore = defineStore('vendor', {
    state: (): VendorStoreState => {
        return {
            loading: true,
            lastFetch: null,
            error: null,
            vendors: []
        }
    },
    actions: {
        /**
         * Loads all vendors
         */
        async getAll(): Promise<void> {
            this.loading = true

            try {
                const { response, body } = await apiFetch('GET', '/api/vendors')
                if (response.status !== 200) {
                    throw new Error(body?.error ?? response.statusText)
                }

                this.error = null
                this.vendors = body!.data as Vendor[]
            } catch (e) {
                this.error = String(e)
            } finally {
                this.loading = false
                this.lastFetch = Date.now()
            }
        },

        /**
         * Returns vendors, if no error. Downloads a fresh copy if the old one is stale.
         */
        async getAllIfMIssing(): Promise<Vendor[]> {
            if (this.lastFetch < (Date.now() - CACHE_DURATION_IN_MS))
                await this.getAll()

            return this.error != null ? null : this.vendors
        }
    }
})
