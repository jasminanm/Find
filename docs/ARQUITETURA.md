# Arquitetura — Find

## Visão geral

```
┌─────────────────┐     REST/JSON      ┌──────────────────────┐
│  Android App    │ ◄────────────────► │  Spring Boot API     │
│  (Compose)      │     JWT Auth       │  (MVC + REST)        │
└────────┬────────┘                    └──────────┬───────────┘
         │                                          │
    Google Maps                               Spring Data JPA
    GPS Location                                      │
         │                                          ▼
         │                               ┌──────────────────────┐
         │                               │     PostgreSQL       │
         └───────────────────────────────┴──────────────────────┘
```

## Backend (camadas MVC)

| Camada | Pacote | Responsabilidade |
|--------|--------|------------------|
| Controller | `controller` | Endpoints REST, validação de entrada |
| Service | `service` | Regras de negócio |
| Repository | `repository` | Acesso à base de dados (JPA) |
| Model | `model` | Entidades JPA |
| DTO | `dto` | Objetos de transferência |
| Security | `security` | JWT, filtros, configuração |

## Android (camadas)

| Camada | Pacote | Responsabilidade |
|--------|--------|------------------|
| UI | `ui/*` | Ecrãs Compose e ViewModels |
| Navigation | `navigation` | Rotas e NavHost |
| Data | `data/api` | Cliente Retrofit |
| Repository | `data/repository` | Abstração de dados |
| Local | `data/local` | Sessão (DataStore) |
| Model | `data/model` | Modelos de domínio |

## Modelo de dados

```
User (1) ──reporta──► (N) Bench
User (1) ──avalia──► (N) Rating ◄── (N) Bench
```

### Entidades

- **User:** email, password (BCrypt), role (USER/ADMIN)
- **Bench:** latitude, longitude, type, color, widthMeters, status
- **Rating:** stars (1-5), user, bench (único por utilizador/banco)

## Casos de uso

### UC1 — Reportar banco
1. Utilizador autenticado abre o mapa
2. Clica em "Reportar banco"
3. App obtém GPS e envia dados ao backend
4. Banco fica com estado `PENDING`

### UC2 — Aprovar banco (Admin)
1. Admin autentica-se
2. Acede ao painel de aprovação
3. Aprova ou rejeita bancos pendentes
4. Bancos aprovados aparecem no mapa público

## Fluxo de autenticação

1. Login/Registo → API devolve JWT
2. Token guardado em DataStore
3. Interceptor Retrofit adiciona `Authorization: Bearer`
4. Spring Security valida JWT em cada pedido
