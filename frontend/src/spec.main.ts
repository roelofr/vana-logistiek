import {ApplicationConfig, provideZonelessChangeDetection,} from '@angular/core';
import {provideHttpClient, withFetch} from '@angular/common/http';
import {provideAppIcons} from './app/app.icons';

export const appTestConfig: ApplicationConfig = {
  providers: [
    provideZonelessChangeDetection(),
    provideHttpClient(withFetch()),
    provideAppIcons(),
  ],
};
