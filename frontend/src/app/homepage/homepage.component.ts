import { Component } from '@angular/core';

@Component({
  selector: 'app-homepage',
  standalone: false,
  templateUrl: './homepage.component.html',
  styleUrl: './homepage.component.css'
})
export class HomepageComponent{
    // title = 'ecommerce-homepage';
    title = 'ecommerce-homepage';
  
  // You can add logic for navigation arrows here
  navigatePrevious() {
    // Logic for previous slide
    console.log('Navigate to previous slide');
  }
  
  navigateNext() {
    // Logic for next slide
    console.log('Navigate to next slide');
  }
  
categories = [
  {
    title: 'Continue shopping deals',
    items: [
      { name: 'Refrigerator blue design', image: 'assets/images/fridge-blue.jpg' },
      { name: 'Refrigerator red design', image: 'assets/images/fridge-red.jpg' },
      { name: 'Refrigerator snowflake', image: 'assets/images/fridge-snowflake.jpg' },
      { name: 'Refrigerator floral', image: 'assets/images/fridge-floral.jpg' }
    ]
  },
  {
    title: 'Appliances for your home | Up to 55% off',
    items: [
      { name: 'Air conditioners', image: 'assets/images/air-conditioner.jpg', category: 'Air conditioners' },
      { name: 'Refrigerators', image: 'assets/images/refrigerator.jpg', category: 'Refrigerators' },
      { name: 'Microwaves', image: 'assets/images/microwave.jpg', category: 'Microwaves' },
      { name: 'Washing machines', image: 'assets/images/washing-machine.jpg', category: 'Washing machines' }
    ]
  },
  {
    title: 'Revamp your home in style',
    items: [
      { name: 'Cushion covers & bedsheets', image: 'assets/images/cushion-covers.jpg', category: 'Cushion covers, bedsheets & more' },
      { name: 'Figurines & vases', image: 'assets/images/figurines.jpg', category: 'Figurines, vases & more' },
      { name: 'Home storage', image: 'assets/images/home-storage.jpg', category: 'Home storage' },
      { name: 'Lighting solutions', image: 'assets/images/lighting.jpg', category: 'Lighting solutions' }
    ]
  },
  {
    title: 'Starting ₹149 | Headphones',
    items: [
      { name: 'boAt', image: 'assets/images/boat.jpg', price: '₹249', brand: 'boAt' },
      { name: 'Boult', image: 'assets/images/boult.jpg', price: '₹349', brand: 'boult' },
      { name: 'Noise', image: 'assets/images/noise.jpg', price: '₹649', brand: 'Noise' },
      { name: 'Zebronics', image: 'assets/images/zebronics.jpg', price: '₹149', brand: 'Zebronics' }
    ]
  }
];
}