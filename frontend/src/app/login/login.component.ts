
import { Component, OnInit } from '@angular/core';
import { UserAuthServiceService } from '../_service/user-auth-service.service';
import { Router } from '@angular/router';
import { UserService } from '../_service/user.service';
import { NgForm } from '@angular/forms';
import Swal from 'sweetalert2';
import { Requestuser } from '../modul/requestuser';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent implements OnInit {
  captchaUrl: string = '';
  userdata: Requestuser = new Requestuser('', '');
  

  constructor(
    private loginService: UserService,
    private userAuthService: UserAuthServiceService,
    private router: Router
  ) {}

  ngOnInit(): void {

    if (!this.userAuthService.isLoggedIn()) {
      this.loadCaptcha();
    }
  }

  // loadCaptcha() {
  //   this.loginService.getCaptchaImage().subscribe(
  //     (response: Blob) => {
  //       this.captchaUrl = URL.createObjectURL(response);
  //       console.log(this.captchaUrl);
  //     },
  //     (error: { message: any; error: { message: any } }) => {
  //       const errorMessage =
  //         error.message || error.error?.message || 'Error fetching CAPTCHA image.';
  //       Swal.fire({
  //         icon: 'error',
  //         title: 'Captcha not loading!',
  //         text: errorMessage,
  //         confirmButtonText: 'OK',
  //       });
  //     }
  //   );
  // }
  loadCaptcha() {
    const timestamp = new Date().getTime();
    this.loginService.getCaptchaImage(timestamp).subscribe(
      (response: Blob) => {
        // Revoke previous URL if exists
        if (this.captchaUrl) {
          URL.revokeObjectURL(this.captchaUrl);
        }
        this.captchaUrl = URL.createObjectURL(response);
      },
      (error) => {
        const errorMessage = error.message || error.error?.message || 'Error fetching CAPTCHA image.';
        Swal.fire({
          icon: 'error',
          title: 'Captcha not loading!',
          text: errorMessage,
          confirmButtonText: 'OK',
        });
      }
    );
  }

  ReloadCaptcha() {
    this.loadCaptcha();
  }

  ngOnDestroy() {
    if (this.captchaUrl) {
      URL.revokeObjectURL(this.captchaUrl);
    }
  }
  handleImageError() {
    console.error('CAPTCHA image failed to load');
    this.loadCaptcha(); // Try reloading on error
  }

  login(form: NgForm) {
    if (form.valid) {
      const captcha = form.value.captcha;
      this.userdata.userName = form.value.username;
      this.userdata.userPassword = form.value.password;
      this.userdata.captcha = form.value.captcha;

      this.loginService.login(this.userdata).subscribe(
        (response: any) => {
          console.log(response);
          this.userAuthService.setRoles(response.user.role);
          this.userAuthService.setToken(response.jwtToken);
          this.userAuthService.setUser(response.user);

          const role = response.user.role[0].roleName;
          console.log("role--"+role)
          if (role === 'ADMIN') {
            this.router.navigate(['/admin']);
          }  else {
            this.router.navigate(['/home']);
          }
        },
        (error) => {
          if (error.error === 'InvalidCAPTCHA') {
            Swal.fire({
              title: 'Invalid CAPTCHA',
              text: 'Please enter the correct CAPTCHA code.',
              icon: 'error',
              confirmButtonText: 'OK',
            });
            this.ReloadCaptcha();
          } else {
            Swal.fire({
              title: 'Invalid Credentials',
              text: 'Please check your username and password.',
              icon: 'error',
              confirmButtonText: 'OK',
            });
          }
        }
      );
    }
  }

  isloggin() {
    return !this.userAuthService.isLoggedIn();
  }
}
