import { Component, Inject, OnInit } from '@angular/core';
import { UserService } from '../_service/user.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { UserDetailsProxy } from '../modul/user-details-proxy';

@Component({
  selector: 'app-view',
  standalone: false,
  templateUrl: './view.component.html',
  styleUrl: './view.component.css'
})
export class ViewComponent implements OnInit {
  userName: string;
  userData: UserDetailsProxy = new UserDetailsProxy();
  loading = true;
  error = false;

  constructor(
    public dialogRef: MatDialogRef<ViewComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private userservice: UserService
  ) {
    this.userName = data.userName;
  }

  ngOnInit(): void {
    this.fetchUserData();
  }

  fetchUserData(): void {
    this.userservice.getUserByName(this.userName).subscribe(
      (response: any) => {
        this.userData = response;
        this.loading = false;
      },
      (error) => {
        console.error('Error fetching user data:', error);
        this.loading = false;
        this.error = true;
      }
    );
  }

  onClose(): void {
    this.dialogRef.close();
  }

  getImageUrl(): string {
    if (! this.userData.imageUuid) return '';
    console.log(this.userservice.getImageUrl( this.userData.imageUuid));
    return this.userservice.getImageUrl(this.userData.imageUuid);
  }
}