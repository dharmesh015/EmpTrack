import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';
import { MatDialog } from '@angular/material/dialog';
import { UpdateUserDialogComponent } from '../update-user-dialog/update-user-dialog.component';
import { AddUserDialogComponent } from '../add-user-dialog/add-user-dialog.component';
import { ViewComponent } from '../view/view.component';
import { UserDetailsProxy } from '../modul/user-details-proxy';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AddUserComponent } from '../add-user/add-user.component';
import { UserService } from '../_service/user.service';
// import * as XLSX from 'xlsx';

@Component({
  selector: 'app-admin',
  standalone: false,
  templateUrl: './admin.component.html',
  styleUrls: ['./admin.component.css'],
})
export class AdminComponent implements OnInit {
  filteredUsers: UserDetailsProxy[] = [];
  size: number = 10;
  page: number = 0;
  totalUsers: number = 0;
  hasMoreUsers: boolean = true;
  searchTerm: string = '';
  isGlobalSearch: boolean = false;
  
  // Sorting properties
  sortBy: string = 'name';
  sortDirection: string = 'asc';
  
  totalPages: number = 0;
  paginationArray: number[] = [];

  user: UserDetailsProxy[] = [];
  loading = true;
  error = '';

  // Add role filter property
  selectedRole: string = 'USER'; // Default role filter
  roles: string[] = ['USER', 'ADMIN']; // Available roles
  
  // Flag to show inactive users (default: false - show only active)
  showInactive: boolean = false;

  constructor(
    private router: Router,
    private userservice: UserService,
    private dialog: MatDialog,
    private http: HttpClient,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }
  
  // Get management title based on selected role
  getManagementTitle(): string {
    return this.selectedRole === 'ADMIN' ? 'Admin Management' : 'User Management';
  }

  imageUrls: Map<string, string> = new Map<string, string>();

  getImageUrl(imageUuid: string): string {
    if (!imageUuid) return '';
    return this.userservice.getImageUrl(imageUuid);
  }

  loadUsers(): void {
    this.loading = true;
    
    if (this.isGlobalSearch && this.searchTerm && this.searchTerm.trim() !== '') {
      this.performGlobalSearch();
    } else {
      this.isGlobalSearch = false;
      // Update to include role filter and active status filter
      this.userservice.getUsersByRole(
        this.selectedRole, 
        this.page, 
        this.size, 
        this.sortBy, 
        this.sortDirection,
        
      ).subscribe({
        next: (data: any) => {
          this.handleUserDataResponse(data);
        },
        error: (error: any) => {
          this.handleError(error, 'Error fetching users');
        }
      });
    }
  }

  // Change role filter
  changeRole(role: string): void {
    this.selectedRole = role;
    this.page = 0; // Reset to first page
    this.loadUsers();
  }
  
  // Toggle showing inactive users
  toggleInactiveUsers(): void {
    this.showInactive = !this.showInactive;
    this.page = 0; // Reset to first page
    this.loadUsers();
  }

  performGlobalSearch(): void {
    this.loading = true;
    
    // Update search to include role filter and active status
    this.userservice.searchUsersByRole(
      this.selectedRole, 
      this.searchTerm, 
      this.page, 
      this.size, 
      this.sortBy, 
      this.sortDirection,
      !this.showInactive // Pass the opposite of showInactive to get only active users by default
    ).subscribe({
      next: (data: any) => {
        this.handleUserDataResponse(data);
      },
      error: (error: any) => {
        this.handleError(error, 'Error searching users');
      }
    });
  }

  globalSearch(): void {
    if (this.searchTerm && this.searchTerm.trim() !== '') {
      this.page = 0; 
      this.isGlobalSearch = true;
      this.performGlobalSearch();
    } else {
      this.isGlobalSearch = false;
      this.loadUsers();
    }
  }
  
  resetSearch(): void {
    // Clear the search text
    this.searchTerm = '';
    // Reset to first page
    this.page = 0; 
    // Reset global search flag
    this.isGlobalSearch = false;
    // Reload users
    this.loadUsers();
  }
  
  // Sort handling
  sort(column: string): void {
    if (this.sortBy === column) {
      // Toggle sort direction if clicking on same column
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      // Default to ascending for new column
      this.sortBy = column;
      this.sortDirection = 'asc';
    }
    
    // Reset to first page when sorting changes
    this.page = 0;
    this.loadUsers();
  }
  
  // Get sort arrow indicator
  getSortIndicator(column: string): string {
    if (this.sortBy !== column) return '';
    return this.sortDirection === 'asc' ? '↑' : '↓';
  }

  // Handle user data response
  handleUserDataResponse(data: any): void {
    this.loading = false;
    this.filteredUsers = data.content;
    this.totalUsers = data.totalElements;
    this.totalPages = data.totalPages;
    this.updatePagination();
  }

  // Handle error responses
  handleError(error: any, message: string): void {
    this.loading = false;
    console.error(error);
    this.error = message;
    this.snackBar.open(`${message}: ${error.message || 'Unknown error'}`, 'Close', {
      duration: 5000,
      panelClass: ['error-snackbar']
    });
  }

  // Update pagination array for UI
  updatePagination(): void {
    this.paginationArray = [];
    const maxVisible = 5; // Maximum number of page buttons to show
    
    if (this.totalPages <= maxVisible) {
      // Show all pages if total is less than or equal to maxVisible
      for (let i = 0; i < this.totalPages; i++) {
        this.paginationArray.push(i);
      }
    } else {
      // Always include first page
      this.paginationArray.push(0);
      
      // Current page neighborhood
      const startNeighborhood = Math.max(1, this.page - 1);
      const endNeighborhood = Math.min(this.totalPages - 2, this.page + 1);
      
      // Add ellipsis if needed before neighborhood
      if (startNeighborhood > 1) {
        this.paginationArray.push(-1); // -1 represents ellipsis
      }
      
      // Add neighborhood pages
      for (let i = startNeighborhood; i <= endNeighborhood; i++) {
        this.paginationArray.push(i);
      }
      
      // Add ellipsis if needed after neighborhood
      if (endNeighborhood < this.totalPages - 2) {
        this.paginationArray.push(-1); // -1 represents ellipsis
      }
      
      // Always include last page
      this.paginationArray.push(this.totalPages - 1);
    }
  }

  // Navigation methods
  previousPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadUsers();
    }
  }

  nextPage(): void {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadUsers();
    }
  }

  goToPage(pageNum: number): void {
    this.page = pageNum;
    this.loadUsers();
  }

  // View user details
  view(userName: string): void {
    this.loading = true;
    this.userservice.getUser(userName).subscribe({
      next: (response: any) => {
        this.loading = false;
        console.log('View user response:', response);
        
        // Open dialog with the user data
        const dialogRef = this.dialog.open(ViewComponent, {
          width: '600px',
          data: response
        });
      },
      error: (error) => {
        this.loading = false;
        this.handleError(error, 'Error fetching user details');
      }
    });
  }

  // Edit user
  editUser(userName: string): void {
    this.loading = true;
    this.userservice.getUser(userName).subscribe({
      next: (response: any) => {
        this.loading = false;
        console.log('Edit user response:', response);
        
        // Open dialog with the user data
        const dialogRef = this.dialog.open(UpdateUserDialogComponent, {
          width: '600px',
          data: response
        });
  
        dialogRef.afterClosed().subscribe(result => {
          if (result) {
            // Reload users if the update was successful
            this.loadUsers();
          }
        });
      },
      error: (error) => {
        this.loading = false;
        this.handleError(error, 'Error fetching user details for editing');
      }
    });
  }

  
  
  // Reactivate user
  

  // Delete user (hard delete - keeping this for admin with warning)
  deleteUser(userName: string, role: string): void {
    Swal.fire({
      title: 'Permanent Deletion Warning',
      text: `You are about to PERMANENTLY delete user "${userName}". This action CANNOT be undone! Consider deactivating instead.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Delete Permanently',
      cancelButtonText: 'Cancel'
    }).then((result) => {
      if (result.isConfirmed) {
        this.userservice.deleteUser(userName).subscribe({
          next: (response: any) => {
            Swal.fire('Deleted!', 'User has been permanently deleted.', 'success');
            this.loadUsers();
          },
          error: (error) => {
            Swal.fire('Error!', `Failed to delete user: ${error.message || 'Unknown error'}`, 'error');
          }
        });
      }
    });
  }

  // Add new user
  addNewUser(): void {
    const dialogRef = this.dialog.open(AddUserDialogComponent, {
      width: '600px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadUsers();
      }
    });
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
      '#1abc9c', '#2ecc71', '#9b59b6',
      '#16a085', '#27ae60', '#2980b9', '#8e44ad', '#2c3e50',
      '#f1c40f', '#e67e22', '#e74c3c', '#ecf0f1', '#95a5a6'
    ];
    
    let hash = 0;
    for (let i = 0; i < name?.length || 0; i++) {
      hash = name.charCodeAt(i) + ((hash << 5) - hash);
    }
    
    return colors[Math.abs(hash) % colors.length];
  }
  
  getSerialNumber(index: number): number {
    return this.page * this.size + index + 1;
  }

  formatDate(dateString: string | undefined): string {
    if (!dateString) return 'N/A';
    
    try {
      const date = new Date(dateString);
      
      if (isNaN(date.getTime())) {
        return 'Invalid Date';
      }
      
      const day = String(date.getDate()).padStart(2, '0');
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const year = date.getFullYear();
      
      return `${day}/${month}/${year}`;
    } catch (error) {
      console.error('Error formatting date:', error);
      return 'Error';
    }
  }

  downloadExcel(): void {
    this.http.get('http://localhost:9099/download', { 
      responseType: 'blob' 
    }).subscribe({
      next: (data: Blob) => {
        const blob = new Blob([data], { 
          type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' 
        });
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'users_data.xlsx';
        link.click();
        window.URL.revokeObjectURL(url);
        Swal.fire({
          title: 'Success',
          text: 'Excel file downloaded successfully',
          icon: 'success'
        });
      },
      error: (error) => {
        console.error('Error downloading Excel file', error);
        this.snackBar.open('Failed to download Excel file', 'Close', { duration: 3000 });
      }
    });
  }
  addUser(): void {
    const dialogRef = this.dialog.open(AddUserComponent, {
      width: '2000px',
      disableClose: true
    });
  
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        // Refresh the user list if a user was added
        this.loadUsers();
      }
    });
  }
}