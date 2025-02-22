import type { Prop } from 'vue'

export const RequiredString: Prop<string> = {
    type: String,
    required: true,
}

export const DefaultBoolean = (value: boolean): Prop<boolean> => ({
    type: Boolean,
    required: false,
    default: value,
})
