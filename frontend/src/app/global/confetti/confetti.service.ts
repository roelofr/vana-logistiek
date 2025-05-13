import {afterNextRender, computed, Injectable, OnDestroy, signal} from '@angular/core';
import {getPreset, PRESET_DEFAULT} from './confetti.util';
import JSConfetti from 'js-confetti';

export let jsConfetti: JSConfetti;

@Injectable({
  providedIn: 'root'
})
export class ConfettiService implements OnDestroy {
  private readonly confettiCounter = signal(0);
  public readonly confettiShowing = computed(() => this.confettiCounter() > 0);

  constructor() {
    afterNextRender(() => {
      if (!jsConfetti)
        jsConfetti = new JSConfetti()
    })
  }

  ngOnDestroy() {
    if (jsConfetti)
      jsConfetti.clearCanvas();
  }

  /**
   * Dispense confetti for entertainment purposes.
   * @param preset
   */
  public dispenseConfetti(preset: string = PRESET_DEFAULT) {
    if (!jsConfetti)
      return;

    this.confettiCounter.update(x => x + 1);

    jsConfetti.addConfetti(getPreset(preset))
      .then(() => {
        this.confettiCounter.update(x => Math.max(0, x - 1));
      })
  }
}
