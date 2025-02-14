import { type District } from '@/domain'
import { ref } from 'vue'

const districts = ref<District[]>([])

fetch('api/districts')
    .then((res) => res.json())
    .then((json) => {
        if (!(json instanceof Array) || json.length == 0) return

        const out: District[] = []
        for (const row of json as District[]) {
            out.push(row)
        }

        districts.value = out
    })

export default districts
