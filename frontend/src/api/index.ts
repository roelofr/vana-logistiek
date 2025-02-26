export { apiFetch } from './fetch.ts'
export * from './tickets.ts'
export * from './vendors.ts'

type ApiKeys = Record<string, string>

export const TicketKeys: ApiKeys = {
    Index: 'ticket-list',
    Show: 'ticket-find',
}
