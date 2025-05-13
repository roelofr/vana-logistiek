import {Injectable} from '@angular/core';

const STORE_PREFIX = "logistiekapp";

@Injectable({
  providedIn: 'root'
})
export class PersistenceService {
  private name(key: string) {
    return `${STORE_PREFIX}.${key}`
  }

  store(key: string, value: unknown): void {
    localStorage.setItem(this.name(key), JSON.stringify(value));
  }

  contains(key: string): boolean {
    return localStorage.getItem(this.name(key)) != null;
  }

  get(key: string): unknown {
    const content = localStorage.getItem(this.name(key));
    if (!content)
      return null;

    try {
      return JSON.parse(content);
    } catch (error) {
      console.error('Corrupt persistence item %s: %o', key, error);
      this.forget(key);
      return null;
    }
  }

  forget(key: string): void {
    localStorage.removeItem(this.name(key));
  }

  clear(): void {
    localStorage.clear();
  }
}
