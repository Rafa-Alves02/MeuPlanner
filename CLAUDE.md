# MeuPlanner

Controle financeiro pessoal desktop — JavaFX + MySQL via JDBC puro (sem ORM). Ver README.md para como rodar.

## Como trabalhar neste projeto

Isto não é um MVP descartável — é pra ficar bem arquitetado, mesmo sendo projeto pessoal. Ao mexer em qualquer parte do código:

**Clean code**
- Sem comentários explicando o que o código faz — nome de classe/método/variável já diz isso. Só comente uma decisão genuinamente não-óbvia (uma invariante escondida, um workaround pontual), e mesmo assim em uma linha.
- Sem abstração especulativa: não crie interface, flag ou camada extra para um caso de uso hipotético futuro. Resolva o problema que existe agora.
- Métodos e classes pequenos, um motivo pra existir cada um.

**Design patterns já em uso — siga-os, não reimplemente do zero**
- **Repository** (`repository/`): uma classe por entidade, estende `BaseRepository`, usa `RowMapper`/`StatementSetter`. Nunca monte JDBC bruto fora daqui.
- **Unit of Work** (`config/TransactionManager`): qualquer operação que escreva em mais de uma tabela (ex.: lançamento que também atualiza saldo de conta) precisa rodar dentro de `TransactionManager.executeInTransaction(...)`. Isso não é opcional — é o que garante que saldo e histórico nunca fiquem dessincronizados.
- **Strategy** (`strategy/`): regras de cálculo que podem variar (hoje só parcelamento) viram uma implementação de interface, não um `if/else` espalhado pelo service.
- **Camadas**: `app` (UI/Controllers) → `service` (regra de negócio) → `repository` (dados) → `config` (infra). Controller nunca fala com repository direto; repository nunca sabe que JavaFX existe.

**DDD, quando fizer sentido**
- Hoje o domínio é simples o bastante pra service concentrar a regra de negócio (anemic model) — está OK, não force um redesenho pra "ter DDD".
- Se uma regra ficar espalhada em vários services ou repetida (ex.: cálculo de saldo, validação de limite), é sinal de mover esse comportamento para dentro da entidade (`model/`) em vez de mais um método de service.
- Novos conceitos de negócio ganham nome de negócio (português, igual ao resto do domínio: `Meta`, `Alerta`, `Fechamento`) — não nomes técnicos genéricos.

**Testes**
- Toda regra de negócio nova (validação, cálculo, strategy) ganha teste unitário em `src/test/java`, sem precisar de banco — siga o padrão de `ParcelasIguaisStrategyTest`, `ContaServiceTest`, `MetaServiceTest`, `LancamentoServiceTest`.
- Repository/fluxo que depende do MySQL: teste de integração (feature), não pular a camada de dados sem cobertura. Se for adicionar teste de repository, usar Testcontainers (subir um MySQL descartável) em vez de mockar JDBC.
- Não considere uma mudança de regra de negócio como terminada sem teste cobrindo o caminho feliz e pelo menos um caso de rejeição/erro.

**Antes de abrir PR**
- `mvn -q compile -Dmaven.compiler.release=21` e `mvn test` limpos (o `-Dmaven.compiler.release=21` é só para sandboxes sem JDK 25 instalado — não altere o `pom.xml` por causa disso).
