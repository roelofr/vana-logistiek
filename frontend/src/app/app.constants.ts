export enum Role {
  Admin = 'role.admin',
  User = 'role.user',
  CentralePost = 'role.centrale-post'
}

export enum TicketStatus {
  Created = 'Created',
  Assigned = 'Assigned',
  Updated = 'Updated',
  Resolved = 'Resolved',
}

class TicketStatusDetail {
  constructor(readonly label: string, readonly icon: string) {
    //
  }
}

export const TicketStatusDetails = Object.freeze(new Map([
  [TicketStatus.Created, new TicketStatusDetail('Nieuw', 'upcoming')],
  [TicketStatus.Assigned, new TicketStatusDetail('Bijgewerkt', 'star-shine')],
  [TicketStatus.Updated, new TicketStatusDetail('Toegewezen', 'person')],
  [TicketStatus.Resolved, new TicketStatusDetail('Opgelost', 'check-small')],
]))

export enum TicketType {
  Generic = 'generic',
  Hulp = 'hulp',
  Materialen = 'materialen',
}

class TicketTypeDetail {
  constructor(readonly label: string,
              readonly icon: string,
              readonly summary: string) {
    //
  }
}

export const TicketTypeDetails = Object.freeze(new Map([
  [TicketType.Generic, new TicketTypeDetail('Generiek ticket', 'assignment', 'Generiek ticket voor de CP om uit te zoeken')],
  [TicketType.Hulp, new TicketTypeDetail('Hulp', 'help', 'Bel de MeningBrigade')],
  [TicketType.Materialen, new TicketTypeDetail('Materiaal', 'forklift', 'Laat een Gator of Heftruck aanroepen voor ontbrekende material of hulp')],
]))
