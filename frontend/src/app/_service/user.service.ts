import { Injectable } from '@angular/core';
import { UserAuthServiceService } from './user-auth-service.service';
import { map, Observable } from 'rxjs';
import { ApiResponse } from '../modul/api-response';
import { UserDetailsProxy } from '../modul/user-details-proxy';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = 'http://localhost:9099'; // Adjust this to your API URL
  requestHeader = new HttpHeaders({ 'No-Auth': 'True' });
  
  constructor(
    private httpclient: HttpClient,
    private userAuthService: UserAuthServiceService
  ) {}
  
  public login(loginData: any) {
    return this.httpclient.post(`${this.apiUrl}/authenticate`, loginData, {
      headers: this.requestHeader,
    });
  }
  
  public forUser() {
    return this.httpclient.get(`${this.apiUrl}/forUser`, {
      responseType: 'text',
    });
  }
  
  public forAdmin() {
    return this.httpclient.get(`${this.apiUrl}/forAdmin`, {
      responseType: 'text',
    });
  }
  
  public roleMatch(allowedRoles: string[]): boolean {
    let isMatch = false;
    const userRoles: any = this.userAuthService.getRoles();
    if (userRoles != null && userRoles) {
      for (let i = 0; i < userRoles.length; i++) {
        for (let j = 0; j < allowedRoles.length; j++) {
          if (userRoles[i].roleName === allowedRoles[j]) {
            isMatch = true;
            return isMatch;
          }
        }
      }
    }
    return isMatch;
  }
  
  public register(userData: any): Observable<ApiResponse> {
    return this.httpclient.post<ApiResponse>(
      `${this.apiUrl}/registerNewUser`,
      userData,
      {
        headers: this.requestHeader,
      }
    );
  }
  
  public registerWithImage(formData: FormData): Observable<ApiResponse> {
    return this.httpclient.post<ApiResponse>(
      `${this.apiUrl}/registerWithImage`,
      formData,
      {
        headers: this.requestHeader,
      }
    );
  }

  // Get all users with pagination and active status filter
  public getAllUsersPageWise(page: number, size: number, sortBy: string, direction: string, activeOnly: boolean = true): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('direction', direction)
      .set('activeOnly', activeOnly.toString());
      
    return this.httpclient.get<any>(`${this.apiUrl}/getAllUsersPageWise`, { params });
  }
  
  // Get users by role with pagination and active status filter
  public getUsersByRole(role: string, page: number, size: number, sortBy: string, direction: string, activeOnly: boolean = true): Observable<any> {
    let params = new HttpParams()
      .set('role', role)
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('direction', direction)
      .set('activeOnly', activeOnly.toString());
      
    return this.httpclient.get<any>(`${this.apiUrl}/getUsersByRole`, { params });
  }
  
  // Search users with active status filter
  public searchUsers(query: string, page: number, size: number, sortBy: string, direction: string, activeOnly: boolean = true): Observable<any> {
    let params = new HttpParams()
      .set('query', query)
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('direction', direction)
      .set('activeOnly', activeOnly.toString());
      
    return this.httpclient.get<any>(`${this.apiUrl}/searchUsers`, { params });
  }
  
  // Search users by role with active status filter
  public searchUsersByRole(role: string, query: string, page: number, size: number, sortBy: string, direction: string, activeOnly: boolean = true): Observable<any> {
    let params = new HttpParams()
      .set('role', role)
      .set('query', query)
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('direction', direction)
      .set('activeOnly', activeOnly.toString());
      
    return this.httpclient.get<any>(`${this.apiUrl}/searchUsersByRole`, { params });
  }
   
  // Get a specific user by username
  public getUser(userName: string): Observable<UserDetailsProxy> {
    return this.httpclient.get<ApiResponse>(`${this.apiUrl}/getuser/${userName}`)
      .pipe(
        map((response: any) => {
          if (response && response.data) {
            return response.data;
          }
          return {} as UserDetailsProxy;
        })
      );
  }
  
  // Deactivate a user (soft delete)
  public deactivateUser(userName: string): Observable<ApiResponse> {
    return this.httpclient.put<ApiResponse>(`${this.apiUrl}/deactivateUser/${userName}`, {});
  }
  
  // Reactivate a user
  public reactivateUser(userName: string): Observable<ApiResponse> {
    return this.httpclient.put<ApiResponse>(`${this.apiUrl}/reactivateUser/${userName}`, {});
  }
  
  // Hard delete a user (kept for admin purposes, but should be used cautiously)
  public deleteUser(userName: string): Observable<ApiResponse> {
    return this.httpclient.delete<ApiResponse>(`${this.apiUrl}/deleteUser/${userName}`);
  }
  
  // Update user information
  public updateUser(userData: UserDetailsProxy): Observable<ApiResponse> {
    return this.httpclient.put<ApiResponse>(`${this.apiUrl}/updateUser`, userData);
  }
  
  // Generate fake users for testing
  public generateFakeUsers(): Observable<ApiResponse> {
    return this.httpclient.get<ApiResponse>(`${this.apiUrl}/generate-fake-users`);
  }
  
  // Get image URL from UUID
  public getImageUrl(imageUuid: string): string {
    return `${this.apiUrl}/user-image/${imageUuid}`;
  }
  
  // Upload user image
  public uploadUserImage(userName: string, imageFile: File): Observable<ApiResponse> {
    const formData = new FormData();
    formData.append('file', imageFile);
    formData.append('userName', userName);
    
    return this.httpclient.post<ApiResponse>(`${this.apiUrl}/uploadUserImage`, formData);
  }
  
  // Get user statistics
  public getUserStats(): Observable<any> {
    return this.httpclient.get<any>(`${this.apiUrl}/userStats`);
  }
  
  // Export users to Excel (server-side if implemented)
  public exportUsersToExcel(): Observable<Blob> {
    return this.httpclient.get(`${this.apiUrl}/exportUsers`, {
      responseType: 'blob'
    });
  }
  
  // Additional helper methods for the admin panel
  
  // Get user roles for dropdown
  public getAllRoles(): Observable<any> {
    return this.httpclient.get<any>(`${this.apiUrl}/getAllRoles`);
  }
  
  // Check if username exists
  public checkUsernameExists(userName: string): Observable<boolean> {
    return this.httpclient.get<boolean>(`${this.apiUrl}/checkUsername/${userName}`);
  }
  
  // Check if email exists
  public checkEmailExists(email: string): Observable<boolean> {
    return this.httpclient.get<boolean>(`${this.apiUrl}/checkEmail/${email}`);
  }

  // Password reset and forgot password functionality
  
  // Send password reset email
  public sendEmail(email: string): Observable<string> {
    const emailRequest = { email: email };
    return this.httpclient.post(`${this.apiUrl}/send-email`, emailRequest, {
      headers: this.requestHeader,
      responseType: 'text'
    });
  }

  // Reset password with token
  public resetPassword(token: string, newPassword: string): Observable<string> {
    return this.httpclient.get(`${this.apiUrl}/reset-password/${token}/${newPassword}`, {
      responseType: 'text'
    });
  }

  // Get CAPTCHA image
  public getCaptchaImage(timestamp: number): Observable<Blob> {
    return this.httpclient.get(`${this.apiUrl}/captcha-image?t=${timestamp}`, {
      headers: this.requestHeader,
      responseType: 'blob'
    });
  }

  // Verify CAPTCHA
  public verifyCaptcha(captchaResponse: string): Observable<boolean> {
    return this.httpclient.post<boolean>(`${this.apiUrl}/verify-captcha`, { captchaResponse }, {
      headers: this.requestHeader
    });
  }

  validateResetToken(token: string): Observable<any> {
    return this.httpclient.get(`${this.apiUrl}/validate-token/${token}`);
  }
}