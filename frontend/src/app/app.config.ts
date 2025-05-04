import {ApplicationConfig, provideZoneChangeDetection} from '@angular/core'
import {provideRouter, TitleStrategy} from '@angular/router'
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async'
import {routes} from './app.routes'
import {TemplatePageTitleStrategy} from './global/template-page-title-strategy.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({eventCoalescing: true}),
    provideRouter(routes),
    provideAnimationsAsync(),
    {provide: TitleStrategy, useClass: TemplatePageTitleStrategy},
  ],
}
