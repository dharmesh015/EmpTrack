import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpEvent, HttpEventType } from '@angular/common/http';
import { finalize } from 'rxjs/operators';
import { ApiResponse } from '../modul/api-response';
import { UserService } from '../_service/user.service';

@Component({
  selector: 'app-add-user',
  standalone: false,
  templateUrl: './add-user.component.html',
  styleUrls: ['./add-user.component.css']
})
export class AddUserComponent implements OnInit {
  currentStep: 'email-verification' | 'user-option' | 'single-user' | 'multi-user' = 'email-verification';
  
  // Forms
  emailCheckForm: FormGroup;
  singleUserForm: FormGroup;
  
  // States
  isCheckingEmail = false;
  isEmailValid = false;
  emailError: string = '';
  isSubmittingUser = false;
  userSubmitError: string = '';
  isUploadingFile = false;
  uploadProgress = 0;
  uploadErrors: string[] = [];
  uploadSuccess = false;
  
  // Gender options
  genderOptions = [
    { value: "MALE", label: 'MALE' },
    { value: "FEMALE", label: 'FEMALE' },
    { value: "OTHER", label: 'OTHER' }
  ];
  
  // File upload
  selectedFile: File | null = null;
  
  constructor(
    private fb: FormBuilder,
    private userService: UserService
  ) {
    this.emailCheckForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]]
    });
    
    this.singleUserForm = this.fb.group({
      name: ['', Validators.required],
      dob: ['', Validators.required],
      userName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      gender: ['MALE', Validators.required],
      address: ['', Validators.required],
      contactNumber: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
      pinCode: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],
      role: ['USER', Validators.required],
      profileImage: [null]
    });
  }
  
  ngOnInit(): void {
  }
  
  // Email verification step
  checkEmail(): void {
    if (this.emailCheckForm.invalid) {
      Object.keys(this.emailCheckForm.controls).forEach(key => {
        const control = this.emailCheckForm.get(key);
        control?.markAsTouched();
      });
      return;
    }
    
    const email = this.emailCheckForm.get('email')?.value;
    this.isCheckingEmail = true;
    this.emailError = '';
    
    this.userService.checkEmail(email)
      .pipe(
        finalize(() => this.isCheckingEmail = false)
      )
      .subscribe({
        next: (response) => {
          if (response.code === 'NotAvailable') {
            this.isEmailValid = true;
            this.currentStep = 'user-option';
            this.singleUserForm.patchValue({ email: email });
          } else {
            this.isEmailValid = false;
            this.emailError = 'This email already exists in the system.';
          }
        },
        error: (error) => {
          console.error('Email check error:', error);
          this.emailError = error.message || 'Failed to check email. Please try again.';
        }
      });
  }
  
  // Choose option step actions
  goToSingleUserForm(): void {
    this.currentStep = 'single-user';
  }
  
  goToMultiUserForm(): void {
    this.currentStep = 'multi-user';
  }
  
  // Single user registration
  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.singleUserForm.patchValue({
        profileImage: file
      });
    }
  }
  
  submitSingleUser(): void {
    if (this.singleUserForm.invalid) {
      // Mark all fields as touched to trigger validation display
      Object.keys(this.singleUserForm.controls).forEach(key => {
        const control = this.singleUserForm.get(key);
        control?.markAsTouched();
      });
      return;
    }
    
    this.isSubmittingUser = true;
    this.userSubmitError = '';
    
    const userData = {
      name: this.singleUserForm.get('name')?.value,
      dob: this.formatDate(this.singleUserForm.get('dob')?.value),
      userName: this.singleUserForm.get('userName')?.value,
      email: this.singleUserForm.get('email')?.value,
      password: this.singleUserForm.get('password')?.value,
      gender: this.singleUserForm.get('gender')?.value, // no need to parseInt
      address: this.singleUserForm.get('address')?.value,
      contactNumber: this.singleUserForm.get('contactNumber')?.value,
      pinCode: this.singleUserForm.get('pinCode')?.value,
      // role: this.singleUserForm.get('role')?.value
    };
    
    console.log('Submitting user data:', userData); // Debug log
    
    const profileImage = this.singleUserForm.get('profileImage')?.value;
    
    this.userService.registerUser(userData, profileImage)
      .pipe(
        finalize(() => this.isSubmittingUser = false)
      )
      .subscribe({
        next: (response: any) => {
          // Assume success if status is 201 or code === 'register'
          if (response.status === 201 && response.code === 'register') {
            this.resetForms();
            this.currentStep = 'email-verification';
            alert(response.message || 'User registered successfully!');
          } else {
            // Fallback in case success status is wrapped differently
            alert('User registered successfully!');
            this.resetForms();
            this.currentStep = 'email-verification';
          }
        },
        
        error: (error) => {
          console.error('Registration error:', error); // Debug log
          
          if (error.code === 'UserNameExist') {
            // Specifically handle username already exists error
            this.userSubmitError = error.message || 'Username already exists. Please choose a different username.';
          } else if (error.errors && Array.isArray(error.errors)) {
            // Handle array of errors
            this.userSubmitError = this.formatErrors(error.errors);
          } else {
            // Handle generic error message
            this.userSubmitError = error.message || 'Failed to register user. Please try again.';
          }
        }
      });
  }
  
  // Multiple users upload
  onBulkFileSelected(event: any): void {
    this.selectedFile = event.target.files[0];
    this.uploadErrors = [];
    this.uploadSuccess = false;
  }
  
  uploadUsers(): void {
    if (!this.selectedFile) {
      alert('Please select a file first');
      return;
    }
  
    this.isUploadingFile = true;
    this.uploadProgress = 0;
    this.uploadErrors = [];
    this.uploadSuccess = false;
  
    this.userService.uploadUsers(this.selectedFile)
      .pipe(
        finalize(() => {
          this.isUploadingFile = false;
          this.clearFileInput(); // <-- Clear the input after upload finishes
        })
      )
      .subscribe({
        next: (response: any) => {
          if (response.status === 200) {
            this.uploadSuccess = true;
            this.selectedFile = null;
  
            if (response.errors && response.errors.length > 0) {
              this.uploadErrors = response.errors;
            } else {
              alert('Users imported successfully!');
            }
          }
        },
        error: (error) => {
          const apiError = error.error || error;
          if (apiError.errors && Array.isArray(apiError.errors)) {
            this.uploadErrors = apiError.errors;
          } else if (typeof apiError === 'string') {
            this.uploadErrors = [apiError];
          } else if (apiError.message) {
            this.uploadErrors = [apiError.message];
          } else {
            this.uploadErrors = ['An unknown error occurred during upload.'];
          }
          this.selectedFile = null;
        }
      });
  }
  clearFileInput(): void {
    const fileInput = document.getElementById('bulkFile') as HTMLInputElement;
    if (fileInput) {
      fileInput.value = ''; // Clear the file input
    }
    this.selectedFile = null;
  }
    
  
  downloadTemplate(): void {
    this.userService.downloadTemplate().subscribe(blob => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'user-upload-template.xlsx';
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
    });
  }
  
  // Helper methods
  resetForms(): void {
    this.emailCheckForm.reset();
    this.singleUserForm.reset({
      gender: 0,
      role: 'USER'
    });
    this.selectedFile = null;
    this.uploadErrors = [];
  }
  
  goBack(): void {
    if (this.currentStep === 'single-user' || this.currentStep === 'multi-user') {
      this.currentStep = 'user-option';
    } else if (this.currentStep === 'user-option') {
      this.currentStep = 'email-verification';
      this.isEmailValid = false;
    }
  }
  
  formatErrors(errors: string[] | string): string {
    if (Array.isArray(errors)) {
      return errors.join('<br>');
    }
    return errors.toString().replace(/\n/g, '<br>');
  }
  
  private formatDate(date: string): string {
    if (!date) return '';
    const d = new Date(date);
    return d.toISOString().split('T')[0]; // Returns YYYY-MM-DD
  }
}