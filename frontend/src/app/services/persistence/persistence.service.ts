import {Injectable} from '@angular/core';

const STORE_PREFIX = "logistiekapp";

@Injectable({
  providedIn: 'root'
})
export class PersistenceService {
  private name(key: string) {
    return `${STORE_PREFIX}.${key}`
  }

  store(key: string, value: any): void {
    localStorage.setItem(this.name(key), JSON.stringify(value));
  }

  contains(key: string): boolean {
    return localStorage.getItem(this.name(key)) != null;
  }

  get(key: string): any {
    const content = localStorage.getItem(this.name(key));
    if (!content)
      return null;

    try {
      return JSON.parse(content);
    } catch (e) {
      console.error('Corrupt persistence item %s', key);
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
