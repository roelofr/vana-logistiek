import {ApplicationConfig, provideZonelessChangeDetection,} from '@angular/core';
import {provideRouter, TitleStrategy} from '@angular/router';
import {provideAnimationsAsync} from '@angular/platform-browser/animations/async';
import {routes} from './app.routes';
import {TemplatePageTitleStrategy} from './global/template-page-title-strategy.service';
import {provideAppIcons} from './app.icons';
import {provideHttpClient, withFetch, withInterceptors} from '@angular/common/http';
import {jwtAuthInterceptor} from './services/http/jwt-auth-interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZonelessChangeDetection(),
    provideRouter(routes),
    provideHttpClient(withFetch(), withInterceptors([jwtAuthInterceptor])),
    provideAnimationsAsync(),
    {provide: TitleStrategy, useClass: TemplatePageTitleStrategy},
    provideAppIcons(),
  ],
};
