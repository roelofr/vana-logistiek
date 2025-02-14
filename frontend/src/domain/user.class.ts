import type { District } from '@/domain'

export interface User {
    name: string
    email: string
    roles: string[]
    district: District
}
