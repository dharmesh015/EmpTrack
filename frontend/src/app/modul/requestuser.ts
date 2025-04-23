export class Requestuser {
    
    userName: string;
    userPassword: string;
    captcha:string;
  
    constructor(userName: string = '', password: string = '',captcha='') {
      this.userName = userName;
      this.userPassword = password;
      this.captcha=captcha;
    }
}
