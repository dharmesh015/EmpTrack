export class Registrationuser {
  name: string;
  dob: string;
  userName: string;
  password: string;
  email:string;
  gender: string;
  address: string;
  profileImage?: String; 
  contactNumber: string;
  pinCode: string;
  userFirstName: any;
  

 

  constructor(
    name?: string,
    dob?: string,
    userName?: string,
    password?: string,
    email?:string,
    gender?: string,
    address?: string,
    profileImage?: String,
    contactNumber?: string,
    pinCode?: string,

  ) {

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

  }
}

