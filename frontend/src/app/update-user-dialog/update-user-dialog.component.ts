import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../_service/user.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { UserAuthServiceService } from '../_service/user-auth-service.service';
import Swal from 'sweetalert2';
import { UserDetailsProxy } from '../modul/user-details-proxy';

@Component({
  selector: 'app-update-user-dialog',
  standalone:false,
  templateUrl: './update-user-dialog.component.html',
  styleUrl: './update-user-dialog.component.css',
})
export class UpdateUserDialogComponent implements OnInit {
  form!: FormGroup;
  userData: UserDetailsProxy = new UserDetailsProxy();
  loading = false;

  constructor(
    public dialogRef: MatDialogRef<UpdateUserDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: FormBuilder,
    private userservice: UserService,
    private userAuth: UserAuthServiceService
  ) {
    // Initialize the form
    this.initForm();
    
    // Set the user data if already provided
    if (this.data) {
      this.userData = this.data;
      this.patchFormValues();
    }
  }

  initForm(): void {
    this.form = this.fb.group({
      name: ['', Validators.required],
      dob: ['', Validators.required],
      userName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      gender: ['', Validators.required],
      address: ['', Validators.required],
      contactNumber: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      pinCode: ['', [Validators.required, Validators.pattern('^[0-9]{6}$')]],
    });
  }

  patchFormValues(): void {
    if (this.userData) {
      this.form.patchValue({
        name: this.userData.name || '',
        dob: this.userData.dob || '',
        userName: this.userData.userName || '',
        email: this.userData.email || '',
        gender: this.userData.gender || '',
        address: this.userData.address || '',
        contactNumber: this.userData.contactNumber || '',
        pinCode: this.userData.pinCode || '',
      });
    }
  }

  ngOnInit(): void {
    // If we need to fetch user data (in case it wasn't passed properly)
    if (!this.userData.userName && this.data?.userName) {
      this.loading = true;
      this.userservice.getUser(this.data.userName).subscribe({
        next: (response: any) => {
          console.log('Update user data received:', response);
          this.userData = response;
          this.patchFormValues();
          this.loading = false;
        },
        error: (error) => {
          console.error('Error fetching user data for update:', error);
          this.loading = false;
          Swal.fire('Error', 'Failed to load user data. Please try again.', 'error');
        }
      });
    }
  }

  onSubmit(): void {
    if (this.form.valid) {
      this.loading = true;
      console.log('Submitting form:', this.form.value);

      // Update userData with form values
      this.userData.name = this.form.value.name;
      this.userData.dob = this.form.value.dob;
      this.userData.userName = this.form.value.userName;
      this.userData.email = this.form.value.email;
      this.userData.gender = this.form.value.gender;
      this.userData.address = this.form.value.address;
      this.userData.contactNumber = this.form.value.contactNumber;
      this.userData.pinCode = this.form.value.pinCode;

      this.userservice.updateUser(this.userData).subscribe({
        next: (response) => {
          this.loading = false;
          Swal.fire('Success', 'User updated successfully', 'success');
          this.dialogRef.close(true);
        },
        error: (error) => {
          this.loading = false;
          console.error('Error updating user:', error);
          Swal.fire('Error', 'Failed to update user. Please try again.', 'error');
        }
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}