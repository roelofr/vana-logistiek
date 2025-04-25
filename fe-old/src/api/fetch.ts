import { useAuthStore } from '@/stores'

declare interface WrappedResponse {
    response: Response
    body: null | {
        error?: string
        data?: unknown
    }
}

declare type RequestMethod = 'HEAD' | 'GET' | 'POST' | 'PUT' | 'DELETE'

export async function apiFetch(
    method: RequestMethod,
    url: string | URL,
    data: unknown = null,
    options: RequestInit = {},
): Promise<WrappedResponse> {
    const fullUrl = new URL(url, document.location.origin)

    const params = {
        ...options,
        method,
        headers: new Headers(options.headers ?? []),
    }

    if (data) {
        params.body = JSON.stringify(data)
        params.headers.set('Content-Type', 'application/json')
    }

    const authBearer = determineBearer(fullUrl)
    if (authBearer) {
        params.mode = 'same-origin'
        params.credentials = 'same-origin'
        params.headers.set('Authorization', authBearer)
    }

    return fetch(fullUrl, params).then(handleResponse)
}

/**
 * Determine a bearer for the Authorization URL
 * @param url
 */
function determineBearer(url: URL): string | null {
    // Check if same origin
    if (url.origin !== document.location.origin) return null

    // Check if logged in
    const { user } = useAuthStore()
    const isLoggedIn = !!user?.token
    if (!isLoggedIn) return null

    // Slets go
    return `Bearer ${user.token}`
}

/**
 * Logout in case the token has expired.
 * @param response Handled response.
 */
async function handleResponse(response: Response): Promise<WrappedResponse> {
    const { user, logout } = useAuthStore()

    if (user && response.status === 401) {
        console.warn('User session no longer valid.')
        logout()
    }

    const responseUrl = response.url
    const responseContentType = response.headers.get('Content-Type')

    if (!String(responseContentType).trim().startsWith('application/json')) {
        console.warn(
            'Recieved non-JSON response from %o, mime is %o',
            responseUrl,
            responseContentType,
        )
        return { response, body: null }
    }

    const body = await response.json()

    if (typeof body !== 'object') {
        console.warn('Receved non-object JSON response from %o', responseUrl)
    }

    return { response, body }
}
