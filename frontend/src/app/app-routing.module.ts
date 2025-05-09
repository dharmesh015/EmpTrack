import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { AdminComponent } from './admin/admin.component';
import { LoginComponent } from './login/login.component';
import { ForbiddenComponent } from './forbidden/forbidden.component';
import { RegistrationComponent } from './registration/registration.component';
import { AuthGuard } from './_auth/auth.guard';

import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { AdminProfileComponent } from './admin-profile/admin-profile.component';
import { AddUserComponent } from './add-user/add-user.component';

const routes: Routes = [
  { path: 'home', component: HomeComponent },

  {
    path: 'admin',
    component: AdminComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ADMIN'] },
  },
  { path: 'login', component: LoginComponent },
  { path: 'registration', component: RegistrationComponent },
  { path: 'forbidden', component: ForbiddenComponent },

  { path: 'reset-password', component: ResetPasswordComponent },
  { path: 'adduser', component: AddUserComponent },
  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'profile', component: AdminProfileComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', component: ForbiddenComponent },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, {
      scrollPositionRestoration: 'enabled',
      anchorScrolling: 'enabled',
      onSameUrlNavigation: 'reload',
      scrollOffset: [0, 50],
    }),
  ],
  exports: [RouterModule],
})
export class AppRoutingModule {}
