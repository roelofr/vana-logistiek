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
