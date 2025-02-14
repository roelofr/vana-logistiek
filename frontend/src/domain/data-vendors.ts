import { type District, type Vendor } from '@/domain/types.ts'
import districts from '@/domain/data-districts.ts'

let vendorCounter = 0
const makeVendor = (location: string, name: string, district: District): Vendor => ({
    id: ++vendorCounter,
    name,
    number: location,
    district,
})

const district = (name: string): District => {
    const res = districts.value.find((d) => d.name.toLowerCase() === name.toLowerCase())
    if (res) return res

    throw new Error(`Unknown district name \`${name}\``)
}

const vendors = Object.freeze(
    [
        makeVendor('1009', 'Celtic World', district('Rood')),
        makeVendor('401', 'Echt leer', district('Blauw')),
        makeVendor('701', 'Eleo Flora', district('Geel')),
        makeVendor('302', 'Games & Stuff', district('Groen')),
        makeVendor('1308', 'Hollow Moon Art', district('Oranje')),
        makeVendor('702', 'Kolderieke Maliën', district('Geel')),
        makeVendor('703', 'Mooie Mannen Droomfontein', district('Geel')),
        makeVendor('1201A', 'Moira', district('Zandbruin')),
        makeVendor('1101A', 'Pagan Ways', district('Zandbruin')),
    ].sort((a, b) => Number.parseInt(a.number) - Number.parseInt(b.number)),
)

export default vendors
