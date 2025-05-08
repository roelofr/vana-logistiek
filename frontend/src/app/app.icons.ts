import { MatIconRegistry } from '@angular/material/icon';
import { inject, provideAppInitializer } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';

const NAMESPACE = 'app';
const iconsToLoad = new Map([['logo', '/logo-css.svg']]);

const staticIcon = `
<?xml version="1.0" encoding="utf-8"?>
<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 64 64">
  <rect x="1" y="1" width="62" height="62" fill="orange" stroke="black" />
</svg>`;

function registerIcons(): void {
  const iconRegistry = inject(MatIconRegistry);
  const sanitizer = inject(DomSanitizer);

  iconRegistry.setDefaultFontSetClass('material-symbols-outlined');

  for (const [name, path] of iconsToLoad) {
    const url = new URL(path, document.location.href);
    if (url.origin !== document.location.origin)
      throw new Error(`Icon ${name} tries to switch origins, blocking request`);

    iconRegistry.addSvgIconInNamespace(
      NAMESPACE,
      name,
      sanitizer.bypassSecurityTrustResourceUrl(url.href),
      { withCredentials: true },
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
