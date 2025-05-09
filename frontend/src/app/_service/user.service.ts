import { Injectable } from '@angular/core';
import { UserAuthServiceService } from './user-auth-service.service';
import { catchError, map, Observable, throwError } from 'rxjs';
import { ApiResponse } from '../modul/api-response';
import { UserDetailsProxy } from '../modul/user-details-proxy';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = 'http://localhost:9099'; // Adjust this to your API URL
  requestHeader = new HttpHeaders({ 'No-Auth': 'True' });
  
  constructor(
    private http: HttpClient,
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
  
  
  public addUserWithImage(formData: FormData): Observable<ApiResponse> {
    return this.httpclient.post<ApiResponse>(
      `${this.apiUrl}/registerWithImage`,
      formData,
    );
  }
  
  public getUsersByRole(role: string, page: number, size: number, sortBy: string, direction: string): Observable<any> {
    let params = new HttpParams()
      .set('role', role)
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('direction', direction)
      ;
      
    return this.httpclient.get<any>(`${this.apiUrl}/getUsersByRole`, { params });
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
  
 
  
  
  // Hard delete a user (kept for admin purposes, but should be used cautiously)
  public deleteUser(userName: string): Observable<ApiResponse> {
    return this.httpclient.delete<ApiResponse>(`${this.apiUrl}/deleteUser/${userName}`);
  }
  
  // Update user information
  public updateUser(userData: UserDetailsProxy): Observable<ApiResponse> {
    return this.httpclient.put<ApiResponse>(`${this.apiUrl}/updateUser`, userData);
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



  public sendEmail(email: string): Observable<ApiResponse> {
    const emailRequest = { email: email };
    return this.httpclient.post<ApiResponse>(`${this.apiUrl}/send-email`, emailRequest, {
      headers: this.requestHeader
    });
  }

  // Reset password with token
  public resetPassword(token: string, newPassword: string): Observable<ApiResponse> {
    return this.httpclient.get<ApiResponse>(`${this.apiUrl}/reset-password/${token}/${newPassword}`);
  }

  // Validate reset token
  public validateResetToken(token: string): Observable<ApiResponse> {
    return this.httpclient.get<ApiResponse>(`${this.apiUrl}/validate-token/${token}`);
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
  // checkEmailExists(email: string): Observable<boolean> {
  //   return this.httpclient.get<boolean>(`${this.apiUrl}/check-email/`+email);
  // }
  // uploadUsersFile(formData: FormData): Observable<any> {
  //   return this.httpclient.post(`${this.apiUrl}/upload-users`, formData);
  // }

  // // addUser(userData: any): Observable<ApiResponse> {
  // //   return this.httpclient.post<ApiResponse>(`${this.apiUrl}/registerNewUser`, userData);
  // // }
  // downloadUserTemplate(): Observable<Blob> {
  //   return this.httpclient.get(`${this.apiUrl}/download-template`, {
  //     responseType: 'blob'
  //   });
  // }
  checkEmail(email: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/check-email/${email}`)
      .pipe(catchError(this.handleError));
  }

  /**
   * Register a single user with profile image
   */
  registerUser(userData: any, file?: File): Observable<any> {
    const formData = new FormData();
    
    // Convert date format if needed
    if (userData.dob && userData.dob instanceof Date) {
      userData.dob = userData.dob.toISOString().split('T')[0];
    }
    
    formData.append('userData', JSON.stringify(userData));
    
    if (file) {
      formData.append('file', file);
    }
    
    return this.http.post(`${this.apiUrl}/registerWithImage`, formData)
      .pipe(catchError(this.handleError));
  }

  /**
   * Upload multiple users from Excel or CSV file
   */
  uploadUsers(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    
    return this.http.post(`${this.apiUrl}/upload-users`, formData)
      .pipe(catchError(this.handleError));
  }

  /**
   * Download blank Excel template for bulk user upload
   */
  downloadTemplate(): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/download-template`, {
      responseType: 'blob'
    }).pipe(catchError(this.handleError));
  }

  /**
   * Error handler for HTTP requests
   */
  private handleError(error: HttpErrorResponse) {
    let errorResponse: ApiResponse = {
      status: error.status || 500,
      message: 'An unknown error occurred!'
    };
    
    // Client-side error
    if (error.error instanceof ErrorEvent) {
      errorResponse.message = `Error: ${error.error.message}`;
      
    // Server-side error
    } else if (error.error) {
      console.log('Raw error response:', error.error);
      
      // If the error is already structured as an object
      if (typeof error.error === 'object' && !Array.isArray(error.error)) {
        // Copy status and message directly from the error response
        if (error.error.status) errorResponse.status = error.error.status;
        if (error.error.message) errorResponse.message = error.error.message;
        if (error.error.code) errorResponse.code = error.error.code;
        if (error.error.errors) errorResponse.errors = error.error.errors;
      }
      // If the error is a string, try to parse it as JSON
      else if (typeof error.error === 'string') {
        try {
          const parsedError = JSON.parse(error.error);
          if (parsedError.status) errorResponse.status = parsedError.status;
          if (parsedError.message) errorResponse.message = parsedError.message;
          if (parsedError.code) errorResponse.code = parsedError.code;
          if (parsedError.errors) errorResponse.errors = parsedError.errors;
        } catch (e) {
          // If parsing fails, use the string as the message
          errorResponse.message = error.error;
        }
      }
    }
    
    // Fallback - if status and message weren't properly set
    if (!errorResponse.message && error.statusText) {
      errorResponse.message = error.statusText;
    }
    
    // Use the HTTP status code from the error if it exists
    if (error.status) {
      errorResponse.status = error.status;
    }
    
    console.log('Processed error response:', errorResponse);
    return throwError(() => errorResponse);
  }
}