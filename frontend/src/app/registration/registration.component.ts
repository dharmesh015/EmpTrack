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
  constructor(
    private userService: UserService,
    private router: Router,
    private userAuthServiceService: UserAuthServiceService
  ) {}

  isLoading = false;

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
        (response) => {
          if (response === 'UserNameExist') {
            console.log('Username already exists!', response);
            Swal.fire({
              title: 'Username Already In Use',
              text: 'The username you entered is already registered. Please use a different username or log in to your account.',
              icon: 'warning',
            });
            this.isLoading = false;
            return;
          } else if (response === 'EmailExist') {
            console.log('Email already exists!', response);
            Swal.fire({
              title: 'Email Already In Use',
              text: 'The email address you entered is already registered. Please use a different email or log in to your account.',
              icon: 'warning',
            });
            this.isLoading = false;
            return;
          }
          console.log(response);
          this.router.navigate(['/login']);
        },
        (error) => {
          console.log('Registration failed', error);
          Swal.fire({
            title: 'Error',
            text: 'Registration failed. Please try again.',
            icon: 'error',
          });
          this.isLoading = false;
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
