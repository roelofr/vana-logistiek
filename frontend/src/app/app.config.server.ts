import {provideServerRendering, withRoutes} from '@angular/ssr';
import {ApplicationConfig, mergeApplicationConfig} from '@angular/core';
import {appConfig} from './app.config';
import {serverRoutes} from './app.routes.server';
import {provideServerServices} from './app.services.server';

const serverConfig: ApplicationConfig = {
  providers: [
    provideServerRendering(withRoutes(serverRoutes)),
    provideServerServices(),
  ],
};

export const config = mergeApplicationConfig(appConfig, serverConfig);
