# Guia de Dados — Find

Documentação da base de dados de exemplo usada para demonstração.

## Contas de demonstração

| Email | Password | Papel |
|-------|----------|-------|
| admin@find.pt | admin123 | ADMIN |
| user@find.pt | user123 | USER |

## Dados iniciais (seed)

Quando a base está vazia, o backend (`DataInitializer`) cria:

| Latitude | Longitude | Tipo | Cor | Largura (m) | Estado |
|----------|-----------|------|-----|-------------|--------|
| 38.7223 | -9.1393 | WOOD | Castanho | 1.8 | APPROVED |
| 38.7250 | -9.1500 | CEMENT | Cinza | 2.0 | APPROVED |
| 38.7300 | -9.1450 | METAL | Preto | 1.5 | PENDING |
| 38.7165 | -9.1480 | WOOD | Verde | 1.6 | APPROVED |
| 38.7362 | -9.1425 | STONE | Bege | 2.2 | APPROVED |
| 38.7108 | -9.1335 | CEMENT | Branco | 1.9 | APPROVED |
| 38.7285 | -9.1560 | METAL | Azul | 1.4 | APPROVED |
| 38.7410 | -9.1378 | WOOD | Vermelho | 2.1 | APPROVED |

Os bancos aprovados aparecem no mapa público. O banco `PENDING` só é visível no painel de administração.

## Como criar / reiniciar a BD

```bash
# Na raiz do projeto
docker compose up -d
cd backend
mvn spring-boot:run
```

A criação das tabelas e a inserção dos dados de teste ocorrem automaticamente ao arrancar a API.

## Consultas úteis

```sql
-- Bancos aprovados (visíveis no mapa)
SELECT id, latitude, longitude, type, color, width_meters
FROM benches
WHERE status = 'APPROVED';

-- Bancos pendentes de aprovação
SELECT id, type, color, reported_by_id, created_at
FROM benches
WHERE status = 'PENDING';

-- Média de avaliações por banco
SELECT b.id, b.color, AVG(r.stars) AS media, COUNT(r.id) AS total
FROM benches b
LEFT JOIN ratings r ON r.bench_id = b.id
WHERE b.status = 'APPROVED'
GROUP BY b.id, b.color;

-- Utilizadores e papéis
SELECT id, email, role FROM users;
```

## Ligação da aplicação

| Serviço | Host / porta |
|---------|--------------|
| PostgreSQL (Docker) | `localhost:5435` |
| API REST | `http://localhost:8081` |
