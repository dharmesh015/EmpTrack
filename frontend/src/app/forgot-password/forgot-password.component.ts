import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { UserService } from '../_service/user.service';
import Swal from 'sweetalert2';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-forgot-password',
  standalone: false,
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css',
})
export class ForgotPasswordComponent {
  isLoading = false;
  token: string = '';
  emailSent = false; // New flag to track when email is sent
  
  constructor(
    private userService: UserService,
    private acrouter: ActivatedRoute,
    private router: Router
  ) {}
  
  sendEmail(form: NgForm) {
    if (form.valid) {
      this.isLoading = true;
      this.userService.sendEmail(form.value.email).subscribe(
        (response) => {
          this.isLoading = false;
          console.log('Response received:', response);

          if (response === 'UNF') {
            Swal.fire({
              title: 'User Not Found',
              text: 'The email address you entered is not associated with any account. Please try again.',
              icon: 'error',
              confirmButtonText: 'OK',
            });
          } else if (response === 'S') {
           
            this.emailSent = true;
            
            form.resetForm();
          } else {
            Swal.fire({
              title: 'Error',
              text: 'Something went wrong. Please try again later.',
              icon: 'error',
              confirmButtonText: 'OK',
            });
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
}