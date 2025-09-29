import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface SeatDto {
  id: number;
  hallId?: number;
  rowNumber: number;
  seatNumber: number;
  label: string;
}

 
@Injectable({ providedIn: 'root' })
export class SeatService {
  private readonly base = 'http://localhost:8080/api/seat';
  constructor(private http: HttpClient) {}

  byHall(hallId: number) {
    return this.http.get<SeatDto[]>(`${this.base}/hall/${hallId}`);
  }

 
  clearHall(hallId: number) {
    return this.http.delete(`${this.base}/hall/${hallId}`, { responseType: 'text' });
  }
}
