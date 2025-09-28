import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface ProjectionDto {
  id: number;
  dateTime: string;      
  basePrice: number;
  hallId: number;
  movieId: number;
}

@Injectable({ providedIn: 'root' })
export class ProjectionService {
  private readonly base = 'http://localhost:8080/api/projection';
  constructor(private http: HttpClient) {}

  getAll()                    { return this.http.get<ProjectionDto[]>(this.base); }
  getByMovie(movieId: number) { return this.http.get<ProjectionDto[]>(`${this.base}/movie/${movieId}`); }
  create(dto: Partial<ProjectionDto>) { return this.http.post<ProjectionDto>(this.base, dto); }
  delete(id: number)          { return this.http.delete(`${this.base}/${id}`, { responseType: 'text' }); }
}
