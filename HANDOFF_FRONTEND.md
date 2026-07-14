# Handoff — Frontend do Finança Dashboard

Este documento resume o backend concluído e as decisões de produto tomadas durante a implementação. Use-o como contexto ao iniciar o frontend.

## Estado atual

- Backend Java 21 + Spring Boot + PostgreSQL/Neon + Flyway + JWT concluído.
- Produção: `https://app-financa-dashboard.onrender.com`
- Saúde pública: `GET /actuator/health`
- Swagger: `https://app-financa-dashboard.onrender.com/swagger-ui.html`
- API OpenAPI: `https://app-financa-dashboard.onrender.com/v3/api-docs`
- Não versionar senhas, JWTs ou URLs de banco com credenciais. A senha Neon usada durante a configuração deve ser rotacionada.

## Integração do frontend

- Definir uma variável de ambiente, por exemplo `VITE_API_URL=https://app-financa-dashboard.onrender.com`.
- Todos os endpoints, exceto `/auth/**`, exigem `Authorization: Bearer <token>`.
- Após login/registro, persistir apenas o `token` retornado e redirecionar à área autenticada.
- CORS já libera `http://localhost:3000`. Para publicar em outro domínio, configurar no Render `CORS_ALLOWED_ORIGINS` com a origem exata do frontend (se houver mais de uma, separar por vírgulas).
- Formato de erros: `{ timestamp, status, erro, mensagem, path }`. Exibir `mensagem` ao usuário.
- Valores monetários chegam como número JSON (`BigDecimal`). Formatar no cliente com `Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' })`; não usar `float` para cálculos de negócio no frontend.
- Meses usam obrigatoriamente o formato `YYYY-MM` (ex.: `2026-07`). Datas de lançamento usam `YYYY-MM-DD`.

## Fluxo e telas sugeridas

1. **Registro/login**: criar uma casa no primeiro acesso, depois autenticar por usuário e senha.
2. **Dashboard mensal**: seletor de mês, cards de renda/gasto/guardado/saldo, resumo por pessoa e gráfico por categoria.
3. **Pessoas**: listar, criar e remover integrantes da casa.
4. **Categorias**: criar/editar/remover categorias e configurar a regra de responsabilidade financeira.
5. **Lançamentos**: listar por mês, filtrar, criar, editar, excluir e alterar status.
6. **Rendas**: consultar/cadastrar renda mensal por pessoa e adicionar/remover rendas extras.
7. **Contas fixas**: cadastro de referências recorrentes; elas não criam lançamentos automaticamente.

Para uma primeira experiência útil, após registro, orientar o usuário a cadastrar pessoas, categorias e a renda/lançamentos do mês.

## Contrato da API

### Autenticação (público)

| Método | Rota | Corpo | Retorno |
| --- | --- | --- | --- |
| POST | `/auth/registrar` | `{ usuario, senha, nome? }` | `{ token }` (201) |
| POST | `/auth/login` | `{ usuario, senha }` | `{ token }` |

O login é único por casa: não há perfis, papéis ou seleção de casa no frontend.

### Pessoas

| Método | Rota | Corpo |
| --- | --- | --- |
| GET | `/pessoas` | — |
| POST | `/pessoas` | `{ nome }` |
| DELETE | `/pessoas/{id}` | — |

Resposta: `{ id, nome }`.

### Categorias

| Método | Rota | Corpo |
| --- | --- | --- |
| GET | `/categorias` | — |
| POST | `/categorias` | objeto abaixo |
| PATCH | `/categorias/{id}` | campos que serão alterados |
| DELETE | `/categorias/{id}` | — |

```json
{
  "nome": "Aluguel",
  "tipoDivisao": "PERCENTUAL",
  "responsavelId": null,
  "ehPoupanca": false,
  "divisoesPercentuais": [
    { "pessoaId": "uuid", "percentual": 50 },
    { "pessoaId": "uuid", "percentual": 50 }
  ]
}
```

`tipoDivisao` aceita:

- `FIXO_POR_PESSOA`: responsabilidade integral da pessoa em `responsavelId`.
- `PERCENTUAL`: usar `divisoesPercentuais`; cada percentual é ligado explicitamente ao `pessoaId`, sem depender da ordem de cadastro.
- `VALOR_FIXO_DIVIDIDO`: divide igualmente entre as pessoas cadastradas.

`ehPoupanca=true` identifica lançamentos como dinheiro guardado, não gasto comum. A tela deve oferecer essa escolha de forma clara.

### Lançamentos

| Método | Rota | Corpo |
| --- | --- | --- |
| GET | `/lancamentos?mes=YYYY-MM&pessoaId=&categoriaId=` | — |
| POST | `/lancamentos` | objeto abaixo |
| PATCH | `/lancamentos/{id}` | qualquer subconjunto dos campos editáveis |
| PATCH | `/lancamentos/{id}/status` | `{ status, responsavelPagamentoId? }` |
| DELETE | `/lancamentos/{id}` | — |

```json
{
  "categoriaId": "uuid",
  "descricao": "Aluguel julho",
  "valor": 900.00,
  "data": "2026-07-05",
  "responsavelPagamentoId": "uuid",
  "status": "PENDENTE"
}
```

`status` aceita `PAGO` ou `PENDENTE`. A resposta inclui `mesReferencia`, `categoriaNome` e os demais campos enviados.

**Decisão importante:** o filtro `pessoaId` em `GET /lancamentos` significa **quem pagou fisicamente** (`responsavelPagamentoId`). Não é o cálculo de responsabilidade financeira e não deve duplicar lançamentos 50/50 na lista.

### Rendas

| Método | Rota | Corpo |
| --- | --- | --- |
| GET | `/rendas?pessoaId={uuid}&mes=YYYY-MM` | — |
| POST | `/rendas` | `{ pessoaId, mesReferencia, valorFixo }` |
| PATCH | `/rendas/{id}` | `{ valorFixo }` |
| POST | `/rendas/{id}/adicionais` | `{ descricao, valor }` |
| DELETE | `/rendas/adicionais/{id}` | — |

Ao consultar uma renda inexistente, a API retorna `existe: false` e `valorFixoSugerido` com a renda fixa do mês anterior da mesma pessoa (ou `null`). O frontend pode oferecer o botão “Usar valor de mês anterior”, mas o `POST /rendas` sempre exige `valorFixo`; a sugestão nunca deve ser enviada automaticamente sem confirmação do usuário.

Quando existe, a resposta contém `{ existe: true, id, pessoaId, mesReferencia, valorFixo, adicionais, valorFixoSugerido }`.

### Contas fixas

| Método | Rota | Corpo |
| --- | --- | --- |
| GET | `/contas-fixas` | — |
| POST | `/contas-fixas` | `{ categoriaId, nome, valorAtual }` |
| PATCH | `/contas-fixas/{id}` | `{ valorAtual }` |
| DELETE | `/contas-fixas/{id}` | — |

Contas fixas são referências para a UI. Não devem gerar ou alterar lançamentos automaticamente.

### Relatório mensal / dashboard

`GET /relatorios/mensal?mes=YYYY-MM`

```json
{
  "mesReferencia": "2026-07",
  "porPessoa": [
    {
      "pessoaId": "uuid",
      "nome": "Lucas",
      "renda": 0,
      "gasto": 0,
      "guardado": 0,
      "saldo": 0,
      "gastosPorCategoria": [
        { "categoriaId": "uuid", "nome": "Mercado", "total": 0 }
      ],
      "guardadoPorCategoria": [
        { "categoriaId": "uuid", "nome": "Investimento", "total": 0 }
      ]
    }
  ],
  "casa": { "renda": 0, "gasto": 0, "guardado": 0, "saldo": 0 },
  "gastosPorCategoria": [
    { "categoriaId": "uuid", "nome": "Mercado", "total": 0 }
  ],
  "guardadoPorCategoria": [
    { "categoriaId": "uuid", "nome": "Investimento", "total": 0 }
  ]
}
```

Regras visuais e de domínio:

- Totais incluem lançamentos `PAGO` e `PENDENTE`: representam o valor já comprometido no mês, não somente o fluxo de caixa pago.
- `saldo = renda - gasto - guardado`.
- O cálculo `porPessoa` segue a regra de divisão da categoria, não quem pagou fisicamente.
- `gastosPorCategoria` exclui categorias de poupança; poupança já é exibida em `guardado`, evitando dupla contagem.
- Cada item de `porPessoa` inclui `gastosPorCategoria` e `guardadoPorCategoria`, ambos já calculados conforme a regra financeira da categoria.
- O `guardadoPorCategoria` do nível raiz consolida os lançamentos de poupança da casa pelo valor total lançado.
- Não existe cálculo de dívida/saldo a pagar entre pessoas nesta versão.

## Backend e deploy

- Banco de produção: Neon PostgreSQL; migrations Flyway V1–V7 executam ao iniciar.
- Hospedagem: Render, plano Free, Docker. O serviço escuta `PORT` automaticamente.
- A URL JDBC do Neon deve seguir o padrão `jdbc:postgresql://host/neondb?sslmode=require&channelBinding=require`, com usuário e senha em variáveis separadas.
- O projeto também contém um conversor para a URL crua `postgresql://...`, mas manter o formato JDBC explícito no Render é mais simples.

## Verificação antes de começar o frontend

1. Abrir o Swagger da produção e testar registro, login e uma rota autenticada.
2. Confirmar se o token fica válido por 24 horas (configuração atual).
3. Configurar `CORS_ALLOWED_ORIGINS` no backend com o domínio final antes de publicar a interface.
4. Criar o arquivo `.env` do frontend com `VITE_API_URL`; não colocar credenciais do banco nele.
