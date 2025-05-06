
import { Component, OnInit } from '@angular/core';
import { UserAuthServiceService } from '../_service/user-auth-service.service';
import { Router } from '@angular/router';
import { UserService } from '../_service/user.service';
import { NgForm } from '@angular/forms';
import Swal from 'sweetalert2';
import { Requestuser } from '../modul/requestuser';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent  {
  captchaUrl: string = '';
  userdata: Requestuser = new Requestuser('', '');
  

  constructor(
    private loginService: UserService,
    private userAuthService: UserAuthServiceService,
    private router: Router
  ) {}

 
  

 





  isloggin() {
    return !this.userAuthService.isLoggedIn();
  }

  login(form: NgForm) {
    if (form.invalid) {
      Object.keys(form.controls).forEach((field) => {
        const control = form.controls[field];
        control.markAsTouched({ onlySelf: true });
      });
      return;
    }
  
    const captcha = form.value.captcha;
    this.userdata.userName = form.value.username;
    this.userdata.userPassword = form.value.password;
    this.userdata.captcha = captcha;
  
    this.loginService.login(this.userdata).subscribe(
      (response: any) => {
      
        if (response.code === 'InvalidCredentials') {
          Swal.fire({
            title: 'Invalid Credentials',
            text: response.message || 'Please check your username and password.',
            icon: 'error',
          });
        
          return;
        }
  
        if (response.code === 'AuthenticationFailed') {
          Swal.fire({
            title: 'Authentication Failed',
            text: response.message || 'An error occurred while logging in.',
            icon: 'error',
          });
         
          return;
        }
  
        // Success case: JWT and user data received
        this.userAuthService.setRoles(response.user.role);
        this.userAuthService.setToken(response.jwtToken);
        this.userAuthService.setUser(response.user);
  
        Swal.fire({
          title: 'Login Successful!',
          text: 'Welcome back!',
          icon: 'success',
          timer: 1500,
          showConfirmButton: false
        });
  
        const role = response.user.role[0].roleName;
        if (role === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else {
          this.router.navigate(['/home']);
        }
      },
      (error) => {
        console.error('Login error:', error);
        let errorMessage = error.error?.message || 'Login failed. Please try again.';
  
        Swal.fire({
          title: 'Error',
          text: errorMessage,
          icon: 'error',
        });
  
        
      }
    );
  }
  
}
