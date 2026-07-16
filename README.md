# Find — Bancos de Jardim

Aplicação móvel Android com backend REST para localizar, reportar e avaliar bancos de jardim num mapa.

## Estrutura do repositório

```
Find/
├── app/                    # Aplicação Android (Kotlin + Jetpack Compose)
├── backend/                # API REST (Java + Spring Boot)
├── docs/                   # Documentação do projeto
├── docker-compose.yml      # PostgreSQL
└── README.md
```

## Documentação

| Documento | Descrição |
|-----------|-----------|
| [Relatório (PDF)](docs/RELATORIO.pdf) | Relatório do projeto |
| [REST API](docs/REST_API.md) | Documentação da API REST |
| [Manual do Utilizador](docs/MANUAL_UTILIZADOR.md) | Guia de utilização da app |
| [Arquitetura](docs/ARQUITETURA.md) | Arquitetura da solução |
| [Diagrama de Classes](docs/DIAGRAMA_CLASSES.md) | Diagrama de classes do domínio |
| [Dicionário de Dados](docs/DICIONARIO_DADOS.md) | Dicionário de dados |
| [Guia de Dados](docs/GUIA_DADOS.md) | Guia da BD de exemplo |

## Requisitos

- **Android:** Android Studio Koala+, JDK 11+
- **Backend:** JDK 17+, Maven 3.9+
- **Base de dados:** PostgreSQL 16 (via Docker)
- **Google Maps:** API Key com Maps SDK for Android ativado

## Configuração rápida

### 1. Base de dados

```bash
docker compose up -d
```

### 2. Backend

```bash
cd backend
mvn spring-boot:run
```

API disponível em `http://localhost:8081`  
Swagger UI: `http://localhost:8081/swagger-ui.html`

**Contas de demonstração:**

| Email | Password | Papel |
|-------|----------|-------|
| admin@find.pt | admin123 | Administrador |
| user@find.pt | user123 | Utilizador |

## Funcionalidades

- Ver bancos aprovados no mapa (sem login)
- Mapa centrado na localização atual (GPS)
- Registo e login de utilizadores
- Reportar novos bancos (tipo, cor, largura)
- Administradores aprovam/rejeitam bancos
- Avaliação de bancos (1–5 estrelas)
- Pesquisa por características

## Tecnologias

| Camada | Tecnologias |
|--------|-------------|
| Mobile | Kotlin, Jetpack Compose, Google Maps, Retrofit |
| Backend | Java 17, Spring Boot 3, Spring Security, JWT |
| Base de dados | PostgreSQL, Spring Data JPA |
| API | REST, OpenAPI/Swagger |
