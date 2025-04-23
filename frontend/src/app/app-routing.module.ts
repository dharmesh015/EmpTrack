import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { AdminComponent } from './admin/admin.component';
import { LoginComponent } from './login/login.component';
import { ForbiddenComponent } from './forbidden/forbidden.component';
import { RegistrationComponent } from './registration/registration.component';
import { AuthGuard } from './_auth/auth.guard';

// import { ProductDisplayComponent } from './product-display-component/product-display-component.component';

import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './reset-password/reset-password.component';
import { HomepageComponent } from './homepage/homepage.component';
import { AdminProfileComponent } from './admin-profile/admin-profile.component';

const routes: Routes = [
  // import { AppRoutingModule } from './app-routing.module';
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

  { path: 'forgot-password', component: ForgotPasswordComponent },
  { path: 'profile', component: AdminProfileComponent }, 
  { path: 'homepage', component: HomepageComponent },
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: '**', component: ForbiddenComponent },
];

@NgModule({
  // exports: [RouterModule],
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
