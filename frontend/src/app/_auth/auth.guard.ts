
import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { UserAuthServiceService } from '../_service/user-auth-service.service';
import { UserService } from '../_service/user.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard implements CanActivate {
  constructor(
    private userAuthService: UserAuthServiceService,
    private router: Router,
    private userService: UserService
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {

    const token = this.userAuthService.getToken();
    if (!token) {
      this.router.navigate(['/login']);
      return false;
    }

    
    const requiredRoles = route.data['roles'] as Array<string>;
    if (requiredRoles && !this.userService.roleMatch(requiredRoles)) {
      this.router.navigate(['/forbidden']);
      return false;
    }

    return true;
  }
}