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

  constructor(
    private userService: UserService,
    private authService: UserAuthServiceService,
    private dialog: MatDialog
  ) { }

  ngOnInit(): void {
    const userName = this.authService.getUser().userName;
    console.log(userName);
    this.loadUserProfile(userName);
  }

  loadUserProfile(userName: string): void {
    this.loading = true;
    this.error = false;

    this.userService.getUserByName(userName)
      .subscribe({
        next: (userData) => {
          this.user = userData;
          this.loading = false;
        },
        error: (err) => {
          console.error('Error fetching user profile:', err);
          this.error = true;
          this.loading = false;
        }
      });
  }

  openUpdateDialog(): void {
    const dialogRef = this.dialog.open(UpdateUserDialogComponent, {
      width: '500px',
      data: { userName: this.user.userName }
    });
  
    dialogRef.afterClosed().subscribe(result => {
      // Only show success message and refresh data if result is true
      // This ensures cancel clicks don't trigger the success flow
      if (result === true) {
        // Refresh profile data after update
        this.loadUserProfile(this.user.userName);
        // // Show success message
        // Swal.fire({
        //   title: 'Success!',
        //   text: 'Profile updated successfully',
        //   icon: 'success',
        //   confirmButtonText: 'OK'
        // });
      }
    });
  }
  imageUrls: Map<string, string> = new Map<string, string>();

  getImageUrl(imageUuid: string): string {
    if (!imageUuid) return '';
    console.log(this.userService.getImageUrl(imageUuid));
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
}