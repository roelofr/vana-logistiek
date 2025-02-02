import { type ClassValue, clsx } from 'clsx'
import { twMerge } from 'tailwind-merge'
import { type Prop } from 'vue'

export function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs))
}

export const RequiredString: Prop<string> = {
    type: String,
    required: true,
}

export const DefaultBoolean = (value: boolean): Prop<boolean> => ({
    type: Boolean,
    required: false,
    default: value,
})
