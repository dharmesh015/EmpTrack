import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../_service/user.service';
import Swal from 'sweetalert2';
import { UserDetailsProxy } from '../modul/user-details-proxy';

@Component({
  selector: 'app-add-user-dialog',
  standalone: false,
  templateUrl: './add-user-dialog.component.html',
  styleUrl: './add-user-dialog.component.css',
})
export class AddUserDialogComponent {
  form!: FormGroup;
  userData: UserDetailsProxy = new UserDetailsProxy();
  
  constructor(
    public dialogRef: MatDialogRef<AddUserDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: FormBuilder,
    private userservice: UserService
  ) {
    this.form = this.fb.group({
      userName: ['', Validators.required],
      password: [
        '',
        [
          Validators.required,
          Validators.minLength(7),
          Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z]).*$/),
        ],
      ],
      name: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      contactNumber: ['', [
        Validators.required, 
        Validators.pattern(/^\d{10}$/)
      ]],
      dob: ['', Validators.required],
      gender: ['',Validators.required],
      address: ['',Validators.required],
      pinCode: ['',[Validators.required, Validators.minLength(6),Validators.maxLength(6)]],
    });
  }

  onSubmit(): void {
    if (this.form.valid) {
      // Map form values to userData properties correctly
      this.userData.userName = this.form.value.userName;
      this.userData.password = this.form.value.password;
      this.userData.name = this.form.value.name;
      this.userData.email = this.form.value.email;
      this.userData.contactNumber = this.form.value.contactNumber;
      this.userData.dob = this.form.value.dob;
      this.userData.gender = this.form.value.gender;
      this.userData.address = this.form.value.address;
      this.userData.pinCode = this.form.value.pinCode;
      
      this.userservice.register(this.userData).subscribe((response) => {
        if (response === 'UserNameExist') {
          console.log('Username already exists!', response);
          Swal.fire({
            title: 'Username Already In Use',
            text: 'The username you entered is already registered. Please use a different username or log in to your account.',
            icon: 'warning',
          });
        } else if (response === 'EmailExist') {
          console.log('Email already exists!', response);
          Swal.fire({
            title: 'Email Already In Use',
            text: 'The email address you entered is already registered. Please use a different email or log in to your account.',
            icon: 'warning',
          });
        } else {
          this.dialogRef.close(this.form.value);
        }
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}