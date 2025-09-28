
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface GenreDto { id: number; name: string; }

@Injectable({ providedIn: 'root' })
export class GenreService {
  private readonly base = 'http://localhost:8080/api/genre';
  constructor(private http: HttpClient) {}
  getAll() { return this.http.get<GenreDto[]>(this.base); }
}
