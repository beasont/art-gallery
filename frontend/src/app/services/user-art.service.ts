import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface UserArt {
  id: number;
  title: string;
  artist: string;
  artYear: number;
  imageUrl: string;
  hashedPassword?: string;
}

@Injectable({
  providedIn: 'root',
})
export class UserArtService {
  private baseUrl = 'http://localhost:5443/api/user-art';

  constructor(private http: HttpClient) {}

  getAll(): Observable<UserArt[]> {
    return this.http.get<UserArt[]>(`${this.baseUrl}`).pipe(
      catchError(this.handleError)
    );
  }

  uploadArt(
    file: File,
    title: string,
    artist: string,
    artYear: number,
    username?: string,
    password?: string
  ): Observable<UserArt> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('title', title);
    formData.append('artist', artist);
    formData.append('artYear', String(artYear));

    if (username && password) {
      formData.append('username', username);
      formData.append('password', password);
    }

    return this.http.post<UserArt>(`${this.baseUrl}/upload`, formData).pipe(
      catchError(this.handleError)
    );
  }

  editArt(
    artId: number,
    title: string,
    artist: string,
    artYear: number,
    file?: File,
    username?: string,
    password?: string
  ): Observable<any> {
    const formData = new FormData();
    formData.append('artId', String(artId));
    formData.append('title', title);
    formData.append('artist', artist);
    formData.append('artYear', String(artYear));
    if (file) {
      formData.append('file', file);
    }
  
    formData.append('username', username || '');
    formData.append('password', password || '');
  
    return this.http.post<Map<string, string>>(`${this.baseUrl}/edit`, formData).pipe(
      catchError(this.handleError)
    );
  }  

  deleteArt(artId: number, username: string, password: string): Observable<any> {
    const formData = new FormData();
    formData.append('artId', String(artId));
    formData.append('username', username);
    formData.append('password', password);
    return this.http.post<Map<string, string>>(`${this.baseUrl}/delete`, formData).pipe(
      catchError(this.handleError)
    );
  }

  // Handle HTTP Errors
  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An unknown error occurred!';
    if (error.error instanceof ErrorEvent) {
      // Client-side/network error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Backend returned unsuccessful response code
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.error.message || error.error}`;
    }
    console.error(errorMessage);
    return throwError(() => new Error(error.error.message || 'Something bad happened; please try again later.'));
  }
}
