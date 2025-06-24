import {DateTime} from 'luxon';
import {TicketStatus} from './app.constants';

export interface Vendor {
    id: number;
    name: string;
    number: string;
    numberNumeric: number;
    district: DistrictRef;
}

export type VendorRef = Omit<Vendor, 'district'>;

export interface Ticket {
    id: number;
    createdAt: DateTime;
    updatedAt: DateTime;
    completedAt: DateTime;
    description: string;
    status: TicketStatus;
    vendor: VendorRef;
    creator: UserRef;
}

export type TicketRef = Omit<Ticket, 'vendor' | 'creator'>;

export interface User {
    name: string;
    email: string;
    active: boolean;
    roles: string[];
    district: DistrictRef;
}

export type UserRef = Omit<User, 'district'>;

export interface District {
    name: string;
    mobileName: string;
    colour: string;
    users: UserRef[];
    vendors: VendorRef[];
}

export type DistrictRef = Omit<District, 'users' | 'vendors'>;

export type TicketDetails = { summary: string } & Record<string, unknown>;
