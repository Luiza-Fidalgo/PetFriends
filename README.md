# 🐾 PetFriends — Microsserviços com DDD e Eventos de Domínio

Projeto acadêmico demonstrando a transformação de um monólito em **microsserviços**,
aplicando **Domain-Driven Design (DDD)** e **comunicação assíncrona orientada a eventos**
(event-driven) com **RabbitMQ**.

Dois microsserviços implementados:

| Microsserviço | Responsabilidade | Porta |
|---|---|---|
| **PetFriends_Almoxarifado** | Controla o **estoque** e separa os produtos do pedido | `8081` |
| **PetFriends_Transporte** | Cuida da **entrega** do pedido até o cliente | `8082` |

---

## 📑 Índice

1. [Visão geral da arquitetura](#-1-visão-geral-da-arquitetura)
2. [Como executar](#-2-como-executar)
3. [Como testar (logs visuais)](#-3-como-testar-logs-visuais)
4. [Parte 1 — DDD e Decomposição](#-parte-1--ddd-e-decomposição)
5. [Parte 2 — Eventos de Domínio (perguntas conceituais)](#-parte-2--eventos-de-domínio-perguntas-conceituais)
6. [Parte 3 — Comunicação Assíncrona (event-driven)](#-parte-3--comunicação-assíncrona-event-driven)
7. [Estrutura de pastas](#-estrutura-de-pastas)

---

## 🏗️ 1. Visão geral da arquitetura

O fluxo de um pedido passa por dois eventos de domínio publicados pelo
**PetFriends_Pedidos** (que aqui é simulado, pois o foco são os dois microsserviços abaixo):

```
                     PetFriends_Pedidos
                            │
        ┌───────────────────┴───────────────────┐
        │ evento: pedido.confirmado              │ evento: pedido.despachado
        ▼                                        ▼
┌──────────────────┐                   ┌──────────────────┐
│   ALMOXARIFADO   │                   │    TRANSPORTE    │
│  separa o estoque│                   │  cria a entrega  │
└──────────────────┘                   └──────────────────┘
        ▲                                        ▲
        └──────────────  RabbitMQ  ──────────────┘
               (mensageria assíncrona)
```

> Os eventos trafegam pelo **RabbitMQ**. Cada microsserviço escuta apenas o evento que
> lhe interessa, usando uma *routing key* diferente.

**Tecnologias:** Java 21 · Spring Boot 3.3 · Spring Data JPA · RabbitMQ (Spring AMQP) · H2 · Docker.

---

## ▶️ 2. Como executar

**Pré-requisitos:** apenas **Docker** instalado e rodando. (Não precisa instalar Java nem Maven — o Docker cuida disso.)

Abra o terminal na pasta do projeto e rode **um único comando**:

```powershell
docker compose up -d --build
```

Isso sobe os **3 contêineres** de uma vez:
- `petfriends-rabbitmq` (mensageria)
- `petfriends-almoxarifado` (porta 8081)
- `petfriends-transporte` (porta 8082)

> 💡 O `-d` deixa rodando em segundo plano (libera o terminal).
> Use `--build` **apenas na primeira vez** ou quando alterar o código Java.

**Verificar se subiu:**
```powershell
docker compose ps
```

**Ver os logs ao vivo dos dois serviços juntos** (opcional):
```powershell
docker compose logs -f
```
*(Ctrl+C aqui só para de acompanhar os logs — não derruba os contêineres.)*

**Desligar tudo no final:**
```powershell
docker compose down
```

---

## 🧪 3. Como testar (logs visuais)

Com os serviços no ar, dispare os eventos com o script pronto:

```powershell
.\testar.ps1            # testa os dois microsserviços
.\testar.ps1 almox      # testa só o Almoxarifado
.\testar.ps1 transp     # testa só o Transporte
```

O script chama os endpoints de teste e mostra os **logs visuais** de volta. Saída esperada:

**Almoxarifado** (separação de estoque):
```
📨 [ALMOXARIFADO] Evento recebido! Pedido: PED-bb94
   Itens a separar: 2
📦 [DOMINIO] Tentando reservar 2 un de 'Racao Premium Caes' (disponivel: 10 un)
✅ [DOMINIO] Reservado! Novo saldo de 'Racao Premium Caes': 8 un
📦 [DOMINIO] Tentando reservar 1 un de 'Bolinha Mordedor' (disponivel: 5 un)
✅ [DOMINIO] Reservado! Novo saldo de 'Bolinha Mordedor': 4 un
🏁 [ALMOXARIFADO] Pedido PED-bb94 separado com sucesso!
```

**Transporte** (criação da entrega):
```
📨 [TRANSPORTE] Evento recebido! Pedido: PED-6988
🚚 [DOMINIO] Entrega criada para pedido PED-6988 -> Rua das Flores, 123 - Sao Paulo (CEP 01001-000)
🏁 [TRANSPORTE] Entrega registrada (id=4871b572-...) para Maria Silva
```

> Também é possível disparar pelo navegador:
> - http://localhost:8081/teste/confirmar-pedido
> - http://localhost:8082/teste/despachar-pedido
>
> E inspecionar o banco em memória em http://localhost:8081/h2-console
> (JDBC URL: `jdbc:h2:mem:almoxarifado`, usuário `sa`, senha em branco).

---

## 📦 Parte 1 — DDD e Decomposição

> *Objetivo: transformar monólitos em microsserviços eficazes, aplicando princípios de DDD.*

Em DDD, um **Agregado** é um conjunto de objetos tratado como uma unidade, com uma **raiz
(Aggregate Root)** que garante as regras de negócio. Cada microsserviço tem o seu agregado
mais representativo:

### 🔹 Almoxarifado → Agregado `ItemEstoque`

O Almoxarifado existe para **controlar o estoque**, então o item de estoque é o coração do domínio.

| Conceito | Classe | Por quê |
|---|---|---|
| **Entity / Aggregate Root** | [`ItemEstoque`](almoxarifado/src/main/java/com/petfriends/almoxarifado/domain/ItemEstoque.java) | Tem identidade (`sku`), ciclo de vida (quantidade muda) e as regras (`reservar`, `repor`) |
| **Value Object** | [`Quantidade`](almoxarifado/src/main/java/com/petfriends/almoxarifado/domain/Quantidade.java) | Sem identidade, **imutável**, comparado por valor, valida-se (não aceita negativo) |
| **Repository** | [`ItemEstoqueRepository`](almoxarifado/src/main/java/com/petfriends/almoxarifado/domain/ItemEstoqueRepository.java) | "Porta" para salvar/buscar o agregado (Spring Data JPA) |

### 🔹 Transporte → Agregado `Entrega`

O Transporte existe para **levar o pedido ao cliente**, então a entrega é o centro do domínio
(seus estados seguem o Diagrama 1: *Em Trânsito → Entregue / Devolvido / Extraviado*).

| Conceito | Classe | Por quê |
|---|---|---|
| **Entity / Aggregate Root** | [`Entrega`](transporte/src/main/java/com/petfriends/transporte/domain/Entrega.java) | Tem identidade (`id`), ciclo de vida (status) e regras de transição de estado |
| **Value Object** | [`Endereco`](transporte/src/main/java/com/petfriends/transporte/domain/Endereco.java) | Sem identidade, **imutável**, comparado por valor, valida o CEP |
| **Repository** | [`EntregaRepository`](transporte/src/main/java/com/petfriends/transporte/domain/EntregaRepository.java) | Salva/busca o agregado `Entrega` |

> **Entity vs Value Object (resumo para a banca):**
> *Entity* tem **identidade** e muda ao longo do tempo (dois itens com o mesmo nome são
> diferentes). *Value Object* não tem identidade, é **imutável** e dois com os mesmos
> valores são considerados **iguais**.

---

## 📨 Parte 2 — Eventos de Domínio (perguntas conceituais)

> *Objetivo: projetar softwares usando "domain events".*

### ❓ 1. Que funcionalidade síncrona executada pelo cliente é afetada pelos eventos de domínio?

O **acompanhamento (rastreamento) do status do pedido** na tela do PetFriends_Web.

O ReactJS consulta o status do pedido de forma **síncrona via REST** (*Em Preparação →
Em Trânsito → Entregue*). Mas esse status só avança porque o **Almoxarifado** e o
**Transporte** processam os eventos de domínio de forma **assíncrona**. Ou seja: a leitura
é síncrona, mas **o que ela mostra depende dos eventos já processados**. Por causa da
**consistência eventual**, pode haver um pequeno atraso entre o evento ocorrer e o cliente
ver a mudança na tela.

### ❓ 2. Diferença entre enviar só o ID do agregado vs. payload completo

| Critério | Só o **ID** | **Payload completo** |
|---|---|---|
| Tamanho da mensagem | Pequena | Maior |
| Acoplamento | **Maior** — o consumidor precisa "ligar de volta" via REST para buscar os dados | **Menor** — a mensagem já é autossuficiente |
| Dados sempre atuais? | Sim (busca na hora) | Não (é uma "foto" do momento do evento) |
| Resiliência | Pior (se a origem cair, trava) | Melhor (não depende da origem online) |

**Resumo:** *só o ID* → mensagem leve, porém gera chamada de volta e mais acoplamento;
*payload completo* → autossuficiente e desacoplado, ao custo de carregar mais dados (que
podem ficar desatualizados).

### ❓ 3. Como projetaria o evento Pedidos → Almoxarifado?

Com **payload contendo os itens**, pois o Almoxarifado precisa dar baixa no estoque sozinho
(só o ID o obrigaria a chamar o Pedidos de volta):

```json
PedidoConfirmadoEvent {
  "pedidoId": "PED-001",
  "itens": [
    { "sku": "RACAO-001", "quantidade": 2 },
    { "sku": "BRINQ-002", "quantidade": 1 }
  ]
}
```
📄 [`PedidoConfirmadoEvent.java`](almoxarifado/src/main/java/com/petfriends/almoxarifado/event/PedidoConfirmadoEvent.java)

### ❓ 4. Como projetaria o evento Pedidos → Transporte?

Com **payload contendo o destino**, pois o Transporte precisa saber para onde levar (esses
dados reconstroem o Value Object `Endereco`):

```json
PedidoDespachadoEvent {
  "pedidoId": "PED-001",
  "clienteNome": "Maria Silva",
  "rua": "Rua das Flores",
  "numero": "123",
  "cidade": "Sao Paulo",
  "cep": "01001-000"
}
```
📄 [`PedidoDespachadoEvent.java`](transporte/src/main/java/com/petfriends/transporte/event/PedidoDespachadoEvent.java)

---

## ⚙️ Parte 3 — Comunicação Assíncrona (event-driven)

> *Objetivo: desenvolver microsserviços event-driven com comunicação assíncrona.*

A mensageria usa **RabbitMQ**. Em cada microsserviço há **duas peças**:

### 🔧 Classe de configuração (recebe os eventos)

Monta o "encanamento" do RabbitMQ: **Exchange** (central de correios) + **Queue** (caixa de
correio do serviço) + **Binding** (regra que liga os dois pela *routing key*).

A exchange `pedidos.exchange` é a mesma; o que muda é a *routing key*, garantindo que cada
serviço receba só o que lhe interessa:

| Microsserviço | Escuta a routing key | Classe |
|---|---|---|
| Almoxarifado | `pedido.confirmado` | [`RabbitConfig`](almoxarifado/src/main/java/com/petfriends/almoxarifado/messaging/RabbitConfig.java) |
| Transporte | `pedido.despachado` | [`RabbitConfig`](transporte/src/main/java/com/petfriends/transporte/messaging/RabbitConfig.java) |

### 🛎️ Serviço que recebe os eventos (listener)

Método anotado com `@RabbitListener` que roda **automaticamente** quando chega a mensagem e
aplica a regra de negócio no agregado:

| Microsserviço | O que faz ao receber | Classe |
|---|---|---|
| Almoxarifado | Para cada item, busca o estoque e **reserva** a quantidade | [`PedidoEventListener`](almoxarifado/src/main/java/com/petfriends/almoxarifado/messaging/PedidoEventListener.java) |
| Transporte | Monta o `Endereco` e **cria a Entrega** (nasce *Em Trânsito*) | [`PedidoEventListener`](transporte/src/main/java/com/petfriends/transporte/messaging/PedidoEventListener.java) |

---
## ⚙️ Parte 4 — Testes e Observabilidade
a) O que é um Gateway de Serviço?

O Gateway de Serviço é o ponto de entrada das requisições em uma arquitetura de microsserviços. Ele recebe as chamadas do cliente e encaminha para o serviço responsável.

Suas vantagens incluem centralizar autenticação, logs e controle de requisições. Como desvantagem, pode se tornar um gargalo ou ponto único de falha.

b) O que é ID de Correlação?

É um identificador único utilizado para acompanhar uma requisição durante sua passagem por diferentes serviços.

Para funcionar corretamente, o ID deve ser criado na entrada do sistema, propagado em chamadas HTTP e mensagens assíncronas e incluído nos logs de todos os serviços.

c) Qual é a função do Micrometer e sua relação com o Zipkin?

O Micrometer coleta métricas e informações de rastreamento em aplicações Java e Spring.

O Micrometer Tracing gera os spans das requisições e os envia ao Zipkin, que apresenta visualmente o caminho e o tempo gasto em cada serviço.

d) O que é um Agregador de Logs?

É uma ferramenta que reúne os logs de todos os microsserviços em um único local.

Isso facilita pesquisas, monitoramento e investigação de erros. Um exemplo é o ELK Stack, formado por Elasticsearch, Logstash e Kibana.

Como desvantagens, exige infraestrutura adicional, padronização dos logs e pode gerar custos elevados de armazenamento.

---

## 📂 Estrutura de pastas

```
Sistema Pet/
├── docker-compose.yml          # sobe os 3 contêineres juntos
├── testar.ps1                  # dispara os eventos e mostra os logs visuais
├── README.md                   # este arquivo
│
├── almoxarifado/               # microsserviço PetFriends_Almoxarifado
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/
│       ├── resources/application.yml
│       └── java/com/petfriends/almoxarifado/
│           ├── AlmoxarifadoApplication.java   # main + carga inicial de estoque
│           ├── domain/                        # ItemEstoque, Quantidade, Repository
│           ├── event/                         # PedidoConfirmadoEvent
│           └── messaging/                     # RabbitConfig, PedidoEventListener, TesteController
│
└── transporte/                 # microsserviço PetFriends_Transporte
    ├── Dockerfile
    ├── pom.xml
    └── src/main/
        ├── resources/application.yml
        └── java/com/petfriends/transporte/
            ├── TransporteApplication.java     # main
            ├── domain/                        # Entrega, Endereco, StatusEntrega, Repository
            ├── event/                         # PedidoDespachadoEvent
            └── messaging/                     # RabbitConfig, PedidoEventListener, TesteController
```

---

### ⚡ Resumo rápido

```powershell
docker compose up -d --build     # 1. sobe tudo
.\testar.ps1                     # 2. dispara e vê os logs visuais
docker compose down              # 3. desliga no final
```
