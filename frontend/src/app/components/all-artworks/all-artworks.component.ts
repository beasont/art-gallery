import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ArtService } from '../../services/art.service';

@Component({
  selector: 'app-all-artworks',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
  <div class="all-artworks-page">
    <h2>All Artworks</h2>

    <div class="controls">
      <label>Sort by:</label>
      <select [(ngModel)]="sortKey" (change)="onSortKeyChange(sortKey)">
        <option value="title">By Title</option>
        <option value="artist">By Author</option>
        <option value="year">By Year</option>
      </select>
      <button (click)="toggleOrder()">{{ sortOrderAsc ? 'Asc' : 'Desc' }}</button>

      <label>Filter By Artist:</label>
      <select [(ngModel)]="selectedArtist" (change)="onArtistChange()">
        <option value="All">All</option>
        <option *ngFor="let a of artists" [value]="a">{{ a }}</option>
      </select>

      <input type="text" [(ngModel)]="searchTerm" placeholder="Search..." />
      <button (click)="onSearch()">Search</button>
    </div>

    <div class="grid-container">
      <div class="art-tile" *ngFor="let art of artworks; index as i">
        <img [src]="art.imageUrl" [alt]="art.title" (click)="openNewTab(art)"/>
        <h4>{{ art.title }}</h4>
        <p>Artist: {{ art.artist }}</p>
        <p>Year: {{ art.year }}</p>
        <button (click)="openCommentForm(i)">
          {{ showCommentFormIndex === i ? 'Cancel' : 'Add Comment' }}
        </button>
        <div class="comment-form" *ngIf="showCommentFormIndex === i">
          <div class="radio-group">
            <label>
              <input type="radio" name="userType-{{i}}" value="guest" [(ngModel)]="selectedUserType[i]" (change)="onUserTypeChange(i)" checked />
              Guest
            </label>
            <label>
              <input type="radio" name="userType-{{i}}" value="user" [(ngModel)]="selectedUserType[i]" (change)="onUserTypeChange(i)" />
              User
            </label>
          </div>

          <div *ngIf="selectedUserType[i] === 'user'">
            <input type="text" placeholder="Username" [(ngModel)]="commentUsername[i]" />
            <input type="password" placeholder="Password" [(ngModel)]="commentPassword[i]" />
          </div>

          <textarea placeholder="Your comment" [(ngModel)]="commentText[i]"></textarea>
          <button (click)="submitComment(i)">Submit</button>
        </div>
      </div>
    </div>

    <div class="nav">
      <button [routerLink]="['/gallery']">Back to Main Carousel</button>
      <button [routerLink]="['/gallery/user']">User Gallery</button>
    </div>
  </div>
  `,
  styleUrls: ['./all-artworks.component.css']
})
export class AllArtworksComponent implements OnInit {
  artworks: any[] = [];
  sortKey = 'title';
  sortOrderAsc = true;
  selectedArtist = 'All';
  artists: string[] = [];
  searchTerm = '';

  // For adding a comment:
  showCommentFormIndex: number | null = null;
  // We'll store per-index arrays
  selectedUserType: ('guest'|'user')[] = [];
  commentUsername: string[] = [];
  commentPassword: string[] = [];
  commentText: string[] = [];

  constructor(private artService: ArtService) {}

  ngOnInit(): void {
    this.fetchAll();
  }

  fetchAll() {
    this.artService.getArtworks().subscribe({
      next: (data) => {
        this.artworks = data;
        this.collectArtistNames();
        this.applySorting();
      },
      error: (err) => console.error(err),
    });
  }

  collectArtistNames() {
    const setOfArtists = new Set<string>();
    this.artworks.forEach(a => setOfArtists.add(a.artist));
    this.artists = Array.from(setOfArtists.values()).sort();
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
    this.filterByArtist();
    this.applySearch();
  }

  toggleOrder() {
    this.sortOrderAsc = !this.sortOrderAsc;
    this.applySorting();
  }

  onSortKeyChange(newKey: string) {
    this.sortKey = newKey;
    this.applySorting();
  }

  filterByArtist() {
    if (this.selectedArtist === 'All') return;
    this.artworks = this.artworks.filter(a => a.artist === this.selectedArtist);
  }

  onArtistChange() {
    this.fetchAll();
  }

  applySearch() {
    if (!this.searchTerm.trim()) return;
    this.artworks = this.artworks.filter(a =>
      a.title.toLowerCase().includes(this.searchTerm.toLowerCase())
      || a.artist.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  onSearch() {
    this.fetchAll();
  }

  openCommentForm(i: number) {
    this.showCommentFormIndex = (this.showCommentFormIndex === i) ? null : i;
    // init arrays
    if (this.showCommentFormIndex === i) {
      this.selectedUserType[i] = 'guest';
      this.commentUsername[i] = '';
      this.commentPassword[i] = '';
      this.commentText[i] = '';
    }
  }

  onUserTypeChange(i: number) {
    if (this.selectedUserType[i] === 'guest') {
      this.commentUsername[i] = '';
      this.commentPassword[i] = '';
    }
  }

  submitComment(i: number) {
    const art = this.artworks[i];
    const artId = parseInt(art.id, 10);

    this.artService.addCommentWithUserOption(
      artId,
      this.commentText[i],
      this.selectedUserType[i] === 'user' ? this.commentUsername[i] : undefined,
      this.selectedUserType[i] === 'user' ? this.commentPassword[i] : undefined
    ).subscribe({
      next: () => {
        alert('Comment added!');
        this.showCommentFormIndex = null;
      },
      error: (err) => {
        console.error('Error adding comment:', err);
        alert('Failed to add comment.');
      }
    });
  }

  openNewTab(art: any) {
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
              overflow: hidden;
              display: flex;
              align-items: center;
              justify-content: center;
            }
            img {
              max-width: 100%;
              max-height: 100%;
              object-fit: contain;
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
