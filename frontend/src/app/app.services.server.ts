import {PersistenceService} from './services/persistence/persistence.service';
import {makeEnvironmentProviders} from '@angular/core';

const serverSideDummyStorage = {
  store(key: string, value: any): void {
    //
  },

  contains(key: string): boolean {
    return false;
  },

  get(key: string): any {
    return null;
  },

  forget(key: string): void {
    //
  },

  clear(): void {
    //
  }
} as PersistenceService;

export const provideServerServices = () => makeEnvironmentProviders([
  {provide: PersistenceService, useValue: serverSideDummyStorage},
])
