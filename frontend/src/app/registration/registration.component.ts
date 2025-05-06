
import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';

import { UserService } from '../_service/user.service';
import { UserAuthServiceService } from '../_service/user-auth-service.service';
import Swal from 'sweetalert2';
import { UserDetailsProxy } from '../modul/user-details-proxy';

@Component({
  selector: 'app-registration',
  standalone: false,
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css'],
})
export class RegistrationComponent {
  userData: UserDetailsProxy = new UserDetailsProxy();
  selectedFile: File | null = null;
  isLoading = false;
  
  constructor(
    private userService: UserService,
    private router: Router,
    private userAuthServiceService: UserAuthServiceService
  ) {}

  onSubmit(form: NgForm) {
    if (form.invalid) {
      Object.keys(form.controls).forEach((field) => {
        const control = form.controls[field];
        control.markAsTouched({ onlySelf: true });
      });
      return;
    }

    if (form.valid) {
      this.userData.name = form.value.name;
      this.userData.dob = form.value.dob;
      this.userData.userName = form.value.username;
      this.userData.password = form.value.password;
      this.userData.email = form.value.email;
      this.userData.gender = form.value.gender;
      this.userData.address = form.value.address;
      this.userData.contactNumber = form.value.contactNumber;
      this.userData.pinCode = form.value.pinCode;

      this.isLoading = true;

      const formData = new FormData();
      formData.append('userData', JSON.stringify(this.userData));

      if (this.selectedFile) {
        formData.append('file', this.selectedFile, this.selectedFile.name);
      }

      this.userService.registerWithImage(formData).subscribe(
        (response: any) => {
          this.isLoading = false;
          
          if (response.code === 'UserNameExist') {
            Swal.fire({
              title: 'Username Already In Use',
              text: response.message || 'The username you entered is already registered. Please use a different username or log in to your account.',
              icon: 'warning',
            });
            return;
          } else if (response.code === 'EmailExist') {
            Swal.fire({
              title: 'Email Already In Use',
              text: response.message || 'The email address you entered is already registered. Please use a different email or log in to your account.',
              icon: 'warning',
            });
            return;
          } else if (response.code === 'InvalidGender') {
            Swal.fire({
              title: 'Invalid Gender',
              text: response.message || 'Please select a valid gender.',
              icon: 'warning',
            });
            return;
          } else if (response.status >= 400) {
            console.log("response.status--"+response.status)
            Swal.fire({
              title: 'Error',
              text: response.message || 'Registration failed. Please check your form and try again.',
              icon: 'error',
            });
            return;
          }
          
          // Success case
          Swal.fire({
            title: 'Registration Successful!',
            text: 'Your account has been created successfully. Please log in.',
            icon: 'success',
          }).then(() => {
            this.router.navigate(['/login']);
          });
        },
        (error) => {
          this.isLoading = false;
          console.error('Registration failed', error);
          
          let errorMessage = 'Registration failed. Please try again.';
          if (error.error && error.error.message) {
            errorMessage = error.error.message;
          }
          
          Swal.fire({
            title: 'Error',
            text: errorMessage,
            icon: 'error',
          });
        }
      );
    }
  }
  
  onFileSelected(event: any): void {
    this.selectedFile = event.target.files[0] ?? null;
  }
  
  islogin(): boolean {
    return this.userAuthServiceService.isLoggedIn();
  }
}