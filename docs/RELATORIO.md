# Relatório — Find (Bancos de Jardim)

> **Universidade Europeia — IADE**  
> Projeto de Desenvolvimento de Software

## 1. Identificação

| Campo | Valor |
|-------|-------|
| Universidade | Universidade Europeia |
| Faculdade | IADE |
| Grupo | |
| Projeto | Find — Bancos de Jardim |
| GitHub | https://github.com/jasminanm/Find |

## 2. Resumo e palavras-chave

### Resumo

O projeto **Find** consiste numa aplicação móvel Android para localizar, reportar e avaliar bancos de jardim em espaços públicos. A solução combina uma interface baseada em mapa com GPS, uma API REST desenvolvida em Java com Spring Boot e uma base de dados PostgreSQL. Utilizadores não autenticados podem consultar bancos já aprovados; utilizadores registados podem reportar novos bancos e atribuir avaliações de 1 a 5 estrelas; administradores validam os reportes antes de estes ficarem visíveis no mapa. O sistema foi organizado em camadas (apresentação, serviços, acesso a dados e segurança com JWT), com dados de demonstração e documentação técnica no repositório GitHub.

### Palavras-chave

Aplicação móvel; Android; Kotlin; Jetpack Compose; Spring Boot; API REST; PostgreSQL; GPS; mapas; mobiliário urbano; autenticação JWT; crowdsourcing.

## 3. Tabela de tarefas

| Tarefa | % Concluído |
|--------|-------------|
| Backend Spring Boot + REST API | 100% |
| Base de dados PostgreSQL | 100% |
| App Android (mapa, GPS) | 100% |
| Autenticação e perfis | 100% |
| Reportar e aprovar bancos | 100% |
| Avaliações e pesquisa | 100% |
| Documentação | 80% |
| Relatório PDF + Poster | 0% |

## 4. Descrição da App

Find permite localizar bancos de jardim num mapa interativo. Utilizadores podem reportar novos bancos e avaliá-los; administradores validam os reportes antes de ficarem visíveis publicamente.

## 5. Objetivos

Facilitar a descoberta de bancos públicos com informação sobre tipo, cor e largura, contribuindo para espaços urbanos mais acessíveis.

## 6. Público-alvo

- Cidadãos que procuram lugares para sentar em espaços públicos
- Administradores municipais ou voluntários que validam dados

## 7. Pesquisa de mercado

Apps como Google Maps mostram mobiliário urbano de forma genérica. Find foca-se especificamente em bancos de jardim com metadados detalhados e validação comunitária.

## 8. Descrição da solução

### Arquitetura
Ver [ARQUITETURA.md](ARQUITETURA.md)

### Tecnologias
- **Mobile:** Kotlin, Jetpack Compose, Google Maps, Retrofit
- **Backend:** Java 17, Spring Boot 3, Spring Security, JWT
- **BD:** PostgreSQL

### Casos de uso
Ver secção "Casos de uso" em [ARQUITETURA.md](ARQUITETURA.md)

### Documentação REST
Ver [REST_API.md](REST_API.md)

### Manual do utilizador
Ver [MANUAL_UTILIZADOR.md](MANUAL_UTILIZADOR.md)

## 9. Autoavaliação

A solução cumpre os requisitos obrigatórios: mapa com GPS, base de dados relacional, REST API, perfis de utilizador e administrador, reporte, aprovação, avaliação e pesquisa. A documentação está estruturada no GitHub; falta gerar o PDF final e o poster.

## 10. Bibliografia

- Spring Boot Documentation — https://spring.io/projects/spring-boot
- Android Jetpack Compose — https://developer.android.com/jetpack/compose
- Google Maps Platform — https://developers.google.com/maps
- PostgreSQL Documentation — https://www.postgresql.org/docs/
