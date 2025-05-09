// export interface ApiResponse {
//     // errors: ApiResponse;
//     // status: number;
//     // code: string;
//     // message: string;
//     // data?: any;
//     status: number | string;
//   message: string;
//   code?: string;
//   data?: any;
//   errors?: string[];
//   }
export interface ApiResponse {
  status: number;
  code?: string;
  message: string;
  data?: any;
  errors?: string[];
}

export interface User {
  id?: number;
  name: string;
  userName: string;
  email: string;
  password?: string;
  dob: string;
  gender: string;
  address: string;
  contactNumber: string;
  pinCode: string;
  profileImageUrl?: string;
}