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

export class TicketStatusDetail {
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
