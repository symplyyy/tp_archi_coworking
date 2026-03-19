# Design Pattern — Builder

## Pattern choisi : Builder (Créationnel)

## Justification

La création d'une réservation implique plusieurs champs obligatoires et des validations métier à effectuer avant que l'objet soit considéré comme valide :
- `roomId`, `memberId`, `startDateTime` et `endDateTime` sont tous requis
- La date de début doit être antérieure à la date de fin
- Le statut initial est toujours `CONFIRMED`

Sans pattern, la construction se faisait via une série de setters successifs, ce qui ne garantit pas que l'objet est dans un état cohérent au moment de son utilisation.

Le **Builder pattern** permet de centraliser cette logique de construction et de validation dans une seule classe (`ReservationBuilder`), et de garantir qu'aucune `Reservation` invalide ne peut être instanciée.

## Implémentation

`ReservationBuilder` (package `builder`) expose une API fluide :

```java
Reservation reservation = new ReservationBuilder()
        .roomId(request.getRoomId())
        .memberId(request.getMemberId())
        .startDateTime(request.getStartDateTime())
        .endDateTime(request.getEndDateTime())
        .build();
```

La méthode `build()` effectue les validations et lève une `IllegalStateException` si les contraintes ne sont pas respectées, avant même que l'objet soit persisté.

## Alternatives considérées

- **State pattern** : pertinent pour modéliser le cycle de vie CONFIRMED → COMPLETED/CANCELLED, mais le comportement associé à chaque état reste simple (pas de logique complexe liée à l'état).
- **Strategy pattern** : aurait pu s'appliquer aux règles de validation, mais ajoute de la complexité inutile pour ce cas d'usage.
