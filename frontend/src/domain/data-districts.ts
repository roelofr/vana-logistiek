import { type District } from '@/domain'

let districtCounter = 0
const makeDistrict = (name: string, color: string): District => ({
  id: ++districtCounter,
  name,
  color
})

const districts = Object.freeze([
  makeDistrict('Oranje', 'amber'),
  makeDistrict('Zandbruin', 'stone'),
  makeDistrict('Rood', 'red'),
  makeDistrict('Blauw', 'blue'),
  makeDistrict('Geel', 'yellow'),
  makeDistrict('Groen', 'lime'),
  makeDistrict('Roze', 'pink')
])

export default districts
