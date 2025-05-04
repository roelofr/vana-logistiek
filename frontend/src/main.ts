import { bootstrapApplication } from '@angular/platform-browser'
import { appConfig } from './app/app.config'
import { AppComponent } from './app/app.component'
import {registerIcons} from './icons';

registerIcons();

bootstrapApplication(AppComponent, appConfig).catch((err) => console.error(err))
