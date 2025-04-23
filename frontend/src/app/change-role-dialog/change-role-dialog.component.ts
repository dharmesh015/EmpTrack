import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { UserService } from '../_service/user.service';
import Swal from 'sweetalert2';
@Component({
  selector: 'app-change-role-dialog',
  standalone: false,
  templateUrl: './change-role-dialog.component.html',
  styleUrl: './change-role-dialog.component.css'
})export class ChangeRoleDialogComponent {
  roles = ['User ', 'Seller', 'Admin']; // Example roles
  selectedRole: any;
  isLoading = false; // Add this loading state variable

  constructor(
    public dialogRef: MatDialogRef<ChangeRoleDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private userService: UserService
  ) {
    this.selectedRole = data.currentRole; // Set the current role
  }

  changeRole(): void {
    console.log("change role");
    this.isLoading = true; // Show spinner before API call
    
    this.userService.updateUserRole(this.data.userName, this.selectedRole).subscribe(
      () => {
        this.isLoading = false; // Hide spinner on success
        Swal.fire('Success', 'User role updated successfully', 'success');
        this.dialogRef.close(true);
      },
      (error:any) => {
        this.isLoading = false; // Hide spinner on error
        Swal.fire('Error', 'Failed to update user role', 'error');
        console.error('Error updating user role', error);
      }
    );
  }
}