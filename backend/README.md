## Backend — Find API

### Pré-requisitos

- JDK 17+
- Maven 3.9+ (ou use `./mvnw` se disponível)
- PostgreSQL em execução (`docker compose up -d` na raiz do projeto)

### Executar

```bash
cd backend
mvn spring-boot:run
```

### Endpoints principais

- Swagger: http://localhost:8080/swagger-ui.html
- API docs: http://localhost:8080/api-docs

### Estrutura

```
src/main/java/pt/iade/find/
├── controller/     # REST controllers
├── service/        # Lógica de negócio
├── repository/     # JPA repositories
├── model/          # Entidades
├── dto/            # Request/Response
├── security/       # JWT + Spring Security
├── config/         # Inicialização de dados
└── exception/      # Tratamento de erros
```
