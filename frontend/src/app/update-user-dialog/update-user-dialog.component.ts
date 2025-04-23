import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../_service/user.service';
import { Registrationuser } from '../modul/registrationuser';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { UserAuthServiceService } from '../_service/user-auth-service.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-update-user-dialog',
  standalone: false,
  templateUrl: './update-user-dialog.component.html',
  styleUrl: './update-user-dialog.component.css',
})
export class UpdateUserDialogComponent implements OnInit {
  form!: FormGroup;
  userName: string;
  userData: Registrationuser = new Registrationuser();

  constructor(
    public dialogRef: MatDialogRef<UpdateUserDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private fb: FormBuilder,
    private userservice: UserService,
    private userAuth: UserAuthServiceService
  ) {
    this.userName = data.userName;
    this.form = this.fb.group({
      name: ['', Validators.required],
      dob: ['', Validators.required],
      userName: [this.userName, Validators.required],
      email: ['', [Validators.required, Validators.email]],
      gender: ['', Validators.required],
      address: ['', Validators.required],
      contactNumber: ['', [Validators.required, Validators.pattern('^[0-9]{10}$')]],
      pinCode: ['', [Validators.required, Validators.pattern('^[0-9]{6}$')]],
    });
  }

  ngOnInit(): void {
    this.userservice.getUserByName(this.userName).subscribe(
      (response: any) => {
        this.userData = response;
        
        this.form.patchValue({
          name: this.userData.name,
          dob: this.userData.dob,
          userName: this.userData.userName,
          email: this.userData.email,
          gender: this.userData.gender,
          address: this.userData.address,
          contactNumber: this.userData.contactNumber,
          pinCode: this.userData.pinCode,
       
        });
      },
      (error) => {
        console.error('Error fetching user data:', error);
      }
    );
  }

  onSubmit(): void {
    if (this.form.valid) {
      console.log('onsubmit----', this.form.value);

      this.userData.name = this.form.value.name;
      this.userData.dob = this.form.value.dob;
      this.userData.userName = this.form.value.userName;
      this.userData.email = this.form.value.email;
      this.userData.gender = this.form.value.gender;
      this.userData.address = this.form.value.address;
      this.userData.contactNumber = this.form.value.contactNumber;
      this.userData.pinCode = this.form.value.pinCode;

      this.userservice.update(this.userData).subscribe((rs) => {
        // this.popop();
        this.dialogRef.close(true);
      });
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }
  popop() {
    
  }

}