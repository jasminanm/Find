# Documentação REST API — Find

Base URL: `http://localhost:8080`

Autenticação: header `Authorization: Bearer <token>` (exceto endpoints públicos).

## Autenticação

### POST /api/auth/register

Regista um novo utilizador.

**Request body:**
```json
{
  "email": "novo@email.pt",
  "password": "password123"
}
```

**Response 201:**
```json
{
  "token": "eyJhbG...",
  "userId": 3,
  "email": "novo@email.pt",
  "role": "USER"
}
```

### POST /api/auth/login

Autentica um utilizador existente.

**Request body:**
```json
{
  "email": "user@find.pt",
  "password": "user123"
}
```

**Response 200:** igual ao registo.

---

## Bancos (público para leitura)

### GET /api/benches

Lista bancos **aprovados**. Suporta filtros opcionais.

**Query params:** `type`, `color`, `minWidth`, `maxWidth`

**Exemplo:** `GET /api/benches?type=WOOD&color=castanho&minWidth=1.5`

**Response 200:**
```json
[
  {
    "id": 1,
    "latitude": 38.7223,
    "longitude": -9.1393,
    "type": "WOOD",
    "color": "Castanho",
    "widthMeters": 1.8,
    "status": "APPROVED",
    "reportedByEmail": "user@find.pt",
    "createdAt": "2026-07-15T10:00:00",
    "averageRating": 4.5,
    "ratingCount": 2
  }
]
```

### GET /api/benches/{id}

Obtém detalhe de um banco.

### POST /api/benches 🔒

Reporta um novo banco (fica `PENDING`).

**Request body:**
```json
{
  "latitude": 38.7223,
  "longitude": -9.1393,
  "type": "CEMENT",
  "color": "Cinza",
  "widthMeters": 2.0
}
```

---

## Avaliações

### GET /api/benches/{benchId}/ratings

Lista avaliações de um banco.

### POST /api/benches/{benchId}/ratings 🔒

Avalia um banco aprovado (1–5 estrelas).

**Request body:**
```json
{
  "stars": 4
}
```

---

## Administração 🔒 ADMIN

### GET /api/admin/benches/pending

Lista bancos pendentes de aprovação.

### PUT /api/admin/benches/{id}/approve

Aprova um banco.

### PUT /api/admin/benches/{id}/reject

Rejeita um banco.

---

## Códigos de erro

| Código | Descrição |
|--------|-----------|
| 400 | Dados inválidos ou regra de negócio violada |
| 401 | Não autenticado |
| 403 | Sem permissão (ex: não é admin) |
| 404 | Recurso não encontrado |

**Formato de erro:**
```json
{
  "error": "Mensagem descritiva"
}
```

## Swagger

Documentação interativa: `http://localhost:8080/swagger-ui.html`
