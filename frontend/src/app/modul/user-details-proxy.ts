export class UserDetailsProxy {
    id: number;
    name: string;
    dob: string;
    userName: string;
    password: string;
    email:string;
    gender: string;
    address: string;
    imageUuid?: string; 
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
      imageUuid?: string,
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
      this.imageUuid = imageUuid || '';
      this.contactNumber = contactNumber || '';
      this.pinCode = pinCode || '';
      this.accessRole = accessRole || '';
    }
  }
  