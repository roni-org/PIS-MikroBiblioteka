# PIS - MikroBiblioteka

## Zespół
Weronika Maślana
Alesia Filinkova
Diana Pelin

## Terminy
* 25.11 - etap 1
* 16.12 - etap 2
* dowolny przed 20.01 - etap 3
* 20.01 - etap 4

## Zakres projektu
system do archiwizowania plików. Umożliwia wgrywanie plików do bazy danych, przegląd i ściąganie ich przez wszystkich użytkowników. Pliki są przechowywane na oddzielnej bazie danych od metadanych plików - możliwej do postawienia na odrębnym serwerze.

## Uruchomienie
z głównego katalogu zawołaj w terminalu:

    docker-compose build
    docker-compose up

(można dopisać frontend lub backend lub db-postgres by uruchomić tylko dany obraz z dockera)
Program korzysta z biblioteki Material UI, zainstaluj ją komendą:

    npm install @mui/material
    npm install @hello-pangea/dnd
    npm install @angular/core @angular/common @angular/platform-browser @angular/compiler @angular/forms @angular/router @angular/material

### Uruchomienie frontendu lokalnie
cd /frontend
npm start

### Uruchomienie backendu lokalnie
cd /backend
mvn spring-boot:run

### docker compose, tips
docker-compose down -v --remove-orphans

### code coverage (jacoco)
in folder: PIS-MikroBiblioteka/backend/target/site/jacoco/index.html
