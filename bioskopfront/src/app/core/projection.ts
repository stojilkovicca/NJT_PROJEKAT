import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProjectionDto {
  id: number;
  movieId: number;
  hallId: number;
  dateTime: string;    
  basePrice: number;   
}

@Injectable({ providedIn: 'root' })
export class ProjectionService {
  private readonly base = 'http://localhost:8080/api/projection';
  constructor(private http: HttpClient) {}

  getByMovie(movieId: number): Observable<ProjectionDto[]> {
    return this.http.get<ProjectionDto[]>(`${this.base}/movie/${movieId}`);
    //   kontroler ima GET /api/projection/movie/{movieId}
  }
}
