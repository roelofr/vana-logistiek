import { defineStore } from 'pinia'

import { apiFetch } from '@/lib'

const baseUrl = `${import.meta.env.VITE_API_URL}/users`

const LOCAL_STORAGE_KEY = 'user_logistiek'

declare interface SessionUser {
    id: number
    name: string
    email: string
    token: string
    expiration: Date
}

declare interface LoginResponse {
    ok: boolean
    error?: string
}

declare interface AuthStore {
    user: SessionUser | null
    token: string | null
    returnUrl: string | null
}

export const useAuthStore = defineStore<'auth', AuthStore>('auth', {
    state: () => {
        const data: AuthStore = {
            user: null,
            token: null,
            returnUrl: null,
        }

        const storedData = JSON.parse(localStorage.getItem(LOCAL_STORAGE_KEY) ?? 'null')
        if (storedData == null) return data

        const storedUser = storedData as SessionUser
        if (storedUser.expiration > new Date()) return data

        data.user = storedUser
        data.token = storedUser.token
        return data
    },
    getters: {
        name() {
            return this.user?.name
        },
        email() {
            return this.user?.email
        },
    },
    actions: {
        async login(username: string, password: string): Promise<LoginResponse> {
            const { response, body } = await apiFetch('POST', `${baseUrl}/authenticate`, {
                username,
                password,
            })

            if (response.status !== 200) {
                return {
                    ok: false,
                    error: typeof body === 'object' ? body?.error : response.statusText,
                }
            }

            // update pinia state
            const user = body as SessionUser
            this.user = user

            // store user details and jwt in local storage to keep user logged in between page refreshes
            localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(user))
            return { ok: true }
        },
        async logout() {
            this.user = null
            this.token = null
            localStorage.removeItem(LOCAL_STORAGE_KEY)
        },
    },
})
