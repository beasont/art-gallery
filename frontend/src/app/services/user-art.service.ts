import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserArt {
  id: number;
  title: string;
  artist: string;
  artYear: number;  // renamed from 'year'
  imageUrl: string;
}

@Injectable({
  providedIn: 'root',
})
export class UserArtService {
  private baseUrl = 'http://localhost:5443/api/user-art';

  constructor(private http: HttpClient) {}

  getAll(): Observable<UserArt[]> {
    return this.http.get<UserArt[]>(this.baseUrl);
  }

  uploadArt(file: File, title: string, artist: string, artYear: number): Observable<UserArt> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('title', title);
    formData.append('artist', artist);
    // rename the param from 'year' to 'artYear'
    formData.append('artYear', String(artYear));

    return this.http.post<UserArt>(`${this.baseUrl}/upload`, formData);
  }

  filterByArtist(artistName: string): Observable<UserArt[]> {
    return this.http.get<UserArt[]>(`${this.baseUrl}/filterByArtist?artistName=${artistName}`);
  }

  searchArt(query: string): Observable<UserArt[]> {
    return this.http.get<UserArt[]>(`${this.baseUrl}/search?query=${query}`);
  }
}
