# Plateforme de réservation de salles de coworking — Microservices

Architecture microservices Spring Boot / Spring Cloud pour la gestion de salles de coworking, membres et réservations.

---

## Architecture

```
                       API Gateway (8080)
                            │
          ┌─────────────────┼──────────────────┐
          │                 │                  │
   Room Service (8081)  Member Service (8082)  Reservation Service (8083)
          │                 │                  │
          └─────────────────┼──────────────────┘
                            │
                       Apache Kafka
                            │
                  Discovery Server (8761)
                  Config Server (8888)
```

---

## Prérequis

- Java 17+
- Maven 3.8+
- Docker (optionnel, pour Kafka)

---

## Lancement

### 1. Démarrer Kafka (optionnel — nécessaire pour les events asynchrones)

```bash
docker run -d --name kafka -p 9092:9092 \
  -e KAFKA_ENABLE_KRAFT=yes \
  -e KAFKA_PROCESS_ROLES=broker,controller \
  -e KAFKA_NODE_ID=1 \
  -e KAFKA_LISTENERS=PLAINTEXT://:9092,CONTROLLER://:9093 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_CONTROLLER_QUORUM_VOTERS=1@localhost:9093 \
  -e KAFKA_CONTROLLER_LISTENER_NAMES=CONTROLLER \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  apache/kafka:3.8.0
```

### 2. Lancer les services dans l'ordre

Chaque service se lance avec :
```bash
cd <nom-du-service>
mvn spring-boot:run
```

**Ordre obligatoire :**

| Ordre | Service | Port |
|-------|---------|------|
| 1 | `config-server` | 8888 |
| 2 | `discovery-server` | 8761 |
| 3 | `room-service` | 8081 |
| 4 | `member-service` | 8082 |
| 5 | `reservation-service` | 8083 |
| 6 | `api-gateway` | 8080 |

---

## Vérification

- **Eureka** : http://localhost:8761 — les 3 services doivent apparaître comme UP
- **Swagger Room Service** : http://localhost:8081/swagger-ui/index.html
- **Swagger Member Service** : http://localhost:8082/swagger-ui/index.html
- **Swagger Reservation Service** : http://localhost:8083/swagger-ui/index.html

---

## Endpoints principaux

### Room Service — `http://localhost:8081`

| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/rooms` | Lister toutes les salles |
| GET | `/rooms/{id}` | Récupérer une salle |
| POST | `/rooms` | Créer une salle |
| PUT | `/rooms/{id}` | Modifier une salle |
| DELETE | `/rooms/{id}` | Supprimer une salle |
| GET | `/rooms/available` | Salles disponibles |

### Member Service — `http://localhost:8082`

| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/members` | Lister tous les membres |
| GET | `/members/{id}` | Récupérer un membre |
| POST | `/members` | Créer un membre |
| PUT | `/members/{id}` | Modifier un membre |
| DELETE | `/members/{id}` | Supprimer un membre |
| PATCH | `/members/{id}/suspension` | Mettre à jour la suspension |

### Reservation Service — `http://localhost:8083`

| Méthode | URL | Description |
|---------|-----|-------------|
| GET | `/reservations` | Lister toutes les réservations |
| POST | `/reservations` | Créer une réservation |
| PATCH | `/reservations/{id}/cancel` | Annuler une réservation |
| PATCH | `/reservations/{id}/complete` | Compléter une réservation |
| GET | `/reservations/member/{id}` | Réservations d'un membre |
| GET | `/reservations/room/{id}` | Réservations d'une salle |

---

## Exemples de requêtes

### Créer une salle
```json
POST http://localhost:8081/rooms
{
    "name": "Salle Alpha",
    "city": "Paris",
    "capacity": 8,
    "type": "MEETING_ROOM",
    "hourlyRate": 30.00
}
```

Types disponibles : `OPEN_SPACE`, `MEETING_ROOM`, `PRIVATE_OFFICE`

### Créer un membre
```json
POST http://localhost:8082/members
{
    "fullName": "Alice Martin",
    "email": "alice@test.fr",
    "subscriptionType": "BASIC"
}
```

Abonnements : `BASIC` (2 réservations max), `PRO` (5 max), `ENTERPRISE` (10 max)

### Créer une réservation
```json
POST http://localhost:8083/reservations
{
    "roomId": 1,
    "memberId": 1,
    "startDateTime": "2026-04-01T09:00:00",
    "endDateTime": "2026-04-01T11:00:00"
}
```

---

## Règles métier

- Une salle ne peut avoir qu'une seule réservation sur un créneau donné
- Un membre suspendu ne peut plus réserver
- La suspension est automatique quand le quota de réservations actives est atteint
- La suppression d'une salle annule toutes ses réservations CONFIRMED (via Kafka)
- La suppression d'un membre supprime toutes ses réservations (via Kafka)
