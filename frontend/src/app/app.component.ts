import {Component, signal} from '@angular/core'
import {EventType, Router, RouterOutlet} from '@angular/router'

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css',
})
export class AppComponent {
  hasCustomUi = signal(false);

  constructor(router: Router) {
    router.events.subscribe((event) => {
      if (event.type != EventType.ResolveStart) {
        console.log('Ingore event {}', event.type)
        return;
      }

      console.log('Router says %o', router.getCurrentNavigation())

      const routeData = event.state.root.routeConfig?.data;

      this.hasCustomUi.set(routeData && routeData['noUi']);
    })
  }
}
