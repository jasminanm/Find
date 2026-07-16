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

### UC1 — Consultar bancos no mapa (Visitante)

**Ator:** Visitante (sem conta)  
**Pré-condição:** A aplicação e a API estão disponíveis; existem bancos com estado `APPROVED`.  
**Objetivo:** Localizar bancos de jardim próximos e consultar as suas características.

**Fluxo principal:**
1. O visitante abre a aplicação.
2. A aplicação solicita permissão de localização (se ainda não existir).
3. O mapa centra-se na localização GPS atual.
4. A aplicação pede à API a lista de bancos aprovados e mostra-os como marcadores.
5. O visitante toca num marcador e consulta o detalhe (tipo, cor, largura e classificação média).
6. Opcionalmente, usa a pesquisa para filtrar por tipo, cor e/ou largura.

**Pós-condição:** O visitante visualizou a informação pública sem necessidade de autenticação.

---

### UC2 — Reportar um novo banco (Utilizador registado)

**Ator:** Utilizador autenticado (`USER`)  
**Pré-condição:** O utilizador possui conta válida e sessão ativa (JWT).  
**Objetivo:** Submeter um novo banco de jardim para validação.

**Fluxo principal:**
1. O utilizador autentica-se com email e palavra-passe.
2. No mapa, toca no botão de reportar (canto inferior direito).
3. A aplicação obtém a localização GPS atual.
4. O utilizador indica tipo (ex.: madeira, cimento), cor e largura.
5. Submete o formulário; a API cria o banco com estado `PENDING`.
6. O utilizador recebe confirmação de que o reporte foi enviado e aguarda aprovação.

**Pós-condição:** O banco fica registado na base de dados como pendente e ainda não aparece no mapa público.

---

### UC3 — Aprovar banco reportado (Administrador)

**Ator:** Administrador (`ADMIN`)  
**Pré-condição:** Existem bancos com estado `PENDING`.  
**Objetivo:** Validar reportes e torná-los visíveis a todos.

**Fluxo principal:**
1. O administrador autentica-se com uma conta `ADMIN`.
2. Acede ao painel de administração.
3. Consulta a lista de bancos pendentes.
4. Analisa os dados e aprova (ou rejeita) o reporte.
5. Se aprovado, o estado passa a `APPROVED` e o banco torna-se visível no mapa para todos os utilizadores.

**Pós-condição:** O banco aprovado está disponível na consulta pública (UC1).

## Fluxo de autenticação

1. Login/Registo → API devolve JWT
2. Token guardado em DataStore
3. Interceptor Retrofit adiciona `Authorization: Bearer`
4. Spring Security valida JWT em cada pedido
