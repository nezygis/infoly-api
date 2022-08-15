# Infoly

# Pre-requisites

* Docker
* Java 11

# Getting Started
**Building docker image:**
```
./gradlew bootBuildImage
```
**Starting services:**
```
docker-compose up -d
```

# REST API

Rest API to search for favorite artists and save them

## Get artists

### Request

`GET /artists?term={search_term}`

    curl -i -H 'Accept: application/json' "http://localhost:8080/artists?term=king%20crimson"

### Response


    HTTP/1.1 200 OK
    Content-Type: application/json
    Content-Length: 225

    {"resultCount":4,"results":[{"artistName":"King Crimson","amgArtistId":4682},{"artistName":"Crimson Kings","amgArtistId":0},{"artistName":"The Crimson King","amgArtistId":0},{"artistName":"The Crimson King","amgArtistId":0}]}

## Get artists top 5 albums

### Request

`GET /artists/{id}`

    curl -i -H 'Accept: application/json' "http://localhost:8080/artists/4682"

### Response

    HTTP/1.1 200 OK
    Content-Type: application/json
    Content-Length: 460

    {"resultCount":5,"results":[{"wrapperType":"collection","collectionName":"In the Court of the Crimson King (Expanded Edition)"},{"wrapperType":"collection","collectionName":"Red (Expanded Edition)"},{"wrapperType":"collection","collectionName":"Discipline (Expanded Edition)"},{"wrapperType":"collection","collectionName":"Larks' Tongues In Aspic (Expanded Edition)"},{"wrapperType":"collection","collectionName":"In the Wake of Poseidon (Expanded Edition)"}]}

## Get favorite artists

### Request

`GET /favorite-artists/`

    curl -i -H 'Accept: application/json' -H 'user-id: test' http://localhost:8080/favorite-artists/

### Response

    HTTP/1.1 200 OK
    Content-Type: application/json
    Content-Length: 20

    {"favorites":[1,12]}

## Add favorite artist

### Request

`POST /favorite-artists/`

    curl -i -H "Content-Type: application/json" -H 'Accept: application/json' -H 'user-id: test' -d  '{"id":1}' http://localhost:8080/favorite-artists

### Response

    HTTP/1.1 200 OK
    Content-Type: application/json
    Content-Length: 17

    {"favorites":[1]}

## Remove favorite artist

### Request

`DELETE /favorite-artists/id`

    curl -i -H 'Accept: application/json' -H 'user-id: test' -d  '{"id":1}' -X DELETE http://localhost:8080/favorite-artists/1/

### Response

    HTTP/1.1 202 Accepted
    Content-Type: application/json
    Content-Length: 32

    {"userId":"test","artistIds":[]} 