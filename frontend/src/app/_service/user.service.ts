import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NgForm } from '@angular/forms';
import { catchError, map, Observable } from 'rxjs';
import { Requestuser } from '../modul/requestuser';
import { UserAuthServiceService } from './user-auth-service.service';
import { Registrationuser } from '../modul/registrationuser';
import { userdata } from '../modul/userdata';
// import { Requestuser } from '../modul/requestuser';


@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = 'http://localhost:9090';

  requestHeader = new HttpHeaders({ 'Content-Type': 'application/json' });
  constructor(
    private httpclient: HttpClient,
    private userAuthService: UserAuthServiceService
  ) {}
  userdata: Requestuser = new Requestuser('', '');

  public login(data: Requestuser): Observable<any> {
    console.log('Sending Data: ' + JSON.stringify(data));
    return this.httpclient.post(this.apiUrl + '/authenticate', data, {
      headers: this.requestHeader.append('No-Auth', 'True'),
      withCredentials: true,
    });
  }

  public roleMatch(allowedRoles: string[]): boolean {
    const userRoles = this.userAuthService.getRoles();

    if (userRoles && userRoles.length > 0) {
      for (const userRole of userRoles) {
        if (allowedRoles.includes(userRole.roleName)) {
          return true;
        }
      }
    }
    return false;
  }

  public register(data: Registrationuser): Observable<string> {
    console.log('Sending Data: ' + JSON.stringify(data));
    return this.httpclient
      .post(this.apiUrl + '/registerNewUser ', data, { responseType: 'text' })
      .pipe(map((response) => response as string));
  }
  public getuser(): Observable<any> {
    const token = this.userAuthService.getToken();
    return this.httpclient
      .get('http://localhost:9090/getdata/' + token, {
        responseType: 'text',
      })
      .pipe(map((response) => response as string));
  }
  sendEmail(email: string): Observable<string> {
    return this.httpclient
      .post(
        this.apiUrl + '/send-email',
        { email: email },
        { responseType: 'text' }
      )
      .pipe(map((response) => response as string));
  }

  resetPassword(email: string, newPassword: string): Observable<any> {
    console.log(email, newPassword);
    return this.httpclient
      .get(`${this.apiUrl}/reset-password/${email}/${newPassword}`, {
        responseType: 'text',
      })
      .pipe(map((response) => response as string));
  }

  validateResetToken(token: string): Observable<any> {
    return this.httpclient.get(`${this.apiUrl}/validate-token/${token}`);
  }

  getAllUsers(): Observable<userdata[]> {
    console.log('service');
    return this.httpclient.get<userdata[]>(`${this.apiUrl}/admin/getAlluser`);
  }

  getAllUsersPageWise(page: number, size: number): Observable<any> {
    return this.httpclient.get(
      `${this.apiUrl}/getAllUsersPageWise?page=${page}&size=${size}`
    );
  }

  deleteUser(name: String): any {
    console.log('service' + name);
    return this.httpclient.delete(`${this.apiUrl}/deleteUser/${name}`);
  }

  public update(data: Registrationuser) {
    console.log('Sending Datadsd: ' + JSON.stringify(data));
    return this.httpclient
      .put(this.apiUrl + '/updateUser', data, { responseType: 'text' })
      .pipe(map((response) => response as string));
  }

  //
  getUserByName(name: String): Observable<Registrationuser> {
    return this.httpclient.get<Registrationuser>(
      `${this.apiUrl}/getuser/${name}`
    );
  }

  SendEmailForRole(name: string, email: string): Observable<string> {
    console.log('Registration seller service!');
    return this.httpclient
      .post(
        this.apiUrl + '/send-email-for-role/' + name,
        { email: email },
        { responseType: 'text' }
      )
      .pipe(map((response) => response as string));
  }

  updateUserRole(userName: string, newRole: string): Observable<any> {
    console.log('updaterole sevice' + newRole);
    return this.httpclient.get(
      this.apiUrl + `/updateUserRole/${userName}/${newRole}`,
      {
        responseType: 'text',
      }
    );
  }

  getCaptchaImage(): Observable<Blob> {
    return this.httpclient.get(this.apiUrl + '/captcha', {
      responseType: 'blob',
      withCredentials: true,
    });
  }
 
}
