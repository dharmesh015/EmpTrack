import { Component, HostListener, OnDestroy, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';

import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import Swal from 'sweetalert2';
import { interval, Subscription } from 'rxjs';
import { Registrationuser } from '../modul/registrationuser';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { UserService } from '../_service/user.service';
import { DomSanitizer } from '@angular/platform-browser';
import { UserAuthServiceService } from '../_service/user-auth-service.service';

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent {}