import {ApplicationConfig, provideExperimentalZonelessChangeDetection} from '@angular/core';
import {provideHttpClient, withFetch} from '@angular/common/http';
import {provideAppIcons} from './app/app.icons';

export const appTestConfig: ApplicationConfig = {
  providers: [
    provideExperimentalZonelessChangeDetection(),
    provideHttpClient(withFetch()),
    provideAppIcons(),
  ]
}
