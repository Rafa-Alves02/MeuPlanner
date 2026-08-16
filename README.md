# MeuPlanner

Controle financeiro pessoal em desktop: contas, entradas, gastos, metas de poupança, alertas de limite por categoria e categorização automática de lançamentos via IA local.

- **Interface:** JavaFX (telas em FXML), base [AtlantaFX](https://github.com/mkpaz/atlantafx) (Primer Dark) com um skin próprio por cima (`style.css`) no visual DedSec — dark, neon ciano/amarelo/rosa, cantos retos, inspirado no menu do Watch Dogs 2
- **Persistência:** MySQL via JDBC puro, pool de conexões HikariCP
- **Build:** Maven

## Pré-requisitos

- **JDK 25** — `java -version` deve mostrar 25 ou superior
- **Maven 3.9+**
- **Docker** (recomendado, pra subir o MySQL) ou uma instância de MySQL 8 já rodando

## Subindo o banco

Com Docker instalado, na raiz do projeto:

```bash
docker compose up -d
```

Isso sobe um MySQL 8.3 na porta `3306`, cria o banco `meuplanner` e já roda `src/database/schema.sql` (tabelas + categorias padrão) na primeira inicialização.

Se preferir usar um MySQL que você já tem instalado, crie o banco e rode o schema manualmente:

```bash
mysql -u root -p < src/database/schema.sql
```

## Configurando a conexão

As credenciais do banco ficam em `src/main/resources/application.properties`, com override por variável de ambiente (variável em maiúsculas, pontos viram `_` — ex.: `db.password` → `DB_PASSWORD`).

Pra rodar contra o `docker compose` acima (senha padrão `root`):

```bash
export DB_PASSWORD=root
```

Se você mudou `MYSQL_ROOT_PASSWORD` no `docker-compose.yml` ou está usando outro MySQL, ajuste `DB_PASSWORD` (e `DB_URL`/`DB_USER` se necessário) de acordo.

## Rodando o app

```bash
mvn javafx:run
```

Na primeira vez, a tela de login não vai ter nenhum usuário cadastrado ainda — usa o botão **"Criar conta"** ali mesmo pra criar o primeiro (username + senha, mínimo 6 caracteres). A senha é guardada com hash bcrypt (`at.favre.lib:bcrypt`), nunca em texto puro.

## Trocando o tema visual

O tema base é aplicado uma vez em `MainApp.start()` via `Application.setUserAgentStylesheet(...)`. O AtlantaFX traz outras opções prontas em `atlantafx.base.theme` (`PrimerLight`, `NordLight`, `NordDark`, `CupertinoLight`, `CupertinoDark`, `Dracula`) — trocar é só importar a classe desejada e usá-la nesse mesmo `setUserAgentStylesheet`. Os estilos específicos do app (navbar, cards, botões, tabelas) continuam em `src/main/resources/css/style.css`, aplicado por cima do tema.

A cor de destaque (usada no item de menu ativo, botões primários e bordas em foco) é ajustável em tempo real pelo seletor de cor na navbar — a escolha é salva em `~/.meuplanner/ui.properties` e volta a valer na próxima abertura do app.

## Estrutura da interface (shell + conteúdo)

A janela é montada uma única vez em `SceneManager.init()` a partir de `fxml/app-shell.fxml` (navbar no topo, área de conteúdo no centro, rodapé embaixo) — a navbar e o rodapé nunca são recriados. `SceneManager.navegarPara(tela)` só troca o conteúdo central, com um crossfade suave em vez de recarregar a janela inteira; isso também evita o "piscar" que acontecia quando cada tela recriava a `Scene` do zero. Cada arquivo em `fxml/` (exceto `app-shell.fxml`, `navbar.fxml`, `login.fxml` e `ofx-importar.fxml`, que são a moldura, a tela de login e um diálogo modal) é só o conteúdo de uma página, sem `BorderPane`/sidebar própria.

A navbar tem dois grupos: **// Contas** (Contas & Bancos, Lançamentos, Metas) e **// Meu Perfil** (Meu Perfil, Relatórios, Gerenciar Categorias, Sair) — cada um é um `MenuButton` com submenu em vez de um botão por tela, pra não lotar a barra.

## Login

`MainApp` mostra `login.fxml` antes de qualquer coisa — só depois de autenticar (ou criar conta) é que `SceneManager.init()` monta o shell e navega pro dashboard. A sessão atual fica em `SessaoAtual` (em memória, reseta ao reabrir o app). O item "Sair" no menu "Meu Perfil" da navbar encerra a sessão e volta pro login sem fechar o app.

Hoje o login é só uma porta de entrada — os dados financeiros (contas, lançamentos, etc.) ainda não são segregados por usuário no banco. Se isso virar necessário (mais de uma pessoa usando o mesmo banco), é um passo à parte: adicionar `usuario_id` nas tabelas e filtrar as queries por usuário logado.

## Categorização por IA (com memória)

Na tela de Importar OFX, o botão "Sugerir categorias (IA)" chama um modelo Llama local (via Ollama, veja `OllamaClient`/`CategorizacaoIAService`) pra sugerir a categoria de cada lançamento. Se a sugestão estiver errada, o botão "Corrigir" na própria linha deixa escolher a categoria certa — essa correção fica salva em `categorizacao_aprendida` (chave: descrição normalizada, sem números) e, da próxima vez que aparecer uma transação parecida, o sistema usa a categoria que você ensinou direto, sem nem chamar a IA de novo. É por isso que não existe mais uma tela dedicada de "Categorias" na navbar principal — o fluxo de categorizar acontece no import, e o cadastro/gerenciamento de categorias (criar, editar cor, excluir) ficou dentro de "Meu Perfil → Gerenciar Categorias".

## Open Finance (Pluggy)

Cada conta pode ser conectada a um banco de verdade via [Pluggy](https://www.pluggy.ai/) — sem precisar mais baixar e importar OFX manualmente. Usa o fluxo **Meu Pluggy** (gratuito, sem prazo de expiração, pra conectar contas do seu próprio CPF).

1. Crie uma conta em [meu.pluggy.ai](https://meu.pluggy.ai) e pegue seu `CLIENT_ID`/`CLIENT_SECRET` no dashboard.
2. Configure as credenciais como variável de ambiente (nunca commitadas):
   ```bash
   export PLUGGY_CLIENT_ID=...
   export PLUGGY_CLIENT_SECRET=...
   ```
3. Na tela de Contas, clique em **"Open Finance"** na linha da conta → abre o widget de conexão do banco no navegador padrão. Depois de conectar, o Pluggy mostra um **Item ID** — cole ele de volta no app e escolha, na lista, qual conta do banco corresponde a essa conta do MeuPlanner.
4. Uma vez vinculada, o botão vira **"Sincronizar"**: busca as transações mais recentes direto da API do Pluggy e abre a mesma tela de revisão/categorização por IA usada no import de OFX (reaproveita `OfxImportService`/`CategorizacaoIAService` — a dedicação de duplicatas usa o mesmo mecanismo de `fitid_ofx`, guardando o id da transação do Pluggy).

Sem as credenciais configuradas, o botão "Open Finance" mostra um erro explicando o que falta — o resto do app funciona normal.

**Migração manual pra quem já tem o banco criado:**
```sql
ALTER TABLE contas ADD COLUMN pluggy_item_id VARCHAR(64);
ALTER TABLE contas ADD COLUMN pluggy_account_id VARCHAR(64);
```

## Rodando os testes

```bash
mvn test
```

## Estrutura do projeto

```
src/main/java/br/com/MeuPlanner/
├── app/          telas (Controllers + SceneManager) e MainApp
├── config/       AppConfig, ConnectionFactory, TransactionManager
├── exception/    exceções de domínio
├── model/        entidades (Conta, Categoria, Entrada, Gasto, Meta, ...)
├── ofx/          parser de extrato OFX
├── pluggy/       cliente da API do Pluggy (Open Finance)
├── repository/   acesso a dados (JDBC), um por entidade
├── service/      regra de negócio
└── strategy/     estratégias de parcelamento

src/main/resources/
├── fxml/         uma tela por controller
├── css/style.css
└── application.properties

src/database/schema.sql   DDL + seed de categorias padrão
```

## Empacotando um executável

Pra gerar um instalador nativo (`.exe`/`.dmg`/`.deb`) em vez de rodar via Maven, use o `jpackage` do próprio JDK depois de compilar o projeto — não há task de empacotamento configurada no `pom.xml` ainda.
