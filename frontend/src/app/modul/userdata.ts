export class userdata{
  id: number;
  name: string;
  dob: string;
  userName: string;
  password: string;
  email:string;
  gender: string;
  address: string;
  profileImage?: Uint8Array; // Optional as you might not always want to transfer this
  contactNumber: string;
  pinCode: string;
  accessRole: string;

  constructor(
    id?: number,
    name?: string,
    dob?: string,
    userName?: string,
    password?: string,
    email?:string,
    gender?: string,
    address?: string,
    profileImage?: Uint8Array,
    contactNumber?: string,
    pinCode?: string,
    accessRole?: string
  ) {
    this.id = id || 0;
    this.name = name || '';
    this.dob = dob || '';
    this.userName = userName || '';
    this.password = password || '';
    this.email = email || '';
    this.gender = gender ||'';
    this.address = address || '';
    this.profileImage = profileImage;
    this.contactNumber = contactNumber || '';
    this.pinCode = pinCode || '';
    this.accessRole = accessRole || '';
  }
}
