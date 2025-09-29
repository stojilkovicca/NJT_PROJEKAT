import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ReservationCreateDto {
  numberOfTickets: number;
  userId: number;       
  projectionId: number;
}

export interface ReservationDto extends ReservationCreateDto {
  id: number;
  reservedAt?: string;
}

@Injectable({ providedIn: 'root' })
export class ReservationService {
  private readonly base = 'http://localhost:8080/api/reservation';
  constructor(private http: HttpClient) {}
  getAll(){ return this.http.get<ReservationDto[]>(this.base); }
  delete(id: number){ return this.http.delete(`${this.base}/${id}`, { responseType:'text' }); }
  create(payload: ReservationCreateDto): Observable<ReservationDto> {
    return this.http.post<ReservationDto>(this.base, payload);
  }
}
