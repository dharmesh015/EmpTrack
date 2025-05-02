import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { UserService } from '../_service/user.service';
import Swal from 'sweetalert2';
import { ActivatedRoute, Router } from '@angular/router';
import { UserAuthServiceService } from '../_service/user-auth-service.service';

@Component({
  selector: 'app-forgot-password',
  standalone: false,
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css',
})
export class ForgotPasswordComponent implements OnInit{
  isLoading = false;
  token: string = '';
  emailSent = false; // New flag to track when email is sent
  captchaUrl: string = '';
  
  constructor(
    private userService: UserService,
    private acrouter: ActivatedRoute,
    private router: Router,
        private userAuthService: UserAuthServiceService,
  ) {}
  
  ngOnInit(): void {
    // Only load CAPTCHA if the user is not logged in
    if (!this.userAuthService.isLoggedIn()) {
      this.loadCaptcha();
    }
  }
  sendEmail(form: NgForm) {
    if (form.valid) {
      this.isLoading = true;
      console.log(form.value.captcha)
      this.userService.sendEmail(form.value.email,form.value.captcha).subscribe(
        (response) => {
          this.isLoading = false;

          if (response === 'InvalidCAPTCHA') {
            Swal.fire({
              title: 'InvalidCAPTCHA',
              text: ' Please try again.',
              icon: 'error',
              confirmButtonText: 'OK',
            });
          }else if (response === 'UNF') {
            Swal.fire({
              title: 'User Not Found',
              text: 'The email address you entered is not associated with any account. Please try again.',
              icon: 'error',
              confirmButtonText: 'OK',
            });
          } else if (response === 'S') {
           
            this.emailSent = true;
            
            form.resetForm();
          } 
        },
        (error) => {
          this.isLoading = false;
          Swal.fire({
            title: 'Error',
            text: 'An error occurred while processing your request. Please try again later.',
            icon: 'error',
            confirmButtonText: 'OK',
          });
        }
      );
    } else {
      Swal.fire({
        title: 'Invalid Input',
        text: 'Please enter a valid email address.',
        icon: 'warning',
        confirmButtonText: 'OK',
      });
    }
  }
   loadCaptcha() {
      const timestamp = new Date().getTime();
      this.userService.getCaptchaImage(timestamp).subscribe(
        (response: Blob) => {
          // Revoke previous URL if exists
          if (this.captchaUrl) {
            URL.revokeObjectURL(this.captchaUrl);
          }
          this.captchaUrl = URL.createObjectURL(response);
        },
        (error) => {
          const errorMessage = error.message || error.error?.message || 'Error fetching CAPTCHA image.';
          Swal.fire({
            icon: 'error',
            title: 'Captcha not loading!',
            text: errorMessage,
            confirmButtonText: 'OK',
          });
        }
      );
    }
  
  
  
    ngOnDestroy() {
      if (this.captchaUrl) {
        URL.revokeObjectURL(this.captchaUrl);
      }
    }
    handleImageError() {
      console.error('CAPTCHA image failed to load');
      this.loadCaptcha(); // Try reloading on error
    }
  
    ReloadCaptcha() {
      this.loadCaptcha();
    }
}