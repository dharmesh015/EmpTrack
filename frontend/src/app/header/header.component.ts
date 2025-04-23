
import { Component, ChangeDetectorRef, OnInit } from '@angular/core';
import { UserAuthServiceService } from '../_service/user-auth-service.service';
import { Router } from '@angular/router';
import { UserService } from '../_service/user.service';

@Component({
  selector: 'app-header',
  standalone: false,
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css'],
})
export class HeaderComponent implements OnInit {
  isMenuOpen = false;
  isLoggedInflag = false;
  isAccountDropdownOpen = false;

  constructor(
    private userAuthService: UserAuthServiceService,
    private userService: UserService,
    private router: Router,
    private cdRef: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    // Subscribe to auth state changes
    this.userAuthService.loggedIn$.subscribe(isLoggedIn => {
      this.isLoggedInflag = isLoggedIn;
      this.cdRef.detectChanges();
    });
    this.updateLoginStatus();
  }

  // Update login status
  private updateLoginStatus(): void {
    this.isLoggedInflag = this.userAuthService.isLoggedIn();
    this.cdRef.detectChanges(); // Force view update
  }

  toggleMenu(): void {
    this.isMenuOpen = !this.isMenuOpen;
    if (!this.isMenuOpen) {
      this.isAccountDropdownOpen = false;
    }
  }
  
  toggleAccountDropdown(): void {
    this.isAccountDropdownOpen = !this.isAccountDropdownOpen;
    setTimeout(() => {
      this.isAccountDropdownOpen = false; 
    }, 2000);
  }

  public logout() {
    this.userAuthService.clear();
    this.isLoggedInflag = false; // Update local flag
    this.router.navigate(['/home']);
  }

  public isLoggedIn(): boolean {
    return this.isLoggedInflag; // Use the local flag
  }

  public roleMatchs(rol: any): boolean {
    return this.userService.roleMatch(rol);
  }

  public isAdmin(): boolean {
    return this.userAuthService.isAdmin();
  }

  public isUser(): boolean {
    return this.userAuthService.isUser();
  }


  public Profilepage() {
    this.router.navigate(['/Profilepage']);
  }
}