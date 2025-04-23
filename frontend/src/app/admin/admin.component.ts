// import { Component, OnInit } from '@angular/core';
// import { Router } from '@angular/router';
// import { UserService } from '../_service/user.service';
// import { Registrationuser } from '../modul/registrationuser';
// import Swal from 'sweetalert2';

// import { MatDialog } from '@angular/material/dialog';
// import { UpdateUserDialogComponent } from '../update-user-dialog/update-user-dialog.component';
// import { AddUserDialogComponent } from '../add-user-dialog/add-user-dialog.component';
// import { userdata } from '../modul/userdata';

// @Component({
//   selector: 'app-admin',
//   standalone: false,
//   templateUrl: './admin.component.html',
//   styleUrl: './admin.component.css',
// })
// export class AdminComponent implements OnInit {
//   user: userdata[] = [];
//   filteredUsers: userdata[] = [];
//   size: number = 10;
//   page: number = 0;
//   totalUsers: number = 0;
//   hasMoreUsers: boolean = true;
//   searchTerm: string = '';

//   constructor(
//     private router: Router,
//     private userservice: UserService,
//     private dialog: MatDialog
//   ) {}

//   ngOnInit(): void {
//     this.loadUsers();
//   }

//   loadUsers(): void {
//     this.userservice.getAllUsersPageWise(this.page, this.size).subscribe(
//       (data: any) => {
//         // Map API response to Registrationuser objects if needed
//         this.user = data.content;
//         this.user = data.content.map((obj: any) => {
//           const roleName = obj.role.length > 0 ? obj.role[0].roleName : 'Guest'; // Default to 'Guest' if no role
//           obj.accessRole = roleName // Assign access role based on mapping
//           return obj; // Return the modified object
//         });
        
//         console.log( this.user);
//         this.totalUsers = data.totalElements;
//         this.filteredUsers = this.user;
//         this.hasMoreUsers = this.user.length === this.size;
//         console.log(this.user);
//       },
//       (error: any) => {
//         console.error('Error fetching users', error);
//         Swal.fire(
//           'Error',
//           'Failed to load users. Please try again later.',
//           'error'
//         );
//       }
//     );
//   }

//   filterUsers(): void {
//     if (this.searchTerm) {
//       this.filteredUsers = this.user.filter((user) =>
//         (user.userName?.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
//          user.name?.toLowerCase().includes(this.searchTerm.toLowerCase()) || 
//          user.email?.toLowerCase().includes(this.searchTerm.toLowerCase()))
//       );
//     } else {
//       this.filteredUsers = this.user;
//     }
//   }

//   editUser(username: string): void {
//     const editdialog = this.dialog.open(UpdateUserDialogComponent, {
//       data: { userName: username },
//       width: '500px',
//       disableClose: true,
//     });

//     editdialog.afterClosed().subscribe((result) => {
//       if (result) {
//         this.loadUsers();
//       }
//     });
//   }

//   deleteUser(name: string, role: string): void {
//     // Don't allow deletion of admin accounts
//     if (role !== 'ADMIN') {
//       Swal.fire({
//         title: 'Are you sure?',
//         text: "You won't be able to revert this!",
//         icon: 'warning',
//         showCancelButton: true,
//         confirmButtonText: 'Yes, delete it!',
//         cancelButtonText: 'No, cancel!',
//       }).then((result) => {
//         if (result.isConfirmed) {
//           this.userservice.deleteUser(name).subscribe(
//             () => {
//               Swal.fire('Deleted!', 'User has been deleted.', 'success');
//               this.loadUsers();
//             },
//             (error: any) => {
//               Swal.fire(
//                 'Error!',
//                 'There was an error deleting the user.',
//                 'error'
//               );
//               console.error('Error deleting user', error);
//             }
//           );
//         }
//       });
//     } else {
//       Swal.fire({
//         title: 'Warning',
//         text: 'You cannot delete admin accounts!',
//         icon: 'warning',
//         confirmButtonText: 'OK',
//       });
//     }
//   }

//   previousPage(): void {
//     if (this.page > 0) {
//       this.page--;
//       this.loadUsers();
//     }
//   }

//   nextPage(): void {
//     if (this.hasMoreUsers) {
//       this.page++;
//       this.loadUsers();
//     }
//   }
  
//   openAddUserDialog(): void {
//     const dialogRef = this.dialog.open(AddUserDialogComponent, {
//       width: '300px',
//       disableClose: true,
//     });

//     dialogRef.afterClosed().subscribe((result) => {
//       if (result) {
//         this.loadUsers();
//       }
//     });
//   }

//   getInitials(name: string): string {
//     if (!name) return '?';
    
//     const nameParts = name.split(' ');
//     if (nameParts.length === 1) {
//       return name.charAt(0).toUpperCase();
//     }
    
//     // Get first letter of first name and first letter of last name
//     return (nameParts[0].charAt(0) + nameParts[nameParts.length - 1].charAt(0)).toUpperCase();
//   }

//   getProfileColor(name: string) {
//     // Generate a consistent color based on the name
//     const colors = [
//       '#1abc9c', '#2ecc71', '#3498db', '#9b59b6', '#34495e',
//       '#16a085', '#27ae60', '#2980b9', '#8e44ad', '#2c3e50',
//       '#f1c40f', '#e67e22', '#e74c3c', '#ecf0f1', '#95a5a6'
//     ];
    
//     let hash = 0;
//     for (let i = 0; i < name?.length || 0; i++) {
//       hash = name.charCodeAt(i) + ((hash << 5) - hash);
//     }
// }}
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { UserService } from '../_service/user.service';
import { Registrationuser } from '../modul/registrationuser';
import Swal from 'sweetalert2';

import { MatDialog } from '@angular/material/dialog';
import { UpdateUserDialogComponent } from '../update-user-dialog/update-user-dialog.component';
import { AddUserDialogComponent } from '../add-user-dialog/add-user-dialog.component';
import { userdata } from '../modul/userdata';
import { ViewComponent } from '../view/view.component';

@Component({
  selector: 'app-admin',
  standalone: false,
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css',
})
export class AdminComponent implements OnInit {
  user: userdata[] = [];
  filteredUsers: userdata[] = [];
  size: number = 10;
  page: number = 0;
  totalUsers: number = 0;
  hasMoreUsers: boolean = true;
  searchTerm: string = '';

  constructor(
    private router: Router,
    private userservice: UserService,
    private dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(): void {
    this.userservice.getAllUsersPageWise(this.page, this.size).subscribe(
      (data: any) => {
        // Map API response to Registrationuser objects if needed
        this.user = data.content;
        this.user = data.content.map((obj: any) => {
          const roleName = obj.role.length > 0 ? obj.role[0].roleName : 'Guest'; // Default to 'Guest' if no role
          obj.accessRole = roleName // Assign access role based on mapping
          return obj; // Return the modified object
        });
        
        console.log(this.user);
        this.totalUsers = data.totalElements;
        this.filteredUsers = this.user;
        this.hasMoreUsers = this.user.length === this.size;
        console.log(this.user);
      },
      (error: any) => {
        console.error('Error fetching users', error);
        Swal.fire(
          'Error',
          'Failed to load users. Please try again later.',
          'error'
        );
      }
    );
  }

  filterUsers(): void {
    if (this.searchTerm) {
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
    });}

  deleteUser(name: string, role: string): void {
    // Don't allow deletion of admin accounts
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
    if (this.hasMoreUsers) {
      this.page++;
      this.loadUsers();
    }
  }
  
  openAddUserDialog(): void {
    const dialogRef = this.dialog.open(AddUserDialogComponent, {
      width: '500px',
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
      // For username with no spaces, return first two letters
      return name.slice(0, 2).toUpperCase();
    }
    
    // Get first letter of first name and first letter of last name
    return (nameParts[0].charAt(0) + nameParts[nameParts.length - 1].charAt(0)).toUpperCase();
  }

  getProfileColor(name: string): string {
    // Generate a consistent color based on the name
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