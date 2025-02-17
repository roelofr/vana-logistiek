// stores/counter.spec.ts
import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, test } from 'vitest'
import { useAuthStore } from '@/stores/auth.store.ts'

describe('Auth Store', () => {
    beforeEach(() => {
        setActivePinia(createPinia())
    })

    test.todo('login')

    test('logout', () => {
        const auth = useAuthStore()

        auth.$patch({
            user: {
                id: 1,
                name: 'test',
                email: 'test',
                token: 'token',
                expiration: new Date('2050-01-01T00:00:00+01:00'),
            },
            token: 'token',
        })

        expect(auth.token).not.toBe(null)
        expect(auth.user).not.toBe(null)

        auth.logout()

        expect(auth.token).toBe(null)
        expect(auth.user).toBe(null)
    })
})
