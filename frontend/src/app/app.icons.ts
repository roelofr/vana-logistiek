import {MatIconRegistry} from '@angular/material/icon';
import {inject, provideAppInitializer} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';

const NAMESPACE = 'app';
const iconsToLoad = new Map([['logo', '/logo-css.svg']]);

const staticIcon = `
<?xml version="1.0" encoding="utf-8"?>
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 64 64">
  <rect x="1" y="1" width="62" height="62" fill="orange" stroke="black" />
</svg>`;

const TRUSTED_ORIGIN = new URL('https://example.com/').origin;
const getTrustworthyPath = (icon: string, path: string) => {
  const url = new URL(path, TRUSTED_ORIGIN);

  if (url.origin !== TRUSTED_ORIGIN)
    throw new Error(`Icon ${icon} tries to switch origins, blocking request`);

  return url.pathname;
}

function registerIcons(): void {
  const iconRegistry = inject(MatIconRegistry);
  const sanitizer = inject(DomSanitizer);

  iconRegistry.setDefaultFontSetClass('material-symbols-outlined');

  for (const [name, path] of iconsToLoad) {
    iconRegistry.addSvgIconInNamespace(
      NAMESPACE,
      name,
      sanitizer.bypassSecurityTrustResourceUrl(getTrustworthyPath(name, path)),
    );
  }
}

function registerTestIcons(): void {
  const iconRegistry = inject(MatIconRegistry);
  const sanitizer = inject(DomSanitizer);

  iconRegistry.setDefaultFontSetClass('material-symbols-outlined');

  for (const name of iconsToLoad.keys()) {
    iconRegistry.addSvgIconLiteralInNamespace(
      NAMESPACE,
      name,
      sanitizer.bypassSecurityTrustHtml(staticIcon),
    );
  }
}

export const provideAppIcons = () => provideAppInitializer(registerIcons);

export const provideTestAppIcons = () =>
  provideAppInitializer(registerTestIcons);
