import { Component, Inject, OnInit } from '@angular/core';
import { UserService } from '../_service/user.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { UserDetailsProxy } from '../modul/user-details-proxy';

@Component({
  selector: 'app-view',
  standalone:false,
  templateUrl: './view.component.html',
  styleUrls: ['./view.component.css']
})
export class ViewComponent implements OnInit {
  userData: UserDetailsProxy = new UserDetailsProxy();
  loading = true;
  error = false;

  constructor(
    public dialogRef: MatDialogRef<ViewComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private userservice: UserService
  ) {
    // The data should already be passed from the admin component
    if (this.data) {
      this.userData = this.data;
      this.loading = false;
    }
  }

  ngOnInit(): void {
    // If data wasn't passed or we need to refetch
    if (!this.userData.userName && this.data.userName) {
      this.fetchUserData(this.data.userName);
    }
  }

  fetchUserData(userName: string): void {
    this.loading = true;
    this.userservice.getUser(userName).subscribe({
      next: (response: any) => {
        console.log('User data received:', response);
        this.userData = response;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error fetching user data:', error);
        this.loading = false;
        this.error = true;
      }
    });
  }

  onClose(): void {
    this.dialogRef.close();
  }

  getImageUrl(): string {
    if (!this.userData.imageUuid) return '';
    return this.userservice.getImageUrl(this.userData.imageUuid);
  }
}