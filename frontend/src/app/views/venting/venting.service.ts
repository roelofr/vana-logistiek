import {Injectable} from '@angular/core';
import {ConfettiService} from '../../global/confetti/confetti.service';

const reacties = [
  'Ja, je mag slaan.',
  'Dat is inderdaad stommiepommie',
  'Nee :(',
  'Nee :)',
  '[Diepe zucht]... Ik leef met je mee.',
  'Wil je een snoepje?',
  'Wil je een koekje?',
  'Standhouders zijn kut.',
  'Had je maar meer moeten eten',
  'Had je maar meer moeten drinken',
  'Standhouder heeft nooit gelijk',
  'Heb je al zonnebrand gebruikt?',
  'Jeetje.',
  'Even tot tien tellen.',
  'Gelukkig heb je de klaag-i-nator nog',
  'Denk maar even na over het antwoord van 966 ÷ √(529)',
  'Je hebt duidelijk nog geen koffie op.',
  'AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHHH',
  'huiliehuilie',
  'boos',
  'Kaalm!',
]


@Injectable({
  providedIn: 'root'
})
export class VentingService {
  constructor(private confettiService: ConfettiService) {
    //
  }

  async submitComplaint(): Promise<string> {
    await new Promise(resolve => setTimeout(resolve, Math.random() * 2000 + 750));

    this.confettiService.dispenseConfetti('sad');

    return (reacties[Math.floor(Math.random() * reacties.length - 1)]);
  }
}
