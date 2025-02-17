// stores/counter.spec.ts
import { createPinia, setActivePinia } from 'pinia'
import { beforeEach, describe, expect, test } from 'vitest'
import { LOCAL_STORAGE_KEY, type SessionUser, useAuthStore } from '@/stores/auth.store.ts'

const makeUser = (): SessionUser => ({
    id: 1,
    name: 'test',
    email: 'test@example.com',
    token: 'tokentest',
    expiration: new Date(Date.now() + 600_000)
})

describe('stores/auth', () => {
    beforeEach(() => {
        setActivePinia(createPinia())
        localStorage.removeItem(LOCAL_STORAGE_KEY)
    })

    describe('init', () => {
        test('default', () => {
            const auth = useAuthStore()

            expect(auth.user).toBe(null)
            expect(auth.token).toBe(null)
        })

        test('restore session', () => {
            localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(makeUser()))

            const auth = useAuthStore()

            expect(auth.user).not.toBe(null)
            expect(auth.token).not.toBe(null)

            expect(auth.name).toBe('test')
            expect(auth.email).toBe('test@example.com')
        })

        test('restore expired', () => {
            const storageUser = makeUser()
            storageUser.expiration = new Date(Date.now() - 600_000)
            localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(storageUser))

            const auth = useAuthStore()

            expect(auth.user).toBe(null)
            expect(auth.token).toBe(null)
        })
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
                expiration: new Date('2050-01-01T00:00:00+01:00')
            },
            token: 'token'
        })

        expect(auth.token).not.toBe(null)
        expect(auth.user).not.toBe(null)

        auth.logout()

        expect(auth.token).toBe(null)
        expect(auth.user).toBe(null)
    })
})
