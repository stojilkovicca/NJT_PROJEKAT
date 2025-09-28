import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface MovieDto {
  id: number;
  title: string;
  description?: string;
  duration?: number;   // u minutima
  rating?: number;     // interna ocena (ako postoji)
  imdbRating?: number; // iz backend-a
  producer?: string;
  actors?: string;
  genreId?: number;    // samo ID žanra
}

@Injectable({ providedIn: 'root' })
export class MovieService {
 
  private readonly base = 'http://localhost:8080/api/movie';

  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<MovieDto[]>(this.base);
  }

  getById(id: number) {
    return this.http.get<MovieDto>(`${this.base}/${id}`);
  }
}
