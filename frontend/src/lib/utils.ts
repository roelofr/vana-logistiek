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

/**
 * Clean up a string to be URL-safe.
 * @param value
 */
export const slug = (value: unknown): string => {
    return String(value)
        .toLowerCase()
        .replace(/[^0-9a-z]+/g, '-')
        .replace(/^-|-$/, '')
}

/**
 * Fire confetti, optionally in a given style.
 * @param style
 */
export const confetti = (style: string = 'default') => {
    document.dispatchEvent(
        new CustomEvent('confetti', {
            detail: style,
        }),
    )
}
