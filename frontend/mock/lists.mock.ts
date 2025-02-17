import { MockHandler } from 'vite-plugin-mock-server'
import { IncomingMessage, ServerResponse } from 'node:http'
import { type District, Vendor } from '../src/domain'

const sendJson = (res: ServerResponse<IncomingMessage>, body: object): void => {
    res.setHeader('Content-Type', 'application/json')
    res.end(JSON.stringify(body, null, 2))
}

let districtCounter = 1
const district = (name: string, colour: string): District => ({
    id: districtCounter++,
    name,
    mobileName: name.substring(0, 2),
    colour
})
const districts = [
    district('Oranje', 'amber'),
    district('Zandbruin', 'stone'),
    district('Rood', 'red'),
    district('Blauw', 'blue'),
    district('Geel', 'yellow'),
    district('Groen', 'lime')
]

const districtByName: Record<string, District> = Object.fromEntries(districts.map(row => ([String(row.name).toLowerCase(), row])))

let vendorCounter = 1
const vendor = (number: string, name: string, district: District): Vendor => ({
    id: vendorCounter++,
    number: number,
    name: name,
    district: district,
})

const vendors = [
    vendor('100', 'Magic World', districtByName.rood),
    vendor('300a', 'Drop Inc.', districtByName.zandbruin)
];

const mocks: MockHandler[] = [
    {
        pattern: '/api/vendor',
        handle: (req, res) => {
            sendJson(res, [
                {
                    data: vendors,
                }
            ])
        }
    },
    {
        pattern: '/api/test1/*',
        handle: (req, res) => {
            res.end('Hello world!' + req.url)
        }
    },
    {
        pattern: '/api/test1/users/{userId}',
        handle: (req, res) => {
            const data = {
                url: req.url,
                params: req.params,
                query: req.query,
                body: req.body
            }
            res.setHeader('Content-Type', 'application/json')
            res.end(JSON.stringify(data))
        }
    },
    {
        pattern: '/api/test1/body/json',
        method: 'POST',
        handle: (req, res) => {
            res.setHeader('Content-Type', 'application/json')

            //req is incomingMessage which extends stream.Readable
            // --> https://nodejs.org/api/stream.html#readablereadsize
            // res.end need to be within the function
            // there is a size limit for the bodyString to get parsed
            req.on('data', (bodyString: string) => {
                const body: object = JSON.parse(bodyString)
                res.end(JSON.stringify(body))
            })
        }
    }
]

export default mocks
