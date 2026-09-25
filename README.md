## Integrantes do grupo park adventure
* Caue Marques e Silva - caue.marques@ufrpe.br
* Edivan José da Silva Júnior - edivan.jose@ufrpe.br
* Humberto Severino do Nascimento Neto - humberto.neto@ufrpe.br
* João Victor Maciel Gomes da Silva - joaovictormaciell43@gmail.com
* Victor Mendes Ferreira de Lima - victormendes6767@gmail.com

# Sistema ParkAdventure

## Descrição

Sistema para gestão de um parque temático/aquático, controlando atrações, visitantes, emissão de ingressos e o acesso às atrações por meio de fila virtual. O sistema deve respeitar restrições de altura e idade mínima por atração, além de controlar a capacidade simultânea de cada uma.

Como diferencial, o preço dos ingressos é ajustado dinamicamente em datas de feriado nacional, consultadas em tempo real via API pública, apoiando também o planejamento de escala de operadores em dias de maior movimento.

## Requisitos Funcionais

### 1. Cadastro de Atrações
- REQ01: Cadastrar atrações (nome, capacidade simultânea máxima, altura mínima em cm, idade mínima)
- REQ02: Implementar herança para tipos de atração: Radical, Aquática e Infantil
- REQ03: Vincular operadores responsáveis a cada atração

### 2. Visitantes e Ingressos
- REQ04: Cadastrar visitantes (nome, data de nascimento, altura em cm)
- REQ05: Emitir ingresso para um visitante, com tipo (Diária ou Passe Anual) e data de validade
- REQ06: Consultar quantidade de ingressos emitidos para uma determinada data

### 3. Precificação Dinâmica (API Pública)
- REQ07: Consultar a lista de feriados nacionais do ano vigente usando a API pública **BrasilAPI (Feriados Nacionais)**
- REQ08: Aplicar automaticamente um acréscimo percentual configurável no preço do ingresso Diária quando a data de emissão coincidir com um feriado nacional
- REQ09: Exibir no cadastro de emissão de ingresso um aviso visual quando a data escolhida for feriado

### 4. Controle de Acesso às Atrações
- REQ10: Registrar acesso de um visitante a uma atração (composição: fila virtual), vinculando ingresso e atração
- REQ11: Consultar ocupação atual (em tempo real) de uma atração, comparando com sua capacidade máxima
- REQ12: Liberar vaga na atração quando o visitante finaliza o uso

### 5. Relatórios e Escala
- REQ13: Relatório de atrações mais acessadas por período, exportável em CSV
- REQ14: Relatório de receita de ingressos por mês, destacando os dias de feriado
- REQ15: Listar operadores escalados para os próximos feriados nacionais (via API)

### 6. Regras e Restrições
- REQ16: **Bloquear** o acesso de um visitante a uma atração Radical quando sua altura for inferior à altura mínima exigida
- REQ17: **Impedir** o acesso a uma atração quando sua idade, calculada pela data de nascimento, for inferior à idade mínima
- REQ18: **Não permitir** registro de acesso a uma atração que já esteja em sua capacidade máxima simultânea
- REQ19: **Bloquear emissão** de ingresso Diária além da capacidade total diária do parque (soma das capacidades das atrações)
- REQ20: **Validar** que o ingresso esteja dentro da validade antes de autorizar qualquer acesso a atração
- REQ21: **Garantir** que toda atração tenha ao menos um operador responsável vinculado antes de ser aberta ao público

## Possíveis APIs/Bibliotecas

JavaFX, BrasilAPI — Feriados Nacionais (https://brasilapi.com.br/docs#tag/Feriados-Nacionais, pública e sem chave), `java.net.http.HttpClient`, Jackson/Gson, Java Time API, JUnit.

**Requisito bônus (opcional, fora da contagem oficial):** gerar uma página HTML estática com o calendário de feriados do ano e os dias de maior movimento previsto, aberta automaticamente no navegador via `Desktop.getDesktop().browse()`.