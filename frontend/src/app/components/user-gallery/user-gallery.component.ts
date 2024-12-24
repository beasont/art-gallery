import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { UserArtService, UserArt } from '../../services/user-art.service';
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
  artYearInput?: number;
  fileToUpload?: File;
  uploadUsername = ''; // Optional Field
  uploadPassword = ''; // Optional Field

  // Comment form
  showCommentFormIndex: number | null = null;
  commentUsername = '';
  commentText = '';
  commentMode = 'GUEST'; // 'GUEST' or 'USER'
  commentPassword = '';

  // Edit form
  editFormVisible = false;
  editArtId: number | null = null;
  editTitle = '';
  editArtist = '';
  editArtYear: number = 0; // Initialize with a valid default number
  editFileToUpload?: File;
  editUsername = ''; // For authentication
  editPassword = ''; // For authentication

  // Drawing feature
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

    // Username and password are optional
    if ((this.uploadUsername && !this.uploadPassword) || (!this.uploadUsername && this.uploadPassword)) {
      alert('Please provide both username and password, or leave both empty.');
      return;
    }

    this.userArtService.uploadArt(
      this.fileToUpload,
      this.titleInput.trim(),
      this.artistInput.trim(),
      this.artYearInput,
      this.uploadUsername.trim() || undefined,
      this.uploadPassword.trim() || undefined
    ).subscribe({
      next: (newArt) => {
        alert('User Artwork uploaded!');
        this.showUploadForm = false;
        // Reset form fields for next upload
        this.titleInput = '';
        this.artistInput = '';
        this.artYearInput = undefined;
        this.fileToUpload = undefined;
        this.uploadUsername = '';
        this.uploadPassword = '';
        this.fetchUserArt();
      },
      error: (err) => {
        console.error('Error uploading user art:', err);
        alert('Failed to upload user artwork.');
      }
    });
  }

  // **Edit Artwork Methods**

  openEditForm(art: UserArt) {
    if (!art.hashedPassword) {
      alert('This artwork was uploaded as a guest and cannot be edited.');
      return;
    }
    this.editFormVisible = true;
    this.editArtId = art.id;
    this.editTitle = art.title;
    this.editArtist = art.artist;
    this.editArtYear = art.artYear || 0; // Default to 0 if undefined
    this.editFileToUpload = undefined;
    this.editUsername = '';
    this.editPassword = '';
  }

  handleEditFileInput(event: any) {
    if (event.target.files && event.target.files.length > 0) {
      this.editFileToUpload = event.target.files[0];
    }
  }

  submitEdit() {
    if (!this.editArtId || !this.editTitle || !this.editArtist || !this.editArtYear) {
      alert('Please complete all fields.');
      return;
    }

    this.userArtService.editArt(
      this.editArtId,
      this.editTitle.trim(),
      this.editArtist.trim(),
      this.editArtYear,
      this.editFileToUpload,
      this.editUsername.trim(),
      this.editPassword.trim()
    ).subscribe({
      next: (updatedArt) => {
        const index = this.userArtworks.findIndex(art => art.id === this.editArtId);
        if (index !== -1) {
          this.userArtworks[index] = updatedArt;
        }
        alert('Artwork updated successfully!');
        this.closeEditForm();
      },
      error: (err) => {
        console.error('Error editing artwork:', err);
        alert('Failed to update artwork.');
      }
    });
  }

  closeEditForm() {
    this.editFormVisible = false;
    this.editArtId = null;
    this.editTitle = '';
    this.editArtist = '';
    this.editArtYear = 0;
    this.editFileToUpload = undefined;
    this.editUsername = '';
    this.editPassword = '';
  }
  
  // **Delete Artwork Methods**
  confirmDelete(art: UserArt) {
    if (!art.hashedPassword) {
      alert('This artwork was uploaded as a guest and cannot be deleted.');
      return;
    }
  
    const username = prompt("Enter your username to confirm deletion:")?.trim();
    const password = prompt("Enter your password:")?.trim();
  
    if (!username || !password) {
      alert("Deletion aborted. Both username and password are required.");
      return;
    }
  
    this.userArtService.deleteArt(art.id, username, password).subscribe({
      next: (res) => {
        console.log('Delete response:', res);
        alert(res.message || 'Artwork deleted successfully!');
        this.fetchUserArt();
      },
      error: (err) => {
        console.error('Error deleting artwork:', err);
        alert(err.message || 'Failed to delete artwork. Please try again later.');
      }
    });
  }

  // **Comment form methods**
  openCommentForm(i: number) {
    this.showCommentFormIndex = (this.showCommentFormIndex === i) ? null : i;
    this.commentUsername = '';
    this.commentText = '';
    this.commentMode = 'GUEST';
    this.commentPassword = '';
  }

  submitComment(i: number) {
    const art = this.userArtworks[i];
    const artId = art.id;

    // For guest-uploaded artworks, no username/password is needed
    this.artService.addCommentWithUserOption(
      artId,
      this.commentText.trim(),
      this.commentMode === 'USER' ? this.commentUsername.trim() : undefined,
      this.commentMode === 'USER' ? this.commentPassword.trim() : undefined
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

  // **Drawing Feature Methods**
  openDrawingWindow() {
    this.drawingOpen = true;
    // Initialize the canvas after the view has rendered
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
    // Get data URL
    const dataURL = this.drawingCanvas.nativeElement.toDataURL("image/png");

    // Prompt for title, author, date
    const titleInput = prompt("Enter Title:");
    if(!titleInput || !titleInput.trim()) { alert("Aborted. Title is required."); return; }
    const title = titleInput.trim();

    const artistInput = prompt("Enter Artist:");
    if(!artistInput || !artistInput.trim()) { alert("Aborted. Artist is required."); return; }
    const artist = artistInput.trim();

    const yearInput = prompt("Enter Year (number):");
    if(!yearInput || isNaN(Number(yearInput))) { alert("Invalid year. Aborted."); return; }
    const year = parseInt(yearInput, 10);

    // Convert dataURL to Blob
    const byteString = atob(dataURL.split(',')[1]);
    const ab = new ArrayBuffer(byteString.length);
    const ia = new Uint8Array(ab);
    for(let i=0; i < byteString.length; i++){
      ia[i] = byteString.charCodeAt(i);
    }

    // Construct a file object
    const blob = new Blob([ia], {type: 'image/png'});
    const file = new File([blob], "canvasDrawing.png", {type: 'image/png'});

    // Prompt for username and password for the drawing upload
    const usernameInput = prompt("Enter your username for the drawing (leave blank to upload as Guest):");
    const passwordInput = usernameInput ? prompt("Enter your password:") : '';

    // Validate credentials
    if ((usernameInput && !passwordInput) || (!usernameInput && passwordInput)) {
      alert("Please provide both username and password, or leave both empty.");
      return;
    }

    const username = usernameInput ? usernameInput.trim() : undefined;
    const password = passwordInput ? passwordInput.trim() : undefined;

    // Proceed with upload
    this.userArtService.uploadArt(
      file,
      title,
      artist,
      year,
      username || undefined,
      password || undefined
    )
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
