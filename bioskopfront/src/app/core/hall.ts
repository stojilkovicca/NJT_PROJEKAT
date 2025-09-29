// src/app/core/hall.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface HallDto {
  id: number;
  name: string;
  capacity: number;
  rows?: number | null;
  seatsPerRow?: number | null;
}

export type HallCreate = Omit<HallDto, 'id'>;

 
@Injectable({ providedIn: 'root' })
export class HallService {
  private readonly base = 'http://localhost:8080/api/hall';
  constructor(private http: HttpClient) {}

  getAll() { return this.http.get<HallDto[]>(this.base); }
  create(dto: HallCreate) { return this.http.post<HallDto>(this.base, dto); }

 
  delete(id: number) {
    return this.http.delete(`${this.base}/${id}`, { responseType: 'text' });
  }

  generateSeats(hallId: number, rows: number, seatsPerRow: number) {
    return this.http.post(`${this.base}/${hallId}/generate-seats?rows=${rows}&seatsPerRow=${seatsPerRow}`, {});
  }
}
