export interface ConfettiConfig {
  confettiRadius?: number;
  confettiNumber?: number;
  confettiColors?: string[];
  emojis?: string[];
  emojiSize?: number;
}

export const PRESET_DEFAULT = 'default';

const emojis = (...args: (string | string[])[]) => {
  return args.flatMap((arg) => (arg instanceof Array ? arg : [arg]));
};

const repeat = (emoji: string, times: number): string[] =>
  Array(times).fill(emoji);

const presets: Record<string, Partial<ConfettiConfig>> = {
  PRESET_DEFAULT: {},
  strong: {
    confettiNumber: 150,
  },
  sad: {
    emojis: emojis('😭', '💧', '💦', '😞'),
    emojiSize: 100,
    confettiNumber: 30,
  },
  penis: {
    emojis: emojis(
      repeat('🍆', 10),
      repeat('💦', 4),
      repeat('😲', 4),
      '🤤',
      '🥴',
    ),
    emojiSize: 80,
    confettiNumber: 50,
  },
  checkmarks: {
    emojis: emojis(
      repeat('✔️', 15),
      repeat('✅', 5),
      repeat('🥳', 4),
      repeat('🎉', 4),
      repeat('✨', 4),
      '🧙',
    ),
    confettiNumber: 30,
    confettiRadius: 40,
    confettiColors: [
      '#388E3C',
      '#689F38',
      '#A5D6A7',
      '#C5E1A5',
      '#1B5E20',
      '#33691E',
    ],
  },
};

export type presetName = keyof typeof presets;

export const getPreset = (name: presetName): ConfettiConfig => {
  if (Object.hasOwn(presets, name)) {
    return presets[name];
  }

  return presets[PRESET_DEFAULT];
};
