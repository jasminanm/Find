# Diagrama de Classes — Find

Diagrama das entidades principais do domínio (backend Java / JPA).

```mermaid
classDiagram
    direction LR

    class User {
        Long id
        String email
        String password
        Role role
    }

    class Bench {
        Long id
        Double latitude
        Double longitude
        BenchType type
        String color
        Double widthMeters
        BenchStatus status
        LocalDateTime createdAt
    }

    class Rating {
        Long id
        Integer stars
        LocalDateTime createdAt
    }

    class Role {
        <<enumeration>>
        USER
        ADMIN
    }

    class BenchType {
        <<enumeration>>
        WOOD
        CEMENT
        METAL
        STONE
        OTHER
    }

    class BenchStatus {
        <<enumeration>>
        PENDING
        APPROVED
        REJECTED
    }

    User "1" --> "*" Bench : reporta
    User "1" --> "*" Rating : avalia
    Bench "1" --> "*" Rating : recebe
    User --> Role
    Bench --> BenchType
    Bench --> BenchStatus
```

## Descrição das relações

| Relação | Cardinalidade | Descrição |
|---------|---------------|-----------|
| User → Bench | 1:N | Um utilizador pode reportar vários bancos |
| User → Rating | 1:N | Um utilizador pode avaliar vários bancos |
| Bench → Rating | 1:N | Um banco pode ter várias avaliações |
| User + Bench → Rating | único | Um utilizador só pode avaliar o mesmo banco uma vez |

## Pacotes relacionados

- Entidades: `pt.iade.find.model`
- Repositórios: `pt.iade.find.repository`
- Serviços: `pt.iade.find.service`
- Controllers: `pt.iade.find.controller`
