import { Component, OnInit, OnDestroy } from '@angular/core';
import { NgForm } from '@angular/forms';
import { UserService } from '../_service/user.service';
import Swal from 'sweetalert2';
import { ActivatedRoute, Router } from '@angular/router';
import { UserAuthServiceService } from '../_service/user-auth-service.service';
import { ApiResponse } from '../modul/api-response';


@Component({
  selector: 'app-forgot-password',
  standalone: false,
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css',
})
export class ForgotPasswordComponent  {
  isLoading = false;
  emailSent = false;
  captchaUrl: string = '';
  
  constructor(
    private userService: UserService,
    private router: Router,
    private userAuthService: UserAuthServiceService
  ) {}
  
 
  
  sendEmail(form: NgForm) {
    if (form.invalid) {
      Object.keys(form.controls).forEach((field) => {
        const control = form.controls[field];
        control.markAsTouched({ onlySelf: true });
      });
      
      Swal.fire({
        title: 'Invalid Input',
        text: 'Please enter a valid email address.',
        icon: 'warning',
        confirmButtonText: 'OK',
      });
      
      return;
    }
    
    this.isLoading = true;
    
    this.userService.sendEmail(form.value.email).subscribe(
      (response: ApiResponse) => {
        this.isLoading = false;
        
        if (response.code === 'Success') {
          this.emailSent = true;
          form.resetForm();
          
          Swal.fire({
            title: 'Email Sent',
            text: 'A password reset link has been sent to your email address.',
            icon: 'success',
            confirmButtonText: 'OK',
          });
        } else if (response.code === 'UserNotFound') {
          Swal.fire({
            title: 'User Not Found',
            text: response.message || 'The email address you entered is not associated with any account. Please try again.',
            icon: 'error',
            confirmButtonText: 'OK',
          });
        } else if (response.code === 'InvalidCAPTCHA') {
          Swal.fire({
            title: 'Invalid CAPTCHA',
            text: response.message || 'CAPTCHA verification failed. Please try again.',
            icon: 'error',
            confirmButtonText: 'OK',
          });
         
        } else {
          Swal.fire({
            title: 'Error',
            text: response.message || 'An error occurred while processing your request.',
            icon: 'error',
            confirmButtonText: 'OK',
          });
        }
      },
      (error) => {
        this.isLoading = false;
        
        let errorMessage = 'An error occurred while processing your request. Please try again later.';
        if (error.error && error.error.message) {
          errorMessage = error.error.message;
        }
        
        Swal.fire({
          title: 'Error',
          text: errorMessage,
          icon: 'error',
          confirmButtonText: 'OK',
        });
        
       
      }
    );
  }
  
  
  
 
}