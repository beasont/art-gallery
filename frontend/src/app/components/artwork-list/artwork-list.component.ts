import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ArtService } from '../../services/art.service';

@Component({
  selector: 'app-artwork-list',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="gallery-container">
      <!-- Button to toggle gallery visibility -->
      <button (click)="toggleGallery()">
        {{ galleryVisible ? 'Close the Gallery' : 'Open the Gallery' }}
      </button>

      <!-- Conditionally display the gallery -->
      <div *ngIf="galleryVisible" class="gallery">
        <h2>Art Gallery</h2>
        <div *ngFor="let artwork of artworks" class="artwork-item">
          <h3>{{ artwork.title }} by {{ artwork.artist }}</h3>
          <img [src]="artwork.imageUrl" alt="{{ artwork.title }}" />
        </div>
      </div>
    </div>
  `,
  styles: [`
    .gallery-container {
      text-align: center;
      margin: 20px;
    }

    button {
      background-color: #333;
      color: white;
      padding: 10px 20px;
      border: none;
      border-radius: 5px;
      cursor: pointer;
      font-size: 16px;
    }

    button:hover {
      background-color: #555;
    }

    .gallery {
      margin-top: 20px;
    }

    .artwork-item {
      margin-bottom: 20px;
    }

    .artwork-item img {
      max-width: 300px;
      border: 1px solid #ddd;
      border-radius: 5px;
    }
  `]
})
export class ArtworkListComponent {
  artworks: any[] = [];
  galleryVisible: boolean = false; // Initially hide the gallery

  constructor(private artService: ArtService) {}

  toggleGallery(): void {
    this.galleryVisible = !this.galleryVisible; // Toggle the gallery visibility
    if (this.galleryVisible && this.artworks.length === 0) {
      this.fetchArtworks();
    }
  }

  fetchArtworks(): void {
    this.artService.getArtworks().subscribe(
      (data) => {
        console.log('Fetched Artworks:', data);
        this.artworks = data;
      },
      (error) => {
        console.error('Error fetching artworks:', error);
      }
    );
  }
}
