import {inject, Injectable} from '@angular/core';
import {RouterStateSnapshot, TitleStrategy} from '@angular/router';
import {Title} from '@angular/platform-browser';

@Injectable({
  providedIn: 'root',
})
export class TemplatePageTitleStrategy extends TitleStrategy {
  private readonly appName = 'LogistiekApp';
  private readonly title = inject(Title);

  override updateTitle(routerState: RouterStateSnapshot) {
    const title = this.buildTitle(routerState);

    if (title !== undefined) {
      this.title.setTitle(`${this.appName} | ${title}`);
    } else {
      this.title.setTitle(`${this.appName}`);
    }
  }
}
