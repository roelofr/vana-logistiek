export type District = {
    id: number
    name: string
    color: string
}

export type Vendor = {
    id: number
    name: string
    number: string
    district: District
}
