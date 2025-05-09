import {ApplicationConfig, provideExperimentalZonelessChangeDetection,} from '@angular/core';
import {provideRouter, TitleStrategy} from '@angular/router';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {routes} from './app.routes';
import {TemplatePageTitleStrategy} from './global/template-page-title-strategy.service';
import {provideAppIcons} from './app.icons';
import {provideHttpClient, withFetch} from '@angular/common/http';
import {provideClientHydration, withEventReplay,} from '@angular/platform-browser';

export const appConfig: ApplicationConfig = {
  providers: [
    provideExperimentalZonelessChangeDetection(),
    provideClientHydration(withEventReplay()),
    provideRouter(routes),
    provideHttpClient(withFetch()),
    provideAnimationsAsync(),
    {provide: TitleStrategy, useClass: TemplatePageTitleStrategy},
    provideAppIcons(),
  ],
};
