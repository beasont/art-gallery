import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ArtService {
  private baseUrl = 'http://localhost:5443/api/artworks';
  private commentUrl = 'http://localhost:5443/api/comments';

  constructor(private http: HttpClient) {}

  getArtworks(): Observable<any> {
    return this.http.get(`${this.baseUrl}`);
  }

  // Add a quick wrapper so main-carousel can call it
  addComment(artId: number, text: string, username?: string): Observable<any> {
    return this.addCommentWithUserOption(artId, text, username, undefined);
  }

  // For adding a comment to either main or user artwork (with optional password)
  addCommentWithUserOption(
    artId: number,
    text: string,
    username?: string,
    password?: string
  ): Observable<any> {
    const formData = new FormData();
    formData.append('artId', String(artId));
    formData.append('text', text);
    if (username) {
      formData.append('username', username);
    }
    if (password) {
      formData.append('password', password);
    }
    return this.http.post(`${this.commentUrl}`, formData);
  }

  // For comment Deletion
  deleteComment(commentId: number, username?: string, password?: string): Observable<any> {
    const formData = new FormData();
    formData.append('commentId', String(commentId));
    if (username) formData.append('username', username);
    if (password) formData.append('password', password);
    return this.http.post(`${this.commentUrl}/delete`, formData);
  }

  // For comment Editing
  editComment(commentId: number, newText: string, username?: string, password?: string): Observable<any> {
    const formData = new FormData();
    formData.append('commentId', String(commentId));
    formData.append('newText', newText);
    if (username) formData.append('username', username);
    if (password) formData.append('password', password);
    return this.http.post(`${this.commentUrl}/edit`, formData);
  }
}
