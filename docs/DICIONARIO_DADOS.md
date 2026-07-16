# Dicionário de Dados — Find

Base de dados: **PostgreSQL**. Esquema gerido por JPA (`spring.jpa.hibernate.ddl-auto`).

## Tabela `users`

| Campo | Tipo | Nulo | Chave | Descrição |
|-------|------|------|-------|-----------|
| id | BIGINT | Não | PK | Identificador único |
| email | VARCHAR | Não | Único | Email de autenticação |
| password | VARCHAR | Não | | Password com hash BCrypt |
| role | VARCHAR | Não | | Papel: `USER` ou `ADMIN` |

## Tabela `benches`

| Campo | Tipo | Nulo | Chave | Descrição |
|-------|------|------|-------|-----------|
| id | BIGINT | Não | PK | Identificador único |
| latitude | DOUBLE | Não | | Latitude GPS (-90 a 90) |
| longitude | DOUBLE | Não | | Longitude GPS (-180 a 180) |
| type | VARCHAR | Não | | Tipo: `WOOD`, `CEMENT`, `METAL`, `STONE`, `OTHER` |
| color | VARCHAR | Não | | Cor do banco |
| width_meters | DOUBLE | Não | | Largura em metros (≥ 0.1) |
| status | VARCHAR | Não | | Estado: `PENDING`, `APPROVED`, `REJECTED` |
| reported_by_id | BIGINT | Sim | FK → users.id | Utilizador que reportou |
| created_at | TIMESTAMP | Não | | Data/hora do reporte |

## Tabela `ratings`

| Campo | Tipo | Nulo | Chave | Descrição |
|-------|------|------|-------|-----------|
| id | BIGINT | Não | PK | Identificador único |
| stars | INTEGER | Não | | Avaliação de 1 a 5 |
| bench_id | BIGINT | Não | FK → benches.id | Banco avaliado |
| user_id | BIGINT | Não | FK → users.id | Utilizador que avaliou |
| created_at | TIMESTAMP | Não | | Data/hora da avaliação |

**Restrição:** unique (`bench_id`, `user_id`) — um utilizador só avalia cada banco uma vez.

## Modelo entidade-relação (conceptual)

```
users (1) ──── reporta ──── (N) benches
users (1) ──── avalia ───── (N) ratings (N) ──── pertence a ──── (1) benches
```
