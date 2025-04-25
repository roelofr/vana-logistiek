import { beforeEach, describe, test } from 'vitest'
import { setActivePinia } from 'pinia'
import { createTestingPinia } from '@pinia/testing'

describe('api/fetch', () => {
    beforeEach(() => {
        setActivePinia(createTestingPinia())
    })

    test.todo('apiFetch/headers unauthenticated')
    test.todo('apiFetch/headers authenticated')

    test.todo('apiFetch/unauthenticated without session')
    test.todo('apiFetch/unauthenticated with session')

    test.todo('determineBearer/non-origin')
    test.todo('determineBearer/origin non-user')
    test.todo('determineBearer/origin user')

    test.todo('handleResponse/ok json')
    test.todo('handleResponse/ok non-json')
    test.todo('handleResponse/err invalid json')
    test.todo('handleResponse/err non-contract json')
})
