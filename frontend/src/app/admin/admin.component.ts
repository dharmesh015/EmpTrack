

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../_service/user.service';
import Swal from 'sweetalert2';
import { MatDialog } from '@angular/material/dialog';
import { UpdateUserDialogComponent } from '../update-user-dialog/update-user-dialog.component';
import { AddUserDialogComponent } from '../add-user-dialog/add-user-dialog.component';
import { ViewComponent } from '../view/view.component';
import { UserDetailsProxy } from '../modul/user-details-proxy';

@Component({
  selector: 'app-admin',
  standalone: false,
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css',
})
export class AdminComponent implements OnInit {
  filteredUsers: UserDetailsProxy[] = [];
  size: number = 10;
  page: number = 0;
  totalUsers: number = 0;
  hasMoreUsers: boolean = true;
  searchTerm: string = '';
  isGlobalSearch: boolean = false;
  
  // New properties for enhanced pagination
  totalPages: number = 0;
  paginationArray: number[] = [];

  user: UserDetailsProxy[] = [];
  loading = true;
  error = '';

  constructor(
    private router: Router,
    private userservice: UserService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }
  
  imageUrls: Map<string, string> = new Map<string, string>();

  getImageUrl(imageUuid: string): string {
    if (!imageUuid) return '';
    console.log(this.userservice.getImageUrl(imageUuid));
    return this.userservice.getImageUrl(imageUuid);
  }

  loadUsers(): void {
    this.loading = true;
    
    if (this.isGlobalSearch && this.searchTerm && this.searchTerm.trim() !== '') {
      this.performGlobalSearch();
    } else {
      this.isGlobalSearch = false;
      this.userservice.getAllUsersPageWise(this.page, this.size).subscribe(
        (data: any) => {
          this.handleUserDataResponse(data);
        },
        (error: any) => {
          this.handleError(error, 'Error fetching users');
        }
      );
    }
  }

  performGlobalSearch(): void {
    this.loading = true;
    
    this.userservice.searchUsers(this.searchTerm, this.page, this.size).subscribe(
      (data: any) => {
        this.handleUserDataResponse(data);
      },
      (error: any) => {
        this.handleError(error, 'Error searching users');
      }
    );
  }

  globalSearch(): void {
    if (this.searchTerm && this.searchTerm.trim() !== '') {
      this.page = 0; // Reset to first page
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
 
    // Reload users
    this.loadUsers();
  }
  handleUserDataResponse(data: any): void {
    this.user = data.content.map((obj: any) => {
      const roleName = obj.role.length > 0 ? obj.role[0].roleName : 'USER'; 
      obj.accessRole = roleName;
      return obj; 
    });
    
    this.totalUsers = data.totalElements;
    this.totalPages = Math.ceil(this.totalUsers / this.size);
    this.updatePaginationArray();
    this.filteredUsers = this.user;
    this.hasMoreUsers = this.page < this.totalPages - 1;
    this.loading = false;
  }
  
  handleError(error: any, message: string): void {
    console.error(message, error);
    Swal.fire(
      'Error',
      `${message}. Please try again later.`,
      'error'
    );
    this.loading = false;
  }

  // New method to update pagination array
  updatePaginationArray(): void {
    this.paginationArray = [];
    
    // For small number of pages, show all
    if (this.totalPages <= 7) {
      for (let i = 0; i < this.totalPages; i++) {
        this.paginationArray.push(i);
      }
    } else {
      // For many pages, show first, last, and pages around current
      if (this.page < 3) {
        // Near start
        for (let i = 0; i < 5; i++) {
          this.paginationArray.push(i);
        }
        this.paginationArray.push(-1); // Ellipsis
        this.paginationArray.push(this.totalPages - 1);
      } else if (this.page > this.totalPages - 4) {
        // Near end
        this.paginationArray.push(0);
        this.paginationArray.push(-1); // Ellipsis
        for (let i = this.totalPages - 5; i < this.totalPages; i++) {
          this.paginationArray.push(i);
        }
      } else {
        // Middle
        this.paginationArray.push(0);
        this.paginationArray.push(-1); // Ellipsis
        for (let i = this.page - 1; i <= this.page + 1; i++) {
          this.paginationArray.push(i);
        }
        this.paginationArray.push(-1); // Ellipsis
        this.paginationArray.push(this.totalPages - 1);
      }
    }
  }

  goToPage(pageNum: number): void {
    if (pageNum >= 0 && pageNum < this.totalPages) {
      this.page = pageNum;
      this.loadUsers();
    }
  }

  filterUsers(): void {
    if (!this.isGlobalSearch && this.searchTerm) {
      this.filteredUsers = this.user.filter((user) =>
        (user.userName?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
         user.name?.toLowerCase().includes(this.searchTerm.toLowerCase()) || 
         user.email?.toLowerCase().includes(this.searchTerm.toLowerCase()))
      );
    } else {
      this.filteredUsers = this.user;
    }
  }

  editUser(username: string): void {
    const editdialog = this.dialog.open(UpdateUserDialogComponent, {
      data: { userName: username },
      width: '500px',
      disableClose: true,
    });

    editdialog.afterClosed().subscribe((result) => {
      if (result) {
        this.loadUsers();
      }
    });
  }

  view(username: string): void {
    const editdialog = this.dialog.open(ViewComponent, {
      data: { userName: username },
      width: '500px',
      disableClose: true,
    });

    editdialog.afterClosed().subscribe((result) => {
      if (result) {
        this.loadUsers();
      }
    });
  }

  deleteUser(name: string, role: string): void {
    if (role !== 'ADMIN') {
      Swal.fire({
        title: 'Are you sure?',
        text: "You won't be able to revert this!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'Yes, delete it!',
        cancelButtonText: 'No, cancel!',
      }).then((result) => {
        if (result.isConfirmed) {
          this.userservice.deleteUser(name).subscribe(
            () => {
              Swal.fire('Deleted!', 'User has been deleted.', 'success');
              this.loadUsers();
            },
            (error: any) => {
              Swal.fire(
                'Error!',
                'There was an error deleting the user.',
                'error'
              );
              console.error('Error deleting user', error);
            }
          );
        }
      });
    } else {
      Swal.fire({
        title: 'Warning',
        text: 'You cannot delete admin accounts!',
        icon: 'warning',
        confirmButtonText: 'OK',
      });
    }
  }

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
  
  openAddUserDialog(): void {
    const dialogRef = this.dialog.open(AddUserDialogComponent, {
      width: '300px',
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe((result) => {
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
}