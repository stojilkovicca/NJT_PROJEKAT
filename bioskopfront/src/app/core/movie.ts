import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface MovieDto {
  id: number;
  title: string;
  description?: string;
  duration?: number;
  rating?: number;
  imdbRating?: number;
  producer?: string;
  actors?: string;
  genreId?: number;
}

@Injectable({ providedIn: 'root' })
export class MovieService {
  private readonly base = 'http://localhost:8080/api/movie';
  constructor(private http: HttpClient) {}

  getAll()              { return this.http.get<MovieDto[]>(this.base); }
  getById(id: number)   { return this.http.get<MovieDto>(`${this.base}/${id}`); }
  create(dto: Partial<MovieDto>) { return this.http.post<MovieDto>(this.base, dto); }
  update(id: number, dto: Partial<MovieDto>) { return this.http.put<MovieDto>(`${this.base}/${id}`, dto); }
  delete(id: number)    { return this.http.delete(`${this.base}/${id}`, { responseType: 'text' }); }
}
