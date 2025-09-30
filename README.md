# Bioskop App (Spring Boot + Angular) - KRATKO UPUTSTVO

## Opis
Aplikacija za rad bioskopa: pregled filmova i repertoara, rezervacije i karte.
Frontend: Angular SPA. Backend: Spring Boot REST API sa JWT autentikacijom.

## Stack
- Frontend: Angular 17+, TypeScript, Router, HttpClient
- Backend: Spring Boot 3+ (Web, Security, Data JPA)
- Baza: MySQL 8+

## Prerequisites
- Node.js 18+, npm, Angular CLI
- Java 17+, Maven 3.9+
- MySQL 8+ (lokalno ili u Dockeru)

## 1) Backend – pokretanje
1. Napravi bazu:
   CREATE DATABASE njt_bioskop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
2. U src/main/resources/application.properties podesi parametre:
   spring.datasource.url=jdbc:mysql://localhost:3306/njt_bioskop?useSSL=false&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=lozinka
   spring.jpa.hibernate.ddl-auto=update
   app.jwt.secret=change-me
3. Start:
   mvn spring-boot:run
   → API radi na http://localhost:8080

## 2) Frontend – pokretanje
1. cd bioskopfront
2. npm install
3. Proveri src/environments/environment.ts:
   apiBase: 'http://localhost:8080/api'
4. Start dev servera:
         ng serve -o
         app na http://localhost:4200


 
 
 
