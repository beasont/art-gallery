import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { UserArt, UserArtService } from '../../services/user-art.service';
import { ArtService } from '../../services/art.service';

@Component({
  selector: 'app-user-gallery',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './user-gallery.component.html',
  styleUrls: ['./user-gallery.component.css']
})
export class UserGalleryComponent implements OnInit {
  userArtworks: UserArt[] = [];
  sortKey = 'title';
  sortOrderAsc = true;
  selectedArtist = 'All';
  artists: string[] = [];
  searchTerm = '';

  // Upload form
  showUploadForm = false;
  titleInput = '';
  artistInput = '';
  artYearInput?: number; // renamed from 'yearInput'
  fileToUpload?: File;

  // For comment
  showCommentFormIndex: number | null = null;
  commentUsername = '';
  commentText = '';
  commentMode = 'GUEST'; // 'GUEST' or 'USER'
  commentPassword = '';

  // For "Draw something!" 
  drawingOpen = false;
  @ViewChild('drawingCanvas') drawingCanvas?: ElementRef<HTMLCanvasElement>;
  context!: CanvasRenderingContext2D | null;
  isDrawing = false;
  currentX = 0;
  currentY = 0;
  drawMode = 'pencil'; // pencil or eraser

  constructor(
    private userArtService: UserArtService,
    private artService: ArtService
  ) {}

  ngOnInit(): void {
    this.fetchUserArt();
  }

  fetchUserArt(): void {
    this.userArtService.getAll().subscribe({
      next: (data) => {
        this.userArtworks = data;
        this.collectArtistNames();
        this.applySorting();
      },
      error: (err) => console.error('Error fetching user artworks', err),
    });
  }

  collectArtistNames() {
    const setOfArtists = new Set<string>();
    this.userArtworks.forEach(a => setOfArtists.add(a.artist));
    this.artists = Array.from(setOfArtists.values()).sort();
  }

  applySorting() {
    switch (this.sortKey) {
      case 'title':
        this.userArtworks.sort((a, b) => a.title.localeCompare(b.title));
        break;
      case 'artist':
        this.userArtworks.sort((a, b) => a.artist.localeCompare(b.artist));
        break;
      case 'artYear':
        this.userArtworks.sort((a, b) => (a.artYear ?? 0) - (b.artYear ?? 0));
        break;
    }
    if (!this.sortOrderAsc) {
      this.userArtworks.reverse();
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
    this.userArtworks = this.userArtworks.filter(a => a.artist === this.selectedArtist);
  }

  onArtistChange() {
    this.fetchUserArt();
  }

  applySearch() {
    if (!this.searchTerm.trim()) return;
    this.userArtworks = this.userArtworks.filter(a =>
      a.title.toLowerCase().includes(this.searchTerm.toLowerCase())
      || a.artist.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  onSearch() {
    this.fetchUserArt();
  }

  toggleUploadForm(event?: MouseEvent) {
    event?.preventDefault();
    this.showUploadForm = !this.showUploadForm;
  }

  handleFileInput(event: any) {
    if (event.target.files && event.target.files.length > 0) {
      this.fileToUpload = event.target.files[0];
    }
  }

  uploadNewArt() {
    if (!this.fileToUpload || !this.titleInput || !this.artistInput || !this.artYearInput) {
      alert('Please provide file, title, artist, and year.');
      return;
    }

    this.userArtService.uploadArt(
      this.fileToUpload,
      this.titleInput,
      this.artistInput,
      this.artYearInput
    ).subscribe({
      next: (newArt) => {
        alert('User Artwork uploaded!');
        this.showUploadForm = false;
        this.titleInput = '';
        this.artistInput = '';
        this.artYearInput = undefined;
        this.fileToUpload = undefined;
        this.fetchUserArt();
      },
      error: (err) => {
        console.error('Error uploading user art:', err);
        alert('Failed to upload user artwork.');
      }
    });
  }

  // Comment form toggling
  openCommentForm(i: number) {
    this.showCommentFormIndex = (this.showCommentFormIndex === i) ? null : i;
    this.commentUsername = '';
    this.commentText = '';
    this.commentMode = 'GUEST';
    this.commentPassword = '';
  }

  submitComment(i: number) {
    const art = this.userArtworks[i];
    let artId = art.id;

    // send via ArtService
    const formData = new FormData();
    formData.append('artId', String(artId));
    formData.append('text', this.commentText);
    if (this.commentMode === 'USER') {
      // user => send username + password
      formData.append('username', this.commentUsername || 'User');
      formData.append('password', this.commentPassword || '');
    } else {
      // guest => maybe no username
    }

    fetch('http://localhost:5443/api/comments', {
      method: 'POST',
      body: formData
    })
    .then(res => {
      if(!res.ok) throw new Error("Error adding comment");
      return res.text();
    })
    .then(txt => {
      alert("Comment added: " + txt);
      this.showCommentFormIndex = null;
    })
    .catch(err => {
      console.error("Error adding comment:", err);
      alert("Failed to add comment.");
    });
  }


  // =========== DRAW SOMETHING FEATURE =============
  drawingDataURL: string | null = null;
  showDrawPrompt = false;

  openDrawingWindow() {
    this.drawingOpen = true;
    // We'll wait a tick, then init the canvas
    setTimeout(() => {
      if (this.drawingCanvas) {
        this.context = this.drawingCanvas.nativeElement.getContext('2d');
        if (this.context) {
          this.context.fillStyle = "#ffffff";
          this.context.fillRect(0,0,
            this.drawingCanvas.nativeElement.width,
            this.drawingCanvas.nativeElement.height
          );
        }
      }
    }, 100);
  }

  closeDrawingWindow() {
    this.drawingOpen = false;
  }

  setMode(mode: string) {
    this.drawMode = mode; // "pencil" or "eraser"
  }

  onMouseDown(evt: MouseEvent) {
    if (!this.context) return;
    this.isDrawing = true;
    const rect = this.drawingCanvas?.nativeElement.getBoundingClientRect();
    if (!rect) return;
    this.currentX = evt.clientX - rect.left;
    this.currentY = evt.clientY - rect.top;
  }

  onMouseMove(evt: MouseEvent) {
    if (!this.isDrawing || !this.context) return;
    const rect = this.drawingCanvas?.nativeElement.getBoundingClientRect();
    if (!rect) return;
    const newX = evt.clientX - rect.left;
    const newY = evt.clientY - rect.top;

    this.context.lineCap = "round";
    if (this.drawMode === 'eraser') {
      this.context.strokeStyle = '#ffffff';
      this.context.lineWidth = 5;
    } else {
      this.context.strokeStyle = '#000000';
      this.context.lineWidth = 1;
    }

    this.context.beginPath();
    this.context.moveTo(this.currentX, this.currentY);
    this.context.lineTo(newX, newY);
    this.context.stroke();

    this.currentX = newX;
    this.currentY = newY;
  }

  onMouseUp() {
    this.isDrawing = false;
  }
  onMouseLeave() {
    this.isDrawing = false;
  }

  resetDrawing() {
    if(this.context && this.drawingCanvas) {
      this.context.fillStyle = "#ffffff";
      this.context.fillRect(
        0,0,
        this.drawingCanvas.nativeElement.width,
        this.drawingCanvas.nativeElement.height
      );
    }
  }

  saveDrawing() {
    if(!this.context || !this.drawingCanvas) return;
    // get data url
    const dataURL = this.drawingCanvas.nativeElement.toDataURL("image/png");
    // prompt for title, author, date => then store in user gallery
    const t = prompt("Enter Title:");
    if(!t) { alert("Aborted."); return; }
    const a = prompt("Enter Artist:");
    if(!a) { alert("Aborted."); return; }
    const y = prompt("Enter Year (number):");
    if(!y) { alert("Aborted."); return; }

    // We'll store a "file" in memory? We'll do a direct call to userArtService
    // but we actually have a base64 data => can store it as a "file"? We'll do the same approach
    // as normal upload but with a Blob.
    const byteString = atob(dataURL.split(',')[1]);
    const ab = new ArrayBuffer(byteString.length);
    const ia = new Uint8Array(ab);
    for(let i=0; i < byteString.length; i++){
      ia[i] = byteString.charCodeAt(i);
    }
    // Construct a file object
    const blob = new Blob([ia], {type: 'image/png'});
    const file = new File([blob], "canvasDrawing.png", {type: 'image/png'});

    // now let's do the upload
    this.userArtService.uploadArt(file, t, a, parseInt(y,10))
    .subscribe({
      next: (newArt) => {
        alert('User Artwork saved from drawing!');
        this.fetchUserArt();
        this.closeDrawingWindow();
      },
      error: (err) => {
        console.error("Error uploading from drawing:", err);
        alert("Failed to upload from drawing.");
      }
    });
  }
}
