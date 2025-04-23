import { AfterViewInit, Component, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
declare var lottie: any;
@Component({
  selector: 'app-forbidden',
  standalone: false,
  templateUrl: './forbidden.component.html',
  styleUrl: './forbidden.component.css'
})
export class ForbiddenComponent implements AfterViewInit {


  constructor(@Inject(PLATFORM_ID) private platformId: Object) {}
  ngAfterViewInit() {

    if (isPlatformBrowser(this.platformId)) {
    const container = document.querySelector('.lottie-animation') as HTMLElement;
    if (container) {
      lottie.loadAnimation({
        container: container,
        renderer: 'svg',
        loop: true,
        autoplay: true,
        path: 'https://lottie.host/d987597c-7676-4424-8817-7fca6dc1a33e/BVrFXsaeui.json'
      });
    } else {
      console.error("Lottie container not found!");
    }
  }
}
}