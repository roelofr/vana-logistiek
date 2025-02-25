import type { District } from '@/domain'

export interface Vendor {
    id: number
    number: string
    name: string
    district: District
}
