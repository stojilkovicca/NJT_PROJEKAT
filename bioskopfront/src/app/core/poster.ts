import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, catchError, of, switchMap } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PosterService {
  private tmdbKey = environment.tmdbKey;
  private omdbKey = environment.omdbKey;

  constructor(private http: HttpClient) {}

  private fromTMDb(title: string) {
    const params = new HttpParams().set('api_key', this.tmdbKey).set('query', title);
    return this.http.get<any>('https://api.themoviedb.org/3/search/movie', { params }).pipe(
      map(res => {
        const path = res?.results?.[0]?.poster_path as string | undefined;
        return path ? `https://image.tmdb.org/t/p/w500${path}` : null;
      }),
      catchError(() => of(null))
    );
  }

  private fromOMDb(title: string) {
    const params = new HttpParams().set('apikey', this.omdbKey).set('t', title);
    return this.http.get<any>('https://www.omdbapi.com/', { params }).pipe(
      map(res => (res?.Poster && res.Poster !== 'N/A' ? (res.Poster as string) : null)),
      catchError(() => of(null))
    );
  }

  /** Pokuša TMDb; ako nema – fallback na OMDb. Vrati pun URL ili null. */
  getPoster(title: string) {
    return this.fromTMDb(title).pipe(
      switchMap(url => (url ? of(url) : this.fromOMDb(title)))
    );
  }
}
