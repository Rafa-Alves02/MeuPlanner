# MeuPlanner

Controle financeiro pessoal em desktop: contas, entradas, gastos, transferências entre contas, metas de poupança e alertas de limite por categoria.

- **Interface:** JavaFX (telas em FXML), tema base [AtlantaFX](https://github.com/mkpaz/atlantafx) (Primer Dark)
- **Persistência:** MySQL via JDBC puro, pool de conexões HikariCP
- **Build:** Maven

## Pré-requisitos

- **JDK 25** — `java -version` deve mostrar 25 ou superior
- **Maven 3.9+**
- **Docker** (recomendado, pra subir o MySQL) ou uma instância de MySQL 8 já rodando
- **[Ollama](https://ollama.com)** (opcional) — só necessário pra usar a sugestão de categoria por IA na importação de OFX. Sem ele instalado, o resto do app funciona normal, só o botão "Sugerir categorias (IA)" que vai dar erro de conexão.

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

## Login

`MainApp` mostra `login.fxml` antes de qualquer coisa — só depois de autenticar (ou criar conta) é que `SceneManager.init()` monta o shell e navega pro dashboard. A sessão atual fica em `SessaoAtual` (em memória, reseta ao reabrir o app). O botão "Sair" na navbar encerra a sessão e volta pro login sem fechar o app.

Hoje o login é só uma porta de entrada — os dados financeiros (contas, lançamentos, etc.) ainda não são segregados por usuário no banco. Se isso virar necessário (mais de uma pessoa usando o mesmo banco), é um passo à parte: adicionar `usuario_id` nas tabelas e filtrar as queries por usuário logado.

## Categorização por IA (Ollama)

Na tela de importar OFX, o botão **"Sugerir categorias (IA)"** manda a descrição de cada transação nova pro [Ollama](https://ollama.com) rodando localmente e pede pra ele escolher, entre as categorias já cadastradas no seu banco, a que mais combina. Nenhum dado financeiro sai da sua máquina — o modelo roda 100% local.

Setup:

```bash
# instala o Ollama (Windows: winget install Ollama.Ollama)
ollama pull llama3.2
```

`OllamaClient` fala com `http://localhost:11434` (porta padrão do Ollama) usando o modelo `llama3.2` — pra trocar o modelo, edita a constante `MODELO` em `OllamaClient.java`. Se a resposta da IA não bater com o nome de nenhuma categoria existente, a transação fica sem categoria (você classifica na mão depois, na tela de Lançamentos) — o app nunca cria uma categoria nova sozinho a partir do que a IA responde.

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
