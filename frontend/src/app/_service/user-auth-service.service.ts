import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Registrationuser } from '../modul/registrationuser';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})

export class UserAuthServiceService {
  constructor(@Inject(PLATFORM_ID) private platformId: object) {}
  private loggedInSubject = new BehaviorSubject<boolean>(
    this.checkInitialLoginState()
  );
  public loggedIn$ = this.loggedInSubject.asObservable();

  private checkInitialLoginState(): boolean {
    return !!this.getToken() && !!this.getRoles();
  }

  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  public setRoles(roles: any[]): void {
    if (this.isBrowser()) {
      sessionStorage.setItem('roles', JSON.stringify(roles));
    }
  }

  public setUser(user: any) {
    sessionStorage.setItem('user', JSON.stringify(user));
  }

  public getUser() {
    const data = sessionStorage.getItem('user');

    return data ? JSON.parse(data) : null;
  }

  public getRoles(): any[] {
    if (this.isBrowser()) {
      const roles = sessionStorage.getItem('roles');
      return roles ? JSON.parse(roles) : [];
    }
    return [];
  }
  // public setToken(jwtToken: string): void {
  //   if (this.isBrowser()) {
  //     sessionStorage.setItem('jwtToken', jwtToken);
  //   }
  // }
  public setToken(jwtToken: string): void {
    if (this.isBrowser()) {
      sessionStorage.setItem('jwtToken', jwtToken);
      this.loggedInSubject.next(true);
    }
  }

  public getToken(): string | null {
    if (this.isBrowser()) {
      return sessionStorage.getItem('jwtToken');
    }
    return null;
  }

  public clear(): void {
    if (this.isBrowser()) {
      sessionStorage.removeItem('jwtToken');
      sessionStorage.removeItem('roles');
      sessionStorage.removeItem('email');
      sessionStorage.removeItem('name');
      sessionStorage.removeItem('user');
      this.loggedInSubject.next(false);
    }
  }

  public isLoggedIn(): boolean {
    return !!this.getToken() && !!this.getRoles();
  }

  public isAdmin(): boolean {
    const roles: any[] = this.getRoles();
    return roles.length > 0 && roles[0].roleName === 'Admin';
  }

 
  public isUser(): boolean {
    const roles: any[] = this.getRoles();
    return roles.length > 0 && roles[0].roleName === 'User';
  }
}
