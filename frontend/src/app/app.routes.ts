import { Routes } from '@angular/router';
import { MainCarouselComponent } from './components/main-carousel/main-carousel.component';
import { AllArtworksComponent } from './components/all-artworks/all-artworks.component';
import { UserGalleryComponent } from './components/user-gallery/user-gallery.component';

export const routes: Routes = [
  { path: '', redirectTo: 'gallery', pathMatch: 'full' },
  { path: 'gallery', component: MainCarouselComponent },
  { path: 'gallery/all', component: AllArtworksComponent },
  { path: 'gallery/user', component: UserGalleryComponent },
];
