import { type District, type Vendor, districts } from '@/domain'

let vendorCounter = 0
const makeVendor = (name: string, location: string, district: District): Vendor => ({
  id: ++vendorCounter,
  name,
  number: location,
  district
})

const district = (name: string): District => {
  const res = districts.find(d => d.name.toLowerCase() === name.toLowerCase())
  if (res)
    return res

  throw new Error(`Unknown district name \`${name}\``)
}

const vendors = Object.freeze([
  makeVendor('401', 'Echt leer', district('Blauw')),
  makeVendor('701', 'Eleo Flora', district('Geel')),
  makeVendor('702', 'Kolderieke Maliën', district('Geel')),
  makeVendor('703', 'Mooie Mannen Droomfontein', district('Geel')),
  makeVendor('1009', 'Celtic World', district('Rood'))
])

export default vendors
