declare interface Vendor {
  id: number;
  name: string;
  number: string;
  numberNumeric: number;
  district: DistrictRef;
}

declare type VendorRef = Omit<Vendor, 'district'>;

declare interface User {
  name: string;
  email: string;
  active: boolean;
  roles: string[];
  district: DistrictRef;
}

declare type UserRef = Omit<User, 'district'>;

declare interface District {
  name: string;
  mobileName: string;
  colour: string;
  users: UserRef[];
  vendors: VendorRef[];
}

declare type DistrictRef = Omit<DistrictRef, 'users' | 'vendors'>;
