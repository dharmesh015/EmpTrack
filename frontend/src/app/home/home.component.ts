import { Component, HostListener, OnDestroy, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';
import { interval, Subscription } from 'rxjs';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../_service/user.service';
import { DomSanitizer } from '@angular/platform-browser';
import { UserAuthServiceService } from '../_service/user-auth-service.service';
import { AddUserDialogComponent } from '../add-user-dialog/add-user-dialog.component';

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent implements OnInit {
  // Stats
  totalUsers: number = 0;
  activeUsers: number = 0;
  inactiveUsers: number = 0;
  adminCount: number = 0;
  superAdmins: number = 0;
  moderators: number = 0;
  activityCount: number = 0;
  todayActivities: number = 0;
  weekActivities: number = 0;
  
  // UI State
  activeCard: string = '';
  
  // Activity Demo Data
  recentActivities: any[] = [
    { 
      action: 'New User Created', 
      description: 'Account created for John Smith with standard privileges', 
      time: '10 minutes ago',
      by: 'Administrator',
      icon: 'person_add',
      iconClass: 'icon-create'
    },
    { 
      action: 'Profile Updated', 
      description: 'Sarah Johnson\'s contact details and role permissions modified', 
      time: '1 hour ago',
      by: 'System Admin',
      icon: 'edit',
      iconClass: 'icon-update'
    },
    { 
      action: 'User Deactivated', 
      description: 'Michael Brown\'s account temporarily suspended for security review', 
      time: '3 hours ago',
      by: 'Security Team',
      icon: 'block',
      iconClass: 'icon-delete'
    },
    { 
      action: 'Bulk Import Completed', 
      description: '24 user accounts were imported from the legacy system', 
      time: '1 day ago',
      by: 'Data Migration',
      icon: 'upload_file',
      iconClass: 'icon-import'
    },
    { 
      action: 'New Admin Assigned', 
      description: 'Jessica Wilson promoted to department administrator', 
      time: '2 days ago',
      by: 'HR Director',
      icon: 'add_moderator',
      iconClass: 'icon-admin'
    }
  ];

  constructor(
    private userService: UserService,
    private dialog: MatDialog,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadDashboardStats();
    this.animateNumbers();
  }

  loadDashboardStats(): void {
    // Get total users count
    this.userService.getAllUsersPageWise(0, 1).subscribe(
      (data: any) => {
        this.totalUsers = data.totalElements;
        
        // Set demo values for the UI (in a real app, get these from API)
        this.activeUsers = Math.round(this.totalUsers * 0.8);
        this.inactiveUsers = this.totalUsers - this.activeUsers;
        this.adminCount = Math.round(this.totalUsers * 0.15);
        this.superAdmins = Math.round(this.adminCount * 0.2);
        this.moderators = Math.round(this.adminCount * 0.8);
        this.activityCount = this.totalUsers * 5; // Estimate activity volume
        this.todayActivities = Math.round(this.activityCount * 0.05);
        this.weekActivities = Math.round(this.activityCount * 0.3);
        
        // In a real implementation, you'd make separate API calls for accurate data
        this.getUserStats();
      },
      (error) => {
        console.error('Error loading dashboard stats', error);
        // Set demo data if API fails
        this.setDemoData();
      }
    );
  }

  getUserStats(): void {
    // Placeholder for real implementation
    console.log('Getting detailed user stats...');
  }

  setDemoData(): void {
    // Fallback demo data if API fails
    this.totalUsers = 156;
    this.activeUsers = 132;
    this.inactiveUsers = 24;
    this.adminCount = 18;
    this.superAdmins = 3;
    this.moderators = 15;
    this.activityCount = 843;
    this.todayActivities = 47;
    this.weekActivities = 203;
  }

  animateNumbers(): void {
    // This would be implemented with a counter animation library
    console.log('Animating counters...');
  }

  openAddUserDialog(): void {
    const dialogRef = this.dialog.open(AddUserDialogComponent, {
      width: '500px',
      disableClose: true,
      panelClass: 'custom-dialog'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loadDashboardStats();
      }
    });
  }
}