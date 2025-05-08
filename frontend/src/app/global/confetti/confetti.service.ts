import {computed, Injectable, OnDestroy, signal} from '@angular/core';
import {getPreset, PRESET_DEFAULT} from './confetti.util';
import JSConfetti from 'js-confetti';

export const jsConfetti: JSConfetti = new JSConfetti();

@Injectable({
  providedIn: 'root'
})
export class ConfettiService implements OnDestroy {
  public readonly confettiShowing = computed(() => this.confettiCounter() > 0);

  private readonly confettiCounter = signal(0);

  ngOnDestroy() {
    jsConfetti.clearCanvas();
  }

  /**
   * Dispense confetti for entertainment purposes.
   * @param preset
   */
  public dispenseConfetti(preset: string = PRESET_DEFAULT) {
    this.confettiCounter.update(x => x + 1);

    jsConfetti.addConfetti(getPreset(preset))
      .then(() => {
        this.confettiCounter.update(x => Math.max(0, x - 1));
      })
  }
}
