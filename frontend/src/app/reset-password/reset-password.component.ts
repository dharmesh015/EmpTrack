import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../_service/user.service';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';

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
    private userservice: UserService,
    private acroute: ActivatedRoute,
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
    this.acroute.queryParams.subscribe((params) => {
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

    this.userservice.validateResetToken(this.token).subscribe(
      (response) => {
        this.isLoading = false;
        this.tokenValidated = true;
      },
      (error) => {
        this.handleInvalidToken(
          'The password reset link is invalid or has expired'
        );
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
      return;
    }

    this.isLoading = true;
    this.userservice
      .resetPassword(this.token, this.resetForm.get('password')?.value)
      .subscribe(
        (response) => {
          this.isLoading = false;
          Swal.fire({
            title: 'Success',
            text: 'Your password has been reset successfully.',
            icon: 'success',
            confirmButtonText: 'OK',
          }).then(() => {
            this.router.navigate(['/login']);
          });
        },
        (error) => {
          this.isLoading = false;
          Swal.fire({
            title: 'Error',
            text: 'Failed to reset password. Please try again.',
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
