

import { Component, OnInit } from '@angular/core';
import { UserService } from '../_service/user.service';
import { UserAuthServiceService } from '../_service/user-auth-service.service';
import { UserDetailsProxy } from '../modul/user-details-proxy';
import { MatDialog } from '@angular/material/dialog';
import { UpdateUserDialogComponent } from '../update-user-dialog/update-user-dialog.component';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-admin-profile',
  standalone: false,
  templateUrl: './admin-profile.component.html',
  styleUrl: './admin-profile.component.css'
})
export class AdminProfileComponent implements OnInit {
  user: UserDetailsProxy = new UserDetailsProxy();
  loading = true;
  error = false;
  errorMessage = '';

  constructor(
    private userService: UserService,
    private authService: UserAuthServiceService,
    private dialog: MatDialog
  ) { }

  ngOnInit(): void {
    const userName = this.authService.getUser()?.userName;
    if (userName) {
      console.log('Loading profile for:', userName);
      this.loadUserProfile(userName);
    } else {
      this.error = true;
      this.errorMessage = 'User not authenticated or username not available';
      this.loading = false;
      console.error('User authentication issue - no username available');
    }
  }

  loadUserProfile(userName: string): void {
    this.loading = true;
    this.error = false;

    this.userService.getUser(userName)
      .subscribe({
        next: (userData) => {
          if (userData) {
            this.user = userData;
            console.log('Profile loaded successfully', this.user);
          } else {
            this.error = true;
            this.errorMessage = 'User profile data is empty';
            console.error('User profile data is empty');
          }
          this.loading = false;
        },
        error: (err) => {
          console.error('Error fetching user profile:', err);
          this.error = true;
          this.errorMessage = `Failed to load profile: ${err.message || 'Unknown error'}`;
          this.loading = false;
          
          // Show error message to user
          Swal.fire({
            title: 'Error!',
            text: 'Failed to load profile data. Please try again later.',
            icon: 'error',
            confirmButtonText: 'OK'
          });
        }
      });
  }

  openUpdateDialog(): void {
    const dialogRef = this.dialog.open(UpdateUserDialogComponent, {
      width: '500px',
      data: this.user // Pass the full user object
    });
  
    dialogRef.afterClosed().subscribe(result => {
      // Only show success message and refresh data if result is true
      // This ensures cancel clicks don't trigger the success flow
      if (result === true) {
        // Refresh profile data after update
        this.loadUserProfile(this.user.userName);
        
        // Show success message
        Swal.fire({
          title: 'Success!',
          text: 'Profile updated successfully',
          icon: 'success',
          confirmButtonText: 'OK'
        });
      }
    });
  }
  
  changePassword(): void {
    Swal.fire({
      title: 'Change Password',
      html: `
        <div class="form-group">
          <label for="current-password">Current Password</label>
          <input type="password" id="current-password" class="swal2-input" placeholder="Enter current password">
        </div>
        <div class="form-group mt-3">
          <label for="new-password">New Password</label>
          <input type="password" id="new-password" class="swal2-input" placeholder="Enter new password">
        </div>
        <div class="form-group mt-3">
          <label for="confirm-password">Confirm New Password</label>
          <input type="password" id="confirm-password" class="swal2-input" placeholder="Confirm new password">
        </div>
      `,
      focusConfirm: false,
      showCancelButton: true,
      confirmButtonText: 'Change Password',
      preConfirm: () => {
        const currentPassword = (document.getElementById('current-password') as HTMLInputElement).value;
        const newPassword = (document.getElementById('new-password') as HTMLInputElement).value;
        const confirmPassword = (document.getElementById('confirm-password') as HTMLInputElement).value;
        
        if (!currentPassword || !newPassword || !confirmPassword) {
          Swal.showValidationMessage('Please fill all fields');
          return false;
        }
        
        if (newPassword !== confirmPassword) {
          Swal.showValidationMessage('New password and confirmation do not match');
          return false;
        }
        
        if (newPassword.length < 8) {
          Swal.showValidationMessage('Password must be at least 8 characters long');
          return false;
        }
        
        return { currentPassword, newPassword };
      }
    }).then((result) => {
      if (result.isConfirmed && result.value) {
        // Here you would call the service to change the password
        // For now we'll just show a success message
        Swal.fire('Success!', 'Your password has been changed.', 'success');
      }
    });
  }

  imageUrls: Map<string, string> = new Map<string, string>();

  getImageUrl(imageUuid: string): string {
    if (!imageUuid) return '';
    return this.userService.getImageUrl(imageUuid);
  }

  getInitials(name: string): string {
    if (!name) return '?';
    
    const nameParts = name.split(' ');
    if (nameParts.length === 1) {
      return name.slice(0, 2).toUpperCase();
    }
    
    return (nameParts[0].charAt(0) + nameParts[nameParts.length - 1].charAt(0)).toUpperCase();
  }

  getProfileColor(name: string): string {
    const colors = [
      '#1abc9c', '#2ecc71',  '#9b59b6',
      '#16a085', '#27ae60', '#2980b9', '#8e44ad', '#2c3e50',
      '#f1c40f', '#e67e22', '#e74c3c', '#ecf0f1', '#95a5a6'
    ];
    
    let hash = 0;
    for (let i = 0; i < name?.length || 0; i++) {
      hash = name.charCodeAt(i) + ((hash << 5) - hash);
    }
    
    return colors[Math.abs(hash) % colors.length];
  }
  
  // Display role in a formatted way
  getRoleDisplay(): string {
    if (!this.user || !this.user.role) return 'User';
    
    if (Array.isArray(this.user.role)) {
      return this.user.role.map(r => r.roleName || r).join(', ');
    }
    
    if (typeof this.user.role === 'string') {
      return this.user.role;
    }
    
    return 'User';
  }
}