import { apiFetch } from '@/api/fetch.ts'
import type { Ticket } from '@/domain'

/**
 * Finds all tickets, as list
 */
export const findAllTickets = async (): Promise<Ticket[]> => {
    const { response, body } = await apiFetch('GET', '/api/tickets')

    if (response.status !== 200) return []

    if (!body) throw new Error('Response is missing!')

    return body as Ticket[]
}

/**
 * Finds a single ticket, returns null if not found.
 * @param ticketId
 */
export const findTicket = async (ticketId: number): Promise<null | Ticket> => {
    const { response, body } = await apiFetch('GET', `/api/tickets/${ticketId}`)

    if (response.status === 404) return null

    if (response.status !== 200) throw new Error('Ticket not found!')

    return body as Ticket
}

/**
 * Creates a ticket
 * @param request
 */
export const saveTicket = async (request: TicketCreateRequest): Promise<Ticket> => {
    if (!request.description) throw new Error('Ticket description is missing!')
    if (!request.vendorId) throw new Error('Ticket vendor is missing!')

    const { response, body } = await apiFetch('POST', '/api/tickets', request)
    if (response.status !== 200) throw new Error('Status is missing!')

    return body as Ticket
}

interface TicketCreateRequest {
    vendorId: number
    description: string
}
