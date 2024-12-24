import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

interface CommentItem {
  id: number;
  text: string;
  username: string;
  createdAt: string;
  artTitle: string;
  artArtist: string;
  isUser: boolean; // used to highlight
  artId: number;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <div class="app-header">
      <h1>Art Gallery Project</h1>
      <h1>Internet Programming UACS || 2024 || by Tristan Beason, ID 5443</h1>
      <button (click)="toggleComments()">Show Most Recent Comments</button>
    </div>

    <div *ngIf="showComments" class="comments-panel">
      <h2>Recent Comments</h2>
      <div *ngIf="recentComments.length === 0">No comments found.</div>
      <ul>
        <li *ngFor="let c of recentComments">
          <span [ngStyle]="{'color': c.isUser ? 'blue' : 'inherit'}">
            <strong>{{ c.username }}</strong>
          </span>
           on
          <em>{{ c.artTitle }}</em> by <em>{{ c.artArtist }}</em>:
          {{ c.text }} 
          <small>({{ c.createdAt }})</small>
          <div *ngIf="c.isUser">
            <!-- We show 'Edit' / 'Delete' for user comments -->
            <button (click)="attemptDelete(c)">Delete</button>
            <button (click)="attemptEdit(c)">Edit</button>
          </div>
        </li>
      </ul>
    </div>

    <router-outlet></router-outlet>
  `,
  styles: [
    `
    .app-header {
      background-color: #efefef;
      padding: 10px;
    }
    .comments-panel {
      background-color: rgba(255,255,255,0.9);
      border: 1px solid #ccc;
      margin: 10px;
      padding: 10px;
      border-radius: 6px;
    }
    ul {
      list-style-type: none;
      padding-left: 0;
    }
    li {
      margin-bottom: 1em;
    }
    `
  ]
})
export class AppComponent {
  showComments = false;
  recentComments: CommentItem[] = [];

  toggleComments() {
    this.showComments = !this.showComments;
    if (this.showComments) {
      this.fetchRecentComments();
    }
  }

  fetchRecentComments() {
    fetch('http://localhost:5443/api/comments/recent')
      .then(resp => resp.json())
      .then((data: CommentItem[]) => {
        this.recentComments = data;
      })
      .catch(err => {
        console.error('Error fetching recent comments:', err);
      });
  }

  attemptDelete(c: CommentItem) {
    const username = prompt("Enter username for comment deletion:", c.username);
    if (!username) { alert("Aborted."); return; }
    const password = prompt("Enter password for user '" + username + "':");
    if(!password) { alert("Aborted."); return; }

    const formData = new FormData();
    formData.append('commentId', String(c.id));
    formData.append('username', username);
    formData.append('password', password);

    fetch('http://localhost:5443/api/comments/delete', {
      method: 'POST',
      body: formData
    })
    .then(res => res.text())
    .then(txt => {
      if (txt.startsWith("Deleted comment successfully")) {
        alert(txt);
        this.fetchRecentComments();
      } else {
        alert(txt);
      }
    })
    .catch(err => {
      console.error("Error deleting comment:", err);
    });
  }

  attemptEdit(c: CommentItem) {
    const username = prompt("Enter username for comment edit:", c.username);
    if (!username) { alert("Aborted."); return; }
    const password = prompt("Enter password for user '" + username + "':");
    if(!password) { alert("Aborted."); return; }
    const newText = prompt("Enter new comment text:", c.text);
    if(!newText) { alert("Aborted."); return; }

    const formData = new FormData();
    formData.append('commentId', String(c.id));
    formData.append('newText', newText);
    formData.append('username', username);
    formData.append('password', password);

    fetch('http://localhost:5443/api/comments/edit', {
      method: 'POST',
      body: formData
    })
    .then(res => res.text())
    .then(txt => {
      if (txt.startsWith("Edited comment successfully")) {
        alert(txt);
        this.fetchRecentComments();
      } else {
        alert(txt);
      }
    })
    .catch(err => {
      console.error("Error editing comment:", err);
    });
  }
}
