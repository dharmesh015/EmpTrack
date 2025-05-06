import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../_service/user.service';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';
import { ApiResponse } from '../modul/api-response';


@Component({
  selector: 'app-reset-password',
  standalone: false,
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.css',
})
export class ResetPasswordComponent implements OnInit {
  resetForm: FormGroup;
  token: string = '';
  isLoading = false;
  tokenValidated = false;
  
  constructor(
    private fb: FormBuilder,
    private userService: UserService,
    private activatedRoute: ActivatedRoute,
    private router: Router
  ) {
    this.resetForm = this.fb.group(
      {
        password: [
          '',
          [
            Validators.required,
            Validators.minLength(7),
            Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z]).*$/),
          ],
        ],
        confirmPassword: ['', Validators.required],
      },
      { validators: this.passwordMatchValidator }
    );
  }
  
  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe((params) => {
      this.token = params['token'];
      if (!this.token) {
        this.handleInvalidToken('No reset token provided');
        return;
      }
      this.validateToken();
    });
  }
  
  validateToken() {
    this.isLoading = true;
    this.userService.validateResetToken(this.token).subscribe(
      (response: ApiResponse) => {
        this.isLoading = false;
        
        if (response.code === 'Valid') {
          this.tokenValidated = true;
        } else {
          this.handleInvalidToken(
            response.message || 'The password reset link is invalid or has expired'
          );
        }
      },
      (error) => {
        this.isLoading = false;
        
        let errorMessage = 'The password reset link is invalid or has expired';
        if (error.error && error.error.message) {
          errorMessage = error.error.message;
        }
        
        this.handleInvalidToken(errorMessage);
      }
    );
  }
  
  passwordMatchValidator(formGroup: FormGroup) {
    return formGroup.get('password')?.value ===
      formGroup.get('confirmPassword')?.value
      ? null
      : { mismatch: true };
  }
  
  onSubmit() {
    if (this.resetForm.invalid) {
      // Mark all fields as touched to trigger validation messages
      Object.keys(this.resetForm.controls).forEach(field => {
        const control = this.resetForm.get(field);
        control?.markAsTouched({ onlySelf: true });
      });
      return;
    }
    
    this.isLoading = true;
    
    this.userService.resetPassword(this.token, this.resetForm.get('password')?.value).subscribe(
      (response: ApiResponse) => {
        this.isLoading = false;
        
        if (response.code === 'Success') {
          Swal.fire({
            title: 'Success',
            text: 'Your password has been reset successfully.',
            icon: 'success',
            confirmButtonText: 'OK',
          }).then(() => {
            this.router.navigate(['/login']);
          });
        } else {
          Swal.fire({
            title: 'Error',
            text: response.message || 'Failed to reset password. Please try again.',
            icon: 'error',
            confirmButtonText: 'OK',
          });
        }
      },
      (error) => {
        this.isLoading = false;
        
        let errorMessage = 'Failed to reset password. Please try again.';
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
  
  private handleInvalidToken(message: string) {
    this.isLoading = false;
    Swal.fire({
      title: 'Invalid or Expired Link',
      text: message,
      icon: 'error',
      confirmButtonText: 'OK',
    }).then(() => {
      this.router.navigate(['/forgot-password']);
    });
  }
}