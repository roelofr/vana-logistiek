interface IAddConfettiConfig {
    confettiRadius?: number
    confettiNumber?: number
    confettiColors?: string[]
    emojis?: string[]
    emojiSize?: number
}

export const presets = new Map<string, Partial<IAddConfettiConfig>>([
    [
        'sad',
        {
            emojis: ['😭', '💧', '💦', '😞'],
            emojiSize: 100,
            confettiNumber: 30,
        },
    ],
    ['default', {}],
])
