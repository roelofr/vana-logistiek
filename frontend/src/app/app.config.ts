import {ApplicationConfig, importProvidersFrom, provideZoneChangeDetection} from '@angular/core'
import {provideRouter, TitleStrategy} from '@angular/router'
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async'
import {routes} from './app.routes'
import {TemplatePageTitleStrategy} from './global/template-page-title-strategy.service';
import {ClarityIcons} from '@cds/core/icon';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({eventCoalescing: true}),
    provideRouter(routes),
    provideAnimationsAsync(),
    importProvidersFrom(ClarityIcons),
    {provide: TitleStrategy, useClass: TemplatePageTitleStrategy},
  ],
}
