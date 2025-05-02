import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NgForm } from '@angular/forms';
import { catchError, map, Observable } from 'rxjs';
import { Requestuser } from '../modul/requestuser';
import { UserAuthServiceService } from './user-auth-service.service';

import { UserDetailsProxy } from '../modul/user-details-proxy';
;

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

  public register(data: UserDetailsProxy): Observable<string> {
    console.log('Sending Data: ' + JSON.stringify(data));
    return this.httpclient
      .post(this.apiUrl + '/registerNewUser ', data, { responseType: 'text' })
      .pipe(map((response) => response as string));
  }

  registerWithImage(formData: FormData): Observable<string> {
    return this.httpclient.post(`${this.apiUrl}/registerWithImage`, formData,{ responseType: 'text' }).pipe(map((response) => response as string));
  }

  public getuser(): Observable<any> {
    const token = this.userAuthService.getToken();
    return this.httpclient
      .get('http://localhost:9090/getdata/' + token, {
        responseType: 'text',
      })
      .pipe(map((response) => response as string));
  }

  sendEmail(email: string, captcha: string): Observable<string> {
    return this.httpclient.post(
      `${this.apiUrl}/send-email/${email}/${captcha}`,
      {}, 
      {
        headers: this.requestHeader.append('No-Auth', 'True'),
        withCredentials: true, 
        responseType: 'text'
      }
    );}

  resetPassword(token: string, newPassword: string): Observable<any> {
    console.log(token, newPassword);
    return this.httpclient
      .get(`${this.apiUrl}/reset-password/${token}/${newPassword}`, {
        responseType: 'text',
      })
      .pipe(map((response) => response as string));
  }

  validateResetToken(token: string): Observable<any> {
    return this.httpclient.get(`${this.apiUrl}/validate-token/${token}`);
  }

 

 

  deleteUser(name: String): any {
    console.log('service' + name);
    return this.httpclient.delete(`${this.apiUrl}/deleteUser/${name}`);
  }

  public update(data: UserDetailsProxy) {
    console.log('Sending Datadsd: ' + JSON.stringify(data));
    return this.httpclient
      .put(this.apiUrl + '/updateUser', data, { responseType: 'text' })
      .pipe(map((response) => response as string));
  }

  getUserByName(name: String): Observable<UserDetailsProxy> {
    return this.httpclient.get<UserDetailsProxy>(
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

  // getCaptchaImage(): Observable<Blob> {
  //   return this.httpclient.get(this.apiUrl + '/captcha', {
  //     responseType: 'blob',
  //     withCredentials: true,
  //   });
  // }

  getCaptchaImage(timestamp?: number): Observable<Blob> {
    const url = timestamp ? `${this.apiUrl}/captcha?t=${timestamp}` : `${this.apiUrl}/captcha`;
    return this.httpclient.get(url, {
      responseType: 'blob',
      withCredentials: true,
    });
  }
  getAllUsers(): Observable<any[]> {
    return this.httpclient.get<any[]>(`${this.apiUrl}/users`);
  }

  getAllUsersPageWise(page: number, size: number): Observable<any> {
    return this.httpclient.get(
      `${this.apiUrl}/getAllUsersPageWise?page=${page}&size=${size}`
    );
  }

  getImageUrl(imageUuid: string): string {
    return `${this.apiUrl}/user-image/${imageUuid}`;
  }
  searchUsers(query: string, page: number, size: number): Observable<any> {
    return this.httpclient.get(`${this.apiUrl}/searchUsers?query=${encodeURIComponent(query)}&page=${page}&size=${size}`);
  }
  
}
