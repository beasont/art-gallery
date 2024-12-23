import { Component, OnInit, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ArtService } from '../../services/art.service';

interface Artwork {
  id: string;
  title: string;
  artist: string;
  imageUrl: string;
  year?: string;
}

@Component({
  selector: 'app-main-carousel',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './main-carousel.component.html',
  styleUrls: ['./main-carousel.component.css']
})
export class MainCarouselComponent implements OnInit, AfterViewInit {
  artworks: Artwork[] = [];
  currentIndex = 0;
  sortKey = 'title';
  sortOrderAsc = true;

  // For adding a comment:
  showCommentForm = false;
  commentUsername = '';
  commentText = '';

  constructor(private artService: ArtService) {}

  ngOnInit(): void {
    // Initial fetch
    this.fetchArtworks();
  }

  // Force re-check or re-fetch once view is loaded
  ngAfterViewInit(): void {
    // Some browsers or router configs can cause images not to appear on the first load.
    // We'll do a quick sanity check or re-fetch:
    if (!this.artworks || this.artworks.length === 0) {
      this.fetchArtworks();
    }
  }

  fetchArtworks() {
    this.artService.getArtworks().subscribe({
      next: (data) => {
        this.artworks = data.map((item: any) => ({
          ...item,
          year: item.year ? item.year : '1900'
        }));
        this.applySorting();
      },
      error: (err) => console.error(err),
    });
  }

  applySorting() {
    switch (this.sortKey) {
      case 'title':
        this.artworks.sort((a, b) => a.title.localeCompare(b.title));
        break;
      case 'artist':
        this.artworks.sort((a, b) => a.artist.localeCompare(b.artist));
        break;
      case 'year':
        this.artworks.sort((a, b) =>
          parseInt(a.year || '0', 10) - parseInt(b.year || '0', 10)
        );
        break;
    }
    if (!this.sortOrderAsc) {
      this.artworks.reverse();
    }
  }

  onSortKeyChange(newKey: string) {
    this.sortKey = newKey;
    this.currentIndex = 0; // reset
    this.applySorting();
  }

  toggleOrder() {
    this.sortOrderAsc = !this.sortOrderAsc;
    this.applySorting();
  }

  prevImage() {
    if (this.artworks.length === 0) return;
    this.currentIndex =
      (this.currentIndex - 1 + this.artworks.length) % this.artworks.length;
  }

  nextImage() {
    if (this.artworks.length === 0) return;
    this.currentIndex =
      (this.currentIndex + 1) % this.artworks.length;
  }

  toggleCommentForm() {
    this.showCommentForm = !this.showCommentForm;
    this.commentUsername = '';
    this.commentText = '';
  }

  submitComment() {
    const artId = parseInt(this.artworks[this.currentIndex].id, 10);
    this.artService.addComment(artId, this.commentText, this.commentUsername || undefined)
      .subscribe({
        next: () => {
          alert('Comment added!');
          this.showCommentForm = false;
        },
        error: (err) => {
          console.error('Error adding comment:', err);
          alert('Failed to add comment.');
        }
      });
  }

  // Opens image in new tab, no scrollbars, auto-resize
  openNewTab(art: Artwork) {
    const imageUrl = art.imageUrl;
    const newTab = window.open('', '_blank');
    if (!newTab) {
      console.warn('Popup blocked or failed to open new tab');
      return;
    }
    newTab.document.write(`
      <html>
        <head>
          <title>${art.title || 'Artwork'}</title>
          <style>
            html, body {
              margin: 0;
              padding: 0;
              background-color: black;
              height: 100%;
              overflow: hidden; /* no scrollbar */
              display: flex;
              align-items: center;
              justify-content: center;
            }
            img {
              max-width: 100%;
              max-height: 100%;
              object-fit: contain;
              display: block;
            }
          </style>
        </head>
        <body>
          <img src="${imageUrl}" alt="Enlarged" />
        </body>
      </html>
    `);
    newTab.document.close();
  }
}
