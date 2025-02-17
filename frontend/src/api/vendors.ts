import { apiFetch } from '@/api/fetch.ts'
import type { Vendor } from '@/domain'

/**
 * Finds all vendors, as list
 */
export const findAllVendors = async (): Promise<Vendor[]> => {
    const { response, body } = await apiFetch('GET', '/api/vendor')

    if (response.status !== 200) return []

    if (!body) throw new Error('Response is missing!')

    return body as Vendor[]
}

/**
 * Finds a single ticket, returns null if not found.
 * @param vendorId
 */
export const findVendor = async (vendorId: string): Promise<null | Vendor> => {
    const { response, body } = await apiFetch('GET', `/api/vendor/${vendorId}`)

    if (response.status === 404) return null

    if (response.status !== 200) throw new Error('Vendor not found!')

    return body as Vendor
}

/**
 * Creates a ticket
 * @param ticket
 */
export const saveVendor = async (vendor: Vendor): Promise<Vendor> => {
    if (!vendor.name) throw new Error('Vendor name is missing!')

    if (!vendor.number) throw new Error('Vendor number is missing!')

    const { response, body } = await apiFetch('POST', '/api/vendor', vendor)

    if (response.status !== 200) throw new Error('Status is incorrect!')

    return body as Vendor
}
