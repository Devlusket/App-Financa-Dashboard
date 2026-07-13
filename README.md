# Finança API

Backend do controle financeiro doméstico por casa. A API usa Java 21, Spring Boot, PostgreSQL, Flyway e JWT.

## Rodar localmente

Pré-requisitos: Java 21, Maven e Docker.

```bash
docker compose up -d postgres
mvn -Dmaven.repo.local=/tmp/financa-m2 spring-boot:run
```

A API inicia em `http://localhost:8080`. O health check público é `GET /actuator/health` e a documentação interativa é `http://localhost:8080/swagger-ui.html`.

Para encerrar o banco local:

```bash
docker compose stop postgres
```

## Variáveis de ambiente

| Variável | Padrão local | Descrição |
| --- | --- | --- |
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/financa` | URL JDBC do PostgreSQL/Neon |
| `DATABASE_USERNAME` | `financa` | Usuário do banco |
| `DATABASE_PASSWORD` | `financa` | Senha do banco |
| `JWT_SECRET` | chave local de desenvolvimento | Segredo JWT; em produção use valor secreto com no mínimo 32 bytes |
| `JWT_EXPIRATION_MS` | `86400000` | Validade do token em milissegundos |
| `SERVER_PORT` | `8080` | Porta HTTP |

Exemplo com Neon:

```bash
export DATABASE_URL='jdbc:postgresql://host.neon.tech/neondb?sslmode=require'
export DATABASE_USERNAME='usuario'
export DATABASE_PASSWORD='senha'
export JWT_SECRET='um-segredo-de-producao-com-no-minimo-32-bytes'
```

## Banco e migrations

O Flyway é executado automaticamente ao subir a aplicação. As migrations estão em `src/main/resources/db/migration` e criam o schema V1–V7. O Hibernate opera com `ddl-auto=validate`, portanto não altera tabelas.

## Deploy no Render

O repositório inclui `render.yaml` e `Dockerfile`. No Render, crie um Blueprint a partir do repositório e informe os valores secretos solicitados para `DATABASE_URL`, `DATABASE_USERNAME` e `DATABASE_PASSWORD` (credenciais Neon). `JWT_SECRET` é gerado automaticamente pelo Blueprint.

`DATABASE_URL` deve ser uma URL JDBC, por exemplo:

```text
jdbc:postgresql://host.neon.tech/neondb?sslmode=require
```

O serviço escuta automaticamente a variável `PORT` fornecida pelo Render, executa Flyway na inicialização e usa `/actuator/health` como health check. Após o primeiro deploy, confirme no log que V1–V7 foram aplicadas e teste `/swagger-ui.html`, registro, login e uma rota autenticada.

## Autenticação

Crie a conta da casa com `POST /auth/registrar` e use `POST /auth/login` para receber o token. Envie-o nas demais rotas:

```http
Authorization: Bearer <token>
```

O login é único por casa; não há perfis ou roles. Cada operação de dados é filtrada pelo `casaId` presente no JWT.

## Decisões de domínio

- Percentuais são vinculados diretamente a pessoas pela tabela `categoria_divisao`; não dependem da ordem de cadastro.
- Contas fixas são somente referência e nunca geram lançamentos automaticamente.
- `GET /lancamentos?pessoaId=` filtra quem pagou fisicamente (`responsavelPagamento`). O relatório mensal calcula responsabilidade financeira pela regra da categoria.
- Relatórios incluem lançamentos PAGO e PENDENTE. Poupança soma apenas em `guardado` e não aparece em `gastosPorCategoria`.
- Não há cálculo de dívida ou saldo entre pessoas.
