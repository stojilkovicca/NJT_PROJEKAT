import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

export interface TicketDto {
  id: number;
  ticketPrice: number;
  qrCode?: string | null;
  projectionId: number;
  seatId: number;
  reservationId: number;
}

// za POST:
export type TicketCreate = Omit<TicketDto, 'id'>;

@Injectable({ providedIn: 'root' })
export class TicketService {
  private readonly base = 'http://localhost:8080/api/ticket';
  constructor(private http: HttpClient) {}

  add(dto: TicketCreate) {                      
    return this.http.post<TicketDto>(this.base, dto);
  }

  byProjection(projectionId: number) {
    return this.http.get<TicketDto[]>(`${this.base}/projection/${projectionId}`);
  }

}
