import { Component, OnInit } from '@angular/core';
import { UserService } from '../_service/user.service';
import { UserAuthServiceService } from '../_service/user-auth-service.service';
import { UserDetailsProxy } from '../modul/user-details-proxy';

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

  constructor(private userService: UserService,private authService:UserAuthServiceService) { }

  ngOnInit(): void {
   
    const userName = this.authService.getUser().userName;
    console.log(userName)
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