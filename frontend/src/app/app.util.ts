import {Vendor, VendorRef} from './app.domain';
import {TicketStatus} from './app.constants';

const VENDOR_CODE_MATCH = /^([1-9][0-9]+)([a-z]+)?$/;
const vendorNameCache = new Map();

export const vendorToSortableNumber = (vendor: Vendor | VendorRef): number => {
  if (vendorNameCache.has(vendor.id))
    return vendorNameCache.get(vendor.id);

  const vendorMatches = VENDOR_CODE_MATCH.exec(vendor.number);
  if (vendorMatches == undefined) {
    vendorNameCache.set(vendor.id, vendor.id);
    return vendor.id;
  }

  const number = parseInt(vendorMatches[1], 10)
  const suffix = (vendorMatches[2] ?? 'a').trim().toLowerCase().charCodeAt(0);

  const order = number * 100 + suffix;
  vendorNameCache.set(vendor.id, order);
  return order;
}

const statusOrder = [
  TicketStatus.Created,
  TicketStatus.Updated,
  TicketStatus.Assigned,
  TicketStatus.Resolved
]

export const ticketStatusToOrder = (status: TicketStatus): number => {
  return statusOrder.indexOf(status) ?? 99;
}
